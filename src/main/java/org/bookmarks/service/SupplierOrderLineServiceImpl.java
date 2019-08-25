package org.bookmarks.service;

import java.util.Collection;
import java.util.Date;

import org.bookmarks.controller.SupplierOrderLineSearchBean;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.CustomerOrderLineStatus;
import org.bookmarks.domain.Level;
import org.bookmarks.domain.Publisher;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.Supplier;
import org.bookmarks.domain.SupplierOrderLine;
import org.bookmarks.domain.SupplierOrderLineStatus;
import org.bookmarks.domain.SupplierOrderLineType;
import org.bookmarks.repository.Repository;
import org.bookmarks.repository.SupplierOrderLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SupplierOrderLineServiceImpl extends AbstractService<SupplierOrderLine> implements SupplierOrderLineService {

	@Autowired
	private SupplierOrderLineRepository supplierOrderLineRepository;

	@Autowired
	private CustomerOrderLineService customerOrderLineService;
	 
	@Autowired
	private StockItemService stockItemService;

	@Autowired
	private SupplierService supplierService;
	
	@Autowired
	private PublisherService publisherService;


	/**
	 * Using the search bean, send all sol with status ready_to_send to the supplier (status SENT_TO_SUPPLIER)
	 * and sendDate = now()
	 * 
	 * TO-DO
	 * If keep in stock amount has been edited, a new keep in stock with status ready to send may be needed
	 */
	@Override
	public void sendAllToSupplier(SupplierOrderLineSearchBean supplierOrderLineSearchBean) {
		Collection<SupplierOrderLine> supplierOrderLines = search(supplierOrderLineSearchBean);
		supplierOrderLineSearchBean.setExport(true);
		for(SupplierOrderLine sol : supplierOrderLines) {
			//If on hold, ignore
			//If ready to send, send
			//If sent, ignore
			SupplierOrderLineStatus status = sol.getSupplierOrderLineStatus();
			switch(status) {
				case ON_HOLD :
					//updateStatus(sol, SupplierOrderLineStatus.READY_TO_SEND);
					break;
				case READY_TO_SEND :
					sendToSupplier(sol);
					break;
				case SENT_TO_SUPPLIER :
					break;
			}
		}
	}

	@Override
	public void sendToSupplier(SupplierOrderLine supplierOrderLine) {
		//Update stockItem.quantityOnOrder
		stockItemService.updateQuantityOnOrderAbsolutely(supplierOrderLine.getStockItem(), supplierOrderLine.getAmount());
		
		/*if(supplierOrderLine.getType() == SupplierOrderLineType.KEEP_IN_STOCK) {
			reconcileKeepInStock(supplierOrderLine.getStockItem(), false);
		}*/
		
		//Update customerorderline status if necessary
		if(supplierOrderLine.getType() == SupplierOrderLineType.CUSTOMER_ORDER) {
			//Have to reget sol to get col details
			supplierOrderLine = get(supplierOrderLine.getId());
			CustomerOrderLine col = supplierOrderLine.getCustomerOrderLine();
			if(col.getStatus() != CustomerOrderLineStatus.COMPLETE && col.getStatus() != CustomerOrderLineStatus.CANCELLED) {
				col.setStatus(CustomerOrderLineStatus.ON_ORDER);
				col.setOnOrderDate(new Date());
				
			}
			
			//Add to the note
			setSendToSupplierNote(col, supplierOrderLine);

			//Persist it
			customerOrderLineService.update(col);
		}
		
		supplierOrderLine.setSupplierOrderLineStatus(SupplierOrderLineStatus.SENT_TO_SUPPLIER);
		supplierOrderLine.setSendDate(new Date());
		update(supplierOrderLine);
	}

	private void setSendToSupplierNote(CustomerOrderLine col, SupplierOrderLine supplierOrderLine) {
		String note = col.getNote();
		Supplier supplier = supplierService.get(supplierOrderLine.getSupplier().getId());
		if(note == null) note = ""; else note = note + "<br/><br/>";
		note = note + "Sent to " + supplier.getName() + " on " + new Date();
		if(supplierOrderLine.getUserInitials() != null) note = note + " by " + supplierOrderLine.getUserInitials();
		col.setNote(note);
	}
	
	private void setPendingOrderNote(CustomerOrderLine col, SupplierOrderLine supplierOrderLine) {
		String note = col.getNote();
		Supplier supplier = supplierService.get(supplierOrderLine.getSupplier().getId());
		if(note == null) note = ""; else note = note + "<br/><br/>";
		note = note + "Put into " + supplier.getName() + " basket on " + new Date() + " by " + supplierOrderLine.getUserInitials();
		col.setNote(note);
	}	

	@Override
	public void putAllOnHold(SupplierOrderLineSearchBean supplierOrderLineSearchBean) {
		Collection<SupplierOrderLine> supplierOrderLines = search(supplierOrderLineSearchBean);
		supplierOrderLineSearchBean.setExport(true);
		for(SupplierOrderLine sol : supplierOrderLines) {
			//If on hold, ignore
			//If ready to send, set to ON_HOLD
			//If sent, ignore
			SupplierOrderLineStatus status = sol.getSupplierOrderLineStatus();
			switch(status) {
				case ON_HOLD :
					break;
				case READY_TO_SEND :
					updateStatus(sol, SupplierOrderLineStatus.ON_HOLD);
					break;
				case SENT_TO_SUPPLIER :
					break;
			}
		}
	}
	
	@Override
	public void takeCustomerOrdersOffHold(SupplierOrderLineSearchBean supplierOrderLineSearchBean) {
		Collection<SupplierOrderLine> supplierOrderLines = search(supplierOrderLineSearchBean);
		supplierOrderLineSearchBean.setExport(true);
		for(SupplierOrderLine sol : supplierOrderLines) {
			//If on hold, ignore
			//If ready to send, set to ON_HOLD
			//If sent, ignore
			SupplierOrderLineStatus status = sol.getSupplierOrderLineStatus();
			switch(status) {
				case ON_HOLD :
					break;
				case READY_TO_SEND :
					updateStatus(sol, SupplierOrderLineStatus.ON_HOLD);
					break;
				case SENT_TO_SUPPLIER :
					break;
			}
		}
	}		
	
	/**
	 * From displayCreateSupplierOrderLine.jsp - SupplierOrderLineController.create()
	 * Creates a supplierorderline either from a customerorderline or not. 
	 */
	@Override
	public void create(SupplierOrderLine supplierOrderLine) {
		CustomerOrderLine col = supplierOrderLine.getCustomerOrderLine();
		
		if(col.getId() != null) { //Has customerorderline
			col = customerOrderLineService.get(col.getId()); //Have now got two supplierorderlines, dupplicate object exception possible
			if(col.getSupplierOrderLine() == null) {
				col.setSupplierOrderLine(createForCustomerOrder(col));
			}
			col.getSupplierOrderLine().setAmount(supplierOrderLine.getAmount());
			col.getSupplierOrderLine().setUserInitials(supplierOrderLine.getUserInitials());
			col.getSupplierOrderLine().setSupplier(supplierOrderLine.getSupplier());
			col.getSupplierOrderLine().setCustomerOrderLine(col); //Why to prevent DuplicateKeyException
			
			//Send directly or save as pending?
			if(supplierOrderLine.getSendDirectToSupplier()) {
				col.setStatus(CustomerOrderLineStatus.ON_ORDER);
				col.setOnOrderDate(new Date());
				col.getSupplierOrderLine().setSupplierOrderLineStatus(SupplierOrderLineStatus.SENT_TO_SUPPLIER); //Direct
				col.getSupplierOrderLine().setSendDate(new Date());
				
				setSendToSupplierNote(col, col.getSupplierOrderLine());
			} else {
				col.setStatus(CustomerOrderLineStatus.PENDING_ON_ORDER);
				col.getSupplierOrderLine().setSupplierOrderLineStatus(SupplierOrderLineStatus.READY_TO_SEND); //Pending

				setPendingOrderNote(col, col.getSupplierOrderLine());
			}
			
			customerOrderLineService.update(col);
			
		} else { //No customerorderline
			supplierOrderLine.setCustomerOrderLine(null);
			
			//Send directly or save as pending?
			if(supplierOrderLine.getSendDirectToSupplier()) {
				supplierOrderLine.setSupplierOrderLineStatus(SupplierOrderLineStatus.SENT_TO_SUPPLIER); //Direct
				supplierOrderLine.setSendDate(new Date());
			} else {
				supplierOrderLine.setSupplierOrderLineStatus(SupplierOrderLineStatus.READY_TO_SEND); //Pending
			}
			
			saveOrUpdate(supplierOrderLine);			
		}
		
		//Has preferred supplier been changed?
		if(supplierOrderLine.getChangePreferredSupplier()) {
			stockItemService.updatePreferredSupplier(supplierOrderLine.getStockItem(), supplierOrderLine.getSupplier());
		}
	}	

	@Override
	public SupplierOrderLine getByStockItemId(Long id) {
		return supplierOrderLineRepository.getByStockItemId(id);
	}	

	@Override
	public SupplierOrderLine getByStockItemId(Long id, SupplierOrderLineType type) {
		return supplierOrderLineRepository.getByStockItemId(id, type);
	}			


	@Override
	public void toggleOnHold(SupplierOrderLine supplierOrderLine) {
		if(supplierOrderLine.getSupplierOrderLineStatus() == SupplierOrderLineStatus.ON_HOLD){
			updateStatus(supplierOrderLine, SupplierOrderLineStatus.READY_TO_SEND);
		} else {
			updateStatus(supplierOrderLine, SupplierOrderLineStatus.ON_HOLD);
		}
	}
	
	@Override
	public void updateStatus(SupplierOrderLine supplierOrderLine, SupplierOrderLineStatus status) {
		supplierOrderLine.setSupplierOrderLineStatus(status);			
		update(supplierOrderLine);
	}
	
	@Override
	public Repository<SupplierOrderLine> getRepository() {
		return supplierOrderLineRepository;
	}

	@Override
	public SupplierOrderLine getByCustomerOrderLineId(Long customerOrderLineId) {
		return supplierOrderLineRepository.getByCustomerOrderLineId(customerOrderLineId);
	}

	@Override
	public void reconcileAllKeepInStockItems() {
		Collection<StockItem> keepInStockItems = stockItemService.getKeepInStockItems();
		
		supplierOrderLineRepository.deleteAllKeepInStockSupplierOrderLines(); //Reset

		for(StockItem stockItem : keepInStockItems) {
			reconcileKeepInStock(stockItem, false);
		}
	}

	/**
	 * From saleService.sell, stockItemService.saveOrUpdate, supplierdelivery.create
	 *  
	 *  Must ensure calling method has populated quantityToKeepInStock and quantityInStock
	 *  
	 *  stockItemService.delete is a special case - TO-DO
	 */
	@Override
	public void reconcileKeepInStock(StockItem stockItem, boolean isSale) {
		Long quantityToKeepInStock = stockItem.getQuantityToKeepInStock();
		Long quantityInStock = stockItem.getQuantityInStock();
		
		//Check if Keep in stock supplier order line is needed
		if(quantityToKeepInStock <= quantityInStock || quantityToKeepInStock == 0) {
			//No, it isn't
			//If isSale a kis supplier order line for this stock should not exist. 
			//Also, if is add new stock (stockItem.getId() == null) and quantityToKeepInStock == 0, it won't exist
			//Otherwise quantityInStock has incremented (supplier delivery?), so maybe filling a kis supplier order line, so delete
			if(!isSale && stockItem.getId() != null) {
				supplierOrderLineRepository.deleteKeepInStockSupplierOrderLine(stockItem);
			}
			return;
		}
		
		//Yes, it is 
		//Need to adjust or create Keep in stock supplier order line
		//Get quantityInStock but using a value of 0 for negative values
		Long inStockQuantity = (quantityInStock < 0 ? 0 : quantityInStock);
		
		SupplierOrderLine supplierOrderLine = supplierOrderLineRepository.getByStockItemId(stockItem.getId(), SupplierOrderLineType.KEEP_IN_STOCK);
		
		if(supplierOrderLine == null) {
			//Doesn't exist, so create new
			supplierOrderLine = new SupplierOrderLine();
			if(stockItem.getId() != null){
				stockItem = stockItemService.get(stockItem.getId()); //Need to refresh to get supplier details
			}
			supplierOrderLine.setStockItem(stockItem);
			supplierOrderLine.setType(SupplierOrderLineType.KEEP_IN_STOCK);
			supplierOrderLine.setPriority(stockItem.getKeepInStockLevel());
			setSupplier(supplierOrderLine, stockItem);
		}
		
		supplierOrderLine.setAmount(quantityToKeepInStock - inStockQuantity);
		
		saveOrUpdate(supplierOrderLine);
	}
	
	
	@Override
	public SupplierOrderLine createForCustomerOrder(CustomerOrderLine customerOrderLine) {
		StockItem stockItem = customerOrderLine.getStockItem();
		
		SupplierOrderLine supplierOrderLine = new SupplierOrderLine();
		supplierOrderLine.setPriority(Level.HIGH);
		supplierOrderLine.setType(SupplierOrderLineType.CUSTOMER_ORDER);
		supplierOrderLine.setStockItem(stockItem);
		supplierOrderLine.setCustomerOrderLine(customerOrderLine);
		setSupplier(supplierOrderLine, stockItem);
		return supplierOrderLine;
	}

	@Override
	public void removeSupplierOrderLine(StockItem stockItem, SupplierOrderLineType type) {
		supplierOrderLineRepository.removeSupplierOrderLine(stockItem, type);
	}

	@Override
	public void setSupplier(SupplierOrderLine supplierOrderLine, StockItem stockItem) {
		Supplier supplier = null;
		if(stockItem.getPreferredSupplier() == null) {
			if(stockItem.getPublisher().getSupplier() == null) {
				Publisher publisher = publisherService.get(stockItem.getPublisher().getId());
				supplier = publisher.getSupplier();
			} else {
				supplier = stockItem.getPublisher().getSupplier();
			}
		} else {
			supplier = stockItem.getPreferredSupplier();
		}			
		supplierOrderLine.setSupplier(supplier);
	}

	@Override
	public void takeAllOffHold(SupplierOrderLineSearchBean supplierOrderLineSearchBean) {
		Collection<SupplierOrderLine> supplierOrderLines = search(supplierOrderLineSearchBean);
		supplierOrderLineSearchBean.setExport(true);
		for(SupplierOrderLine sol : supplierOrderLines) {
			SupplierOrderLineStatus status = sol.getSupplierOrderLineStatus();
			switch(status) {
				case ON_HOLD :
					updateStatus(sol, SupplierOrderLineStatus.READY_TO_SEND);
					break;
				case READY_TO_SEND :
					break;
				case SENT_TO_SUPPLIER :
					break;
			}
		}	
	}

	@Override
	public void putCustomerOrdersOnHold(
			SupplierOrderLineSearchBean supplierOrderLineSearchBean) {
		// TODO Auto-generated method stub
		
	}
}
