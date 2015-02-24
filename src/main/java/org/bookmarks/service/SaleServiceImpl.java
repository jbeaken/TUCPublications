package org.bookmarks.service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import org.bookmarks.controller.SearchBean;
import org.bookmarks.controller.bean.SaleReportBean;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.Event;
import org.bookmarks.domain.InvoiceOrderLine;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.SaleOrReturn;
import org.bookmarks.domain.SaleOrReturnOrderLine;
import org.bookmarks.domain.StockItem;
import org.bookmarks.repository.Repository;
import org.bookmarks.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SaleServiceImpl extends AbstractService<Sale> implements SaleService{

	@Autowired
	private SaleRepository saleRepository;
	
	@Autowired
	private StockItemService stockItemService;
	
	@Autowired
	private SupplierOrderLineService supplierOrderLineService;

	public Sale sell(StockItem stockItem, Event event) {
		//Create sale
		Sale sale = getSale(stockItem, event);
		
		sell(sale);
		
		return sale;
	}
	
	private Sale getSale(StockItem stockItem, Event event) {
		Sale sale = new Sale();
		sale.setQuantity(1l);
		sale.setStockItem(stockItem);
		sale.setSellPrice(stockItem.getSellPrice());
		sale.setVat(stockItem.getVat());
		sale.setEvent(event);
		
		BigDecimal vatAmount = 
				sale.getSellPrice()
				.multiply(sale.getStockItem().getVat())
				.divide(new BigDecimal(100));	
		sale.setVatAmount(vatAmount);		
		return sale;
	}




	@Override
	public void sell(Sale sale) {
		//Update stock record
		stockItemService.updateQuantityInStock(sale.getStockItem(), sale.getQuantity() * -1);	
				
		//Reconcile keep in stock supplier order lines
		supplierOrderLineService.reconcileKeepInStock(sale.getStockItem(), true);
		
		//Persist
		save(sale);
	}	
	
//	@Override
//	public void sell(InvoiceOrderLine invoiceOrderLine) {
//		Sale sale = new Sale();
//		sale.setQuantity(invoiceOrderLine.getAmount());
//		sale.setStockItem(invoiceOrderLine.getStockItem());
//		sale.setSellPrice(invoiceOrderLine.getSellPrice().multiply(new BigDecimal(sale.getQuantity())));
//		sale.setVat(invoiceOrderLine.getVat());
//		invoiceOrderLine.setSale(sale);
//		
//		sell(sale);
//	}	
	@Override
	public void updateWithStockRecord(Sale sale, boolean updateStockRecord) {
		if(sale.getEvent() == null || sale.getEvent().getId() == null) {
			sale.setEvent(null); //Why do I have to do this
		}
		
		//TO-DO
		//Quick fix
		if(sale.getOriginalQuantity() == null) {
			sale.setOriginalQuantity(0l);
		}
		//Update stock record
		if(updateStockRecord) {
			stockItemService.updateQuantityInStock(sale.getStockItem(), (sale.getQuantity() - sale.getOriginalQuantity()) * -1);	
		}
		
		//Reconcile keep in stock supplier order lines
		supplierOrderLineService.reconcileKeepInStock(sale.getStockItem(), false);
		
		super.update(sale);
	}

	@Override
	public void update(Sale sale) {
		if(sale.getEvent() == null || sale.getEvent().getId() == null) {
			sale.setEvent(null); //Why do I have to do this
		}
		
		//TO-DO
		//Quick fix
		if(sale.getOriginalQuantity() == null) {
			sale.setOriginalQuantity(0l);
		}
		//Update stock record
		stockItemService.updateQuantityInStock(sale.getStockItem(), (sale.getQuantity() - sale.getOriginalQuantity()) * -1);
		
		//Reconcile keep in stock supplier order lines
		supplierOrderLineService.reconcileKeepInStock(sale.getStockItem(), false);
		
		super.update(sale);
	}

	@Override
	public void delete(Sale sale) {
		// Must also place back into stock
		sale = saleRepository.get(sale);
		
		StockItem stockItem = sale.getStockItem();
		
		//Update stock record
		stockItemService.updateQuantityInStock(stockItem, sale.getQuantity());
		
		//Reconcile keep in stock supplier order lines
		supplierOrderLineService.reconcileKeepInStock(sale.getStockItem(), false);
		
		super.delete(sale);
	}

	@Override
	public Repository<Sale> getRepository() {
		return saleRepository;
	}



	@Override
	public Collection<Sale> getAll(Date startDate, Date endDate) {
		return saleRepository.get(startDate, endDate);
	}
	
	@Override
	public Collection<Sale> get(Long stockItemID, Date startDate, Date endDate) {
		return saleRepository.get(stockItemID, startDate, endDate);
	}



	@Override
	public Collection<Sale> getFull(Date startDate, Date endDate) {
		return saleRepository.getFull(startDate, endDate);
	}

	//Called from customerOrderLineController.complete - customerOrderLineService.complete
	//This is non-account completion
	@Override
	public Sale sellCustomerOrder(CustomerOrderLine col) {
		Sale sale = getSale(col.getStockItem(), null);
		sale.setQuantity(col.getAmount());
		if(col.getSellPrice() != null) {
			sale.setSellPrice(col.getSellPrice());
		}
		sale.setCustomerOrderLine(col); //TO-DO not persisting
		stockItemService.updateQuantities(sale.getStockItem(), null, null, null, null, sale.getQuantity() * -1);
		save(sale);
		return sale;
	}

	@Override
	@Transactional
	public void sell(SaleOrReturn saleOrReturn) {
		//This could be an edit, check original amount sold
		for(SaleOrReturnOrderLine s : saleOrReturn.getSaleOrReturnOrderLines()) {
			Sale sale = getSale(s.getStockItem(), null);
			
			Long newAmountToSell = s.getAmountSold() - s.getOriginalAmountSold();
			Long newAmountWithCustomer = s.getOriginalAmountRemainingWithCustomer() - s.getAmountRemainingWithCustomer();
			
			sale.setQuantity(newAmountToSell);
			
			//Change quantities
			stockItemService.updateQuantities(s.getStockItem(), (newAmountWithCustomer - newAmountToSell) * 1, newAmountToSell * -1, null, null, null);
			if(newAmountToSell != 0l) {
				save(sale);
			}
		}
		
	}

	@Override
	public Long getTotalQuantityForPeriod(StockItem stockItem, Date startDate,
			Date endDate) {
		return saleRepository.getTotalQuantityForPeriod(stockItem, startDate, endDate);
	}


}
