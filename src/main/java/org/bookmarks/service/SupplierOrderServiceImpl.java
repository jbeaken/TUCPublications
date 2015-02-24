package org.bookmarks.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.CustomerOrderLineStatus;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.Supplier;
import org.bookmarks.domain.SupplierOrder;
import org.bookmarks.domain.SupplierOrderLine;
import org.bookmarks.domain.SupplierOrderLineStatus;
import org.bookmarks.domain.SupplierOrderStatus;
import org.bookmarks.repository.Repository;
import org.bookmarks.repository.SupplierOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupplierOrderServiceImpl extends AbstractService<SupplierOrder> implements SupplierOrderService {

	@Autowired
	private SupplierOrderRepository supplierOrderRepository;
	

	@Autowired
	private SupplierService supplierService;

	@Autowired
	private StockItemService stockItemService;
	
	@Autowired
	private SupplierOrderLineService supplierOrderLineService;

	@Autowired
	private CustomerOrderLineService customerOrderLineService;

//	@Override
//	@Transactional
//	public void toggleOnHold(SupplierOrderLine supplierOrderLine) {
//		if(supplierOrderLine.getSupplierOrderLineStatus() == SupplierOrderLineStatus.ON_HOLD){
//			supplierOrderLine.setSupplierOrderLineStatus(SupplierOrderLineStatus.READY_TO_SEND);
//		} else {
//			supplierOrderLine.setSupplierOrderLineStatus(SupplierOrderLineStatus.ON_HOLD);			
//		}
//	}
	
	@Override
	@Transactional
	public void processReorderReview(Collection<SupplierOrder> supplierOrders) {
		for(SupplierOrder so : supplierOrders) {
			saveOrUpdate(so);
		}
	}	
	
	
	@Override
	public Repository<SupplierOrder> getRepository() {
		return supplierOrderRepository;
	}

	@Override
	public Collection<SupplierOrder> getPending() {
		return supplierOrderRepository.getPending();
	}
	
	@Override
	public Map<Long, SupplierOrder> getPendingAsMap() {
		Collection<SupplierOrder> supplierOrders = getPending();
		
		Map<Long, SupplierOrder> supplierOrderMap = new HashMap<Long, SupplierOrder>();

		if(supplierOrders.isEmpty()) {
			return supplierOrderMap;
		}
		for(SupplierOrder supplierOrder : supplierOrders) {
			supplierOrderMap.put(supplierOrder.getSupplier().getId(), supplierOrder);
		}
		return supplierOrderMap;
	}

	@Override
	public SupplierOrder getPendingFromRepository(Long supplierId) {
		SupplierOrder supplierOrder = supplierOrderRepository.getPending(supplierId);
		if(supplierOrder == null) {
			//No pending, so create new
			supplierOrder = new SupplierOrder(new Supplier(supplierId));
		}
		return supplierOrder;
	}


	//Check if supplierorderline is on hold, if so add to next pending
	@Override
	@Transactional
	public void sendToSupplier(SupplierOrder supplierOrder) {
		//List of on hold order lines to add to next pending order
		List<SupplierOrderLine> onHoldList = new ArrayList<SupplierOrderLine>();
		List<SupplierOrderLine> toSendList = new ArrayList<SupplierOrderLine>();
		//Update stockitem on order record and change record
		for(SupplierOrderLine supplierOrderLine : supplierOrder.getSupplierOrderLines()) {
			if(supplierOrderLine.getSupplierOrderLineStatus() == SupplierOrderLineStatus.ON_HOLD) {
				supplierOrderLine.setSupplierOrderLineStatus(SupplierOrderLineStatus.READY_TO_SEND);
				onHoldList.add(supplierOrderLine);
//				supplierOrder.removeSupplierOrderLine(supplierOrderLine.getId());
				continue;
			}
			supplierOrderLine.setSupplierOrderLineStatus(SupplierOrderLineStatus.SENT_TO_SUPPLIER);
			StockItem stockItem = supplierOrderLine.getStockItem();
			stockItemService.updateQuantityOnOrder(stockItem, supplierOrderLine.getAmount());
			//supplierOrderLine.set

			CustomerOrderLine customerOrderLine = supplierOrderLine.getCustomerOrderLine();
			if(customerOrderLine == null || customerOrderLine.getId() == null) {
				supplierOrderLine.setCustomerOrderLine(null);
			} else {
				//Update customer order line, TO-DO split if quantity has changed via an earlier editSupplierOrderLine
				customerOrderLine.setOnOrderDate(new Date());
				customerOrderLine.setStatus(CustomerOrderLineStatus.ON_ORDER);
				customerOrderLineService.update(customerOrderLine);
			}
			toSendList.add(supplierOrderLine);
		}
		
		if(!toSendList.isEmpty()) {  //That is if all supplier order lines weren't on hold
			supplierOrder.setSupplierOrderLines(toSendList);
			supplierOrder.setSupplierOrderStatus(SupplierOrderStatus.SENT_TO_SUPPLIER);
			supplierOrder.setSendDate(new Date());
			saveOrUpdate(supplierOrder);
		} else {
			//Do nothing, just replacing one pending with another, pointless
			return;
		}
		
		//Have on-hold orders?
		if(onHoldList.isEmpty()) return;
		
		SupplierOrder newPendingOrder = supplierOrder.getPending();
		newPendingOrder.setSupplierOrderLines(onHoldList);
		
		save(newPendingOrder);
	}


	@Override
	public void removeSupplierOrderLine(Long supplierOrderLineId, SupplierOrder supplierOrder) {
		SupplierOrderLine sol = supplierOrder.getSupplierOrderLine(supplierOrderLineId);
		CustomerOrderLine col = sol.getCustomerOrderLine();
		//This supplierorderline might represent a customer order
		if(col != null && col.getId() != null && col.getStatus() == 			CustomerOrderLineStatus.PENDING_ON_ORDER) {
			//This is too simple, but will do for starters, just set to out of stock
			col.setStatus(CustomerOrderLineStatus.OUT_OF_STOCK);
			customerOrderLineService.update(col);
		}

		supplierOrder.removeSupplierOrderLine(supplierOrderLineId);

		update(supplierOrder);
	}


	@Override
	/**
	 * 	editSupplierOrder.jsp This jsp has the following :
	 * 	<form:hidden path="customerOrderLine.id"/>
	 * So need to null customerOrderLine if id is null, I not sure why
	 */
	public void editSupplierOrderLine(SupplierOrder supplierOrder, SupplierOrderLine supplierOrderLine) {
		//Not sure why I do this, set to null so it doesn't try and persist a customer order line
		if(supplierOrderLine.getCustomerOrderLine().getId() == null) {
			supplierOrderLine.setCustomerOrderLine(null);
		}
		//Check the supplier hasn't been changed
		if(supplierOrderLine.getSupplier().getId().longValue() != supplierOrder.getSupplier().getId().longValue()) {
			//Changed supplier, delete and add to new supplier
			removeSupplierOrderLine(supplierOrderLine.getId(), supplierOrder);
			supplierOrderLine.setSendDirectToSupplier(false);
			sendToSupplier(supplierOrderLine);
		}
		for(SupplierOrderLine sol : supplierOrder.getSupplierOrderLines()) {
			if(sol.getId().equals(supplierOrderLine.getId())) {
				//Again, 
				if(sol.getCustomerOrderLine().getId() == null) {
					sol.setCustomerOrderLine(null);
				}
				sol.setAmount(supplierOrderLine.getAmount());
			}
		}
		update(supplierOrder);
	}
	//From searchCustomerOrderLines or searchStockItems - SupplierOrderController.displayCreateSupplierOrderLineForCustomerOrder
	//From searchStockItems - displayCreateSupplierOrderLine - SupplierOrderController.sendToSupplier
	@Override
//	@Transactional
	public void sendToSupplier(SupplierOrderLine supplierOrderLine) {
		SupplierOrder supplierOrder = null;
		CustomerOrderLine col = supplierOrderLine.getCustomerOrderLine();
		
		//If a customerorderLine is attached, sort it out
		if(col != null && col.getId() != null) {
			col = customerOrderLineService.get(col.getId());
			supplierOrderLine.setCustomerOrderLine(col); //Why to prevent DuplicateKeyException
			if(supplierOrderLine.getSendDirectToSupplier()) {
				col.setStatus(CustomerOrderLineStatus.ON_ORDER);
				col.setOnOrderDate(new Date());
			} else {
				col.setStatus(CustomerOrderLineStatus.PENDING_ON_ORDER);
			}
		} else {
			supplierOrderLine.setCustomerOrderLine(null);
		}
		
		if(supplierOrderLine.getAmount() != 0) {
			if(supplierOrderLine.getSendDirectToSupplier()) {
				supplierOrder = new SupplierOrder();
				supplierOrder.setSupplier(supplierOrderLine.getSupplier());
				supplierOrder.addSupplierOrderLine(supplierOrderLine);
				sendToSupplier(supplierOrder);
			} else {
				 supplierOrder = getPendingFromRepository(supplierOrderLine.getSupplier().getId()); 
				 supplierOrder.addSupplierOrderLine(supplierOrderLine);
				 saveOrUpdate(supplierOrder);
			}
		}

		if(supplierOrderLine.getChangePreferredSupplier()) {
			stockItemService.updatePreferredSupplier(supplierOrderLine.getStockItem(), supplierOrderLine.getSupplier());
		}
		
		//Has marxism order as well?
		sendMarxismSupplierOrderLineToSupplier(supplierOrderLine, null);
	}
	
	@Override
	public void sendMarxismSupplierOrderLineToSupplier(SupplierOrderLine supplierOrderLine, Map<Long, SupplierOrder> supplierOrderMap) {
		if(supplierOrderLine.getMarxismAmount() == null || supplierOrderLine.getMarxismAmount() < 1) return; //Nothing to do (to-do delete if exists)
		
		//Create a new marxism sol
		SupplierOrderLine marxismSupplierOrderLine = new SupplierOrderLine();
		marxismSupplierOrderLine.setStockItem(supplierOrderLine.getStockItem());
//		sol.setSupplier(supplierOrderLine.getSupplier().getMarxismSupplier());
		marxismSupplierOrderLine.setSendDirectToSupplier(false);
		marxismSupplierOrderLine.setChangePreferredSupplier(false);
		marxismSupplierOrderLine.setAmount(supplierOrderLine.getMarxismAmount());
		
	

	}
	
	@Override
	public SupplierOrder getPendingFromMap(Supplier supplier, Map<Long, SupplierOrder> supplierOrderMap) {
		SupplierOrder supplierOrder = supplierOrderMap.get(supplier.getId());
		if(supplierOrder == null) {
			supplierOrder = new SupplierOrder(supplier);
			supplierOrderMap.put(supplier.getId(), supplierOrder);
		}
		return supplierOrder;
	}


	@Override
	public void save(Collection<SupplierOrder> supplierOrders) {
		for(SupplierOrder so : supplierOrders) {
			saveOrUpdate(so);
		}
	}

	@Override
	@Transactional
	public void markAllForHold(SupplierOrder supplierOrder) {
		for(SupplierOrderLine sol : supplierOrder.getSupplierOrderLines()) {
			supplierOrderLineService.updateStatus(sol, SupplierOrderLineStatus.ON_HOLD);
		}
	}

	@Override
	@Transactional
	public void markAllForReadyToSend(SupplierOrder supplierOrder) {
		for(SupplierOrderLine sol : supplierOrder.getSupplierOrderLines()) {
			supplierOrderLineService.updateStatus(sol, SupplierOrderLineStatus.READY_TO_SEND);
		}
	}

	/**
	 * this sends direct to supplier (ie bypasses READY_TO_SEND pending status)
	 */
	@Override
	public void sendToSupplier(CustomerOrderLine customerOrderLine, boolean isDirect) {
		SupplierOrderLine supplierOrderLine = new SupplierOrderLine();
		supplierOrderLine.setAmount(customerOrderLine.getAmount());
		supplierOrderLine.setStockItem(customerOrderLine.getStockItem());
		supplierOrderLine.setSupplierOrderLineStatus(SupplierOrderLineStatus.READY_TO_SEND);
		supplierOrderLine.setCustomerOrderLine(customerOrderLine);
		supplierOrderLine.setSendDirectToSupplier(isDirect);
		supplierOrderLine.setSupplier(customerOrderLine.getStockItem().getPreferredSupplier());
		if(isDirect) {
			supplierOrderLine.setSupplierOrderLineStatus(SupplierOrderLineStatus.SENT_TO_SUPPLIER);
		} else {
			supplierOrderLine.setSupplierOrderLineStatus(SupplierOrderLineStatus.READY_TO_SEND);
		}
		
		sendToSupplier(supplierOrderLine);
		
	}

	@Override
	public void markCustomerOrdersReadyToSend(SupplierOrder supplierOrder) {
		for(SupplierOrderLine sol : supplierOrder.getSupplierOrderLines()) {
			if(sol.getCustomerOrderLine() != null) {
				supplierOrderLineService.updateStatus(sol, SupplierOrderLineStatus.READY_TO_SEND);
			}
		}
	}
}
