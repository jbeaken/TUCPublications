package org.bookmarks.service;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.bookmarks.website.domain.Address;
import org.bookmarks.controller.InvoiceSearchBean;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.Event;
import org.bookmarks.domain.Invoice;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;
import org.bookmarks.service.CustomerService;
import org.bookmarks.report.bean.InvoiceReportBean;
import org.bookmarks.repository.InvoiceRepository;
import org.bookmarks.repository.Repository;
import org.springframework.beans.factory.annotation.Value;
import org.bookmarks.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.format.number.CurrencyFormatter;
import org.springframework.format.number.PercentFormatter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvoiceServiceImpl extends AbstractService<Invoice> implements InvoiceService {

	@Autowired
	private StockItemService stockItemService;

	@Autowired
	private CustomerOrderLineService customerOrderLineService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private SaleService saleService;

	@Value("#{ applicationProperties['vatNumber'] }")
	private String vatNumber;

	@Override
	public Invoice get(Invoice e) {
		Invoice invoice = super.get(e);
		invoice.calculate(false);
		return invoice;
	}


	@Override
	public Invoice get(Long id) {
		Invoice invoice = super.get(id);
		invoice.calculate(false); //Assume has been overidden
		return invoice;
	}


	@Override
	@Transactional
	public void delete(Invoice invoice) {
		//Credit customer invoice amount
		BigDecimal creditAmount = invoice.getTotalPrice().add(invoice.getSecondHandPrice()).add(invoice.getServiceCharge());

		customerService.debitAccount(invoice.getCustomer(), creditAmount);


		//Place back into stock
		for(Sale sale : invoice.getSales()) {
			stockItemService.updateQuantityInStock(sale.getStockItem(), sale.getQuantity());
		}

		//delete from database, deletes invoice order lines and sales
		super.delete(invoice);
	}


	@Override
	public Repository<Invoice> getRepository() {
		return invoiceRepository;
	}


	@Override
	public void addStockItem(StockItem stockItem, Map<Long, Sale> orderLineMap, Invoice invoice) {
		//Check if this stock item is already in map
		Sale sale = orderLineMap.get(stockItem.getId());
		if(sale != null) {
			//Already there, increment
			sale.setQuantity(sale.getQuantity() + 1l);
		} else {
			//Not in map, put in
			sale = new Sale(stockItem, invoice);
			orderLineMap.put(stockItem.getId(), sale);
		}
		invoice.calculate(orderLineMap.values(), true);
	}


	@Override
	public void addStockItem(CustomerOrderLine col, Map<Long, Sale> orderLineMap, Invoice invoice) {
		StockItem stockItem = col.getStockItem();
		//Check if this stock item is already in map
		Sale sale = orderLineMap.get(stockItem.getId());
		if(sale != null) {
			//Already there, increment
			sale.setQuantity(sale.getQuantity() + col.getAmount());
		} else {
			//Not in map, put in
			sale = new Sale(stockItem, invoice);
			sale.setQuantity(col.getAmount());
			if(col.getSellPrice() != null) {
				sale.setSellPrice(col.getSellPrice()); //Override sell price with one from customer order line (if it's there)
			}
			orderLineMap.put(stockItem.getId(), sale);
		}
		invoice.calculate(orderLineMap.values(), true);
	}



	@Override
	public void addCustomerOrderLine(CustomerOrderLine customerOrderLine, Map<Long, Sale> orderLineMap,
			Invoice invoice) {
//		StockItem stockItem = customerOrderLine.getStockItem();
//		//Check if this stock item is already in map
//		Sale saleInMap = orderLineMap.get(stockItem.getId());
//		if(saleInMap != null) {
//			//Already there, increment
//			saleInMap.setQuantity(saleInMap.getQuantity() + customerOrderLine.getAmount());
//		} else {
//			//Not in map, put in
//			Sale invoiceOrderLine = new Sale(customerOrderLine, invoice.getCustomer());
//			orderLineMap.put(stockItem.getId(), invoiceOrderLine);
//		}

	}


//	@Override
//	public BigDecimal getPrice(Invoice invoice,	Map<Long, InvoiceOrderLine> orderLineMap) {
//		return getPrice(invoice, orderLineMap.values());
//	}

//	@Override
//	public BigDecimal getPrice(Invoice invoice) {
//		return getPrice(invoice, invoice.getInvoiceOrderLines());
//	}
//
//	public BigDecimal getPrice(Invoice invoice, Collection<InvoiceOrderLine> invoiceOrderLines) {
//		float totalPrice = 0;
//		for(InvoiceOrderLine invoiceOrderLine : invoiceOrderLines){
//			Float discount = invoiceOrderLine.getDiscount().floatValue();
//			float sellPrice = invoiceOrderLine.getStockItem().getSellPrice().floatValue();
//			long amount = invoiceOrderLine.getAmount();
//
//			BigDecimal price = new BigDecimal(sellPrice * (1 - discount));
//			price = price.setScale(2, BigDecimal.ROUND_HALF_DOWN);
//
//			totalPrice += (price.floatValue() * amount);
//			//Set values
//			invoiceOrderLine.setDiscount(new BigDecimal(discount));
//			invoiceOrderLine.setPrice(price);
//			invoiceOrderLine.setTotalPrice(new BigDecimal(price.floatValue() * amount).setScale(2, BigDecimal.ROUND_HALF_DOWN));
//		}
//		//Add any 2nd hand
//		if(invoice.getSecondHandPrice() != null) {
//			totalPrice += invoice.getSecondHandPrice().floatValue();
//		}
//		return new BigDecimal(totalPrice);
//	}

	/**
	 * update stock record if this isn't a proforma
	 */
	@Override
	@Transactional
	public void save(Invoice invoice, Collection<Sale> sales, Map<Long, CustomerOrderLine> customerOrderLineMap, Event event) {
//		Customer customer = customerService.get(invoice.getCustomer().getId());
//		invoice.setCustomer(customer);

		invoice.setSales(new HashSet<Sale>(sales));
		invoice.calculate(false);

		if(event != null) {
			for(Sale sale : sales) {
				sale.setEvent(event);
			}
		}

		//Debit customer account, check for customer having an account is already done in controller
//		customer.debitAccount(getPrice(invoice));
		if(invoice.getPaid() == false && invoice.getIsProforma() == false) {
			customerService.debitAccount(invoice.getCustomer(), invoice.getTotalPrice().multiply(new BigDecimal(-1)));
		}

		//This invoice may involve customer orders
		if(customerOrderLineMap != null) {
			Collection<CustomerOrderLine> customerOrderLines = customerOrderLineMap.values();
			invoice.setCustomerOrderLines(customerOrderLines);
			for(CustomerOrderLine customerOrderLine : invoice.getCustomerOrderLines()) {
				//Caution, can also be here if there is a customer order screen open same time as an invoice
				//How to tell??
				customerOrderLine.setInvoice(invoice);
				customerOrderLine.complete();
				customerOrderLineService.update(customerOrderLine);
			}
		}

		save(invoice); //Persists sales as well

		if(invoice.getUpdateStock() == Boolean.TRUE) {
			//Need to remove items from stock, but not for customer orders, need to remove quantityReadyForCustomer
			for(Sale sale : invoice.getSales()) {
				boolean isCustomerOrder = false;
				if(customerOrderLineMap != null) {
					for(CustomerOrderLine customerOrderLine :  customerOrderLineMap.values()) {
						if(customerOrderLine.getStockItem().getId().equals(sale.getStockItem().getId())) {
							isCustomerOrder = true;
							sale.setCustomerOrderLine(customerOrderLine);
							saleService.updateWithStockRecord(sale, false);
							break;
						}
					}
				}
				if(isCustomerOrder) {
					stockItemService.updateQuantities(sale.getStockItem(), null , null, null, sale.getQuantity() * -1, sale.getQuantity() * -1);
				} else {
					stockItemService.updateQuantities(sale.getStockItem(), sale.getQuantity() * -1, null, null, null, null);
				}
			}// end for
		}
	}



	@Override
	public Invoice getNewInvoice(Long customerId) {
		//Create invoice and attach customer
		Customer customer = customerService.get(customerId);
		Invoice invoice = new Invoice(customer);
		return invoice;
	}

	@Override
	public void addStockItem(Long stockItemId,
			Map<Long, Sale> orderLineMap, Invoice invoice) {
		StockItem stockItem = stockItemService.get(stockItemId);
		addStockItem(stockItem, orderLineMap, invoice);
	}




	@Override
	public void update(Invoice invoice, Collection<Sale> sales,
			Map<Long, CustomerOrderLine> customerOrderLineMap, BigDecimal originalInvoicePrice) {
		Customer customer = customerService.get(invoice.getCustomer().getId());
		invoice.setCustomer(customer);

		invoice.setSales(new HashSet<Sale>(sales));
		invoice.calculate(false);

		for(Sale sale : invoice.getSales()) {
			System.err.println(sale.getId());
		}

		//Debit customer account, does a customer have to have an account to generate an invoice?
//		customer.debitAccount(getPrice(invoice));
		customerService.debitAccount(invoice.getCustomer(), (invoice.getTotalPrice().subtract(originalInvoicePrice)).multiply(new BigDecimal(-1)));

		//This invoice may involve customer orders
		if(customerOrderLineMap != null) {
			Collection<CustomerOrderLine> customerOrderLines = customerOrderLineMap.values();
			invoice.setCustomerOrderLines(customerOrderLines);
			for(CustomerOrderLine customerOrderLine : invoice.getCustomerOrderLines()) {
				customerOrderLine.setInvoice(invoice);
				customerOrderLineService.update(customerOrderLine);
			}
		}

		update(invoice); //Persists invoice order lines
	}


	@Override
	public Collection<InvoiceReportBean> getInvoiceReportBeans(InvoiceSearchBean invoiceSearchBean) {
		return invoiceRepository.getInvoiceReportBeans(invoiceSearchBean);
	}


	@Override
	public List getAllForCsv() {
		return invoiceRepository.getAllForCsv();
	}

}
