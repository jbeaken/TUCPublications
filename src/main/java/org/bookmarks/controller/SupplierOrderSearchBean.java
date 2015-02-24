package org.bookmarks.controller;

import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.SupplierOrder;

public class SupplierOrderSearchBean extends AbstractSearchBean {

	//Constructors
	public SupplierOrderSearchBean() {
		supplierOrder = new SupplierOrder();
		stockItem = new StockItem();
		setGroupBy(true);
	}
	public SupplierOrderSearchBean(Long customerOrderLineId) {
		this();
		CustomerOrderLine col = new CustomerOrderLine(customerOrderLineId);
		setCustomerOrderLine(col);
	}
	
	private SupplierOrder supplierOrder;
	
	private StockItem stockItem;
	
	private Boolean containsCustomerOrderLines;
	
	private CustomerOrderLine customerOrderLine;
	
	public CustomerOrderLine getCustomerOrderLine() {
		return customerOrderLine;
	}
	public void setCustomerOrderLine(CustomerOrderLine customerOrderLine) {
		this.customerOrderLine = customerOrderLine;
	}
	public Boolean getContainsCustomerOrderLines() {
		return containsCustomerOrderLines;
	}
	public void setContainsCustomerOrderLines(Boolean containsCustomerOrderLines) {
		this.containsCustomerOrderLines = containsCustomerOrderLines;
	}
	public StockItem getStockItem() {
		return stockItem;
	}
	public void setStockItem(StockItem stockItem) {
		this.stockItem = stockItem;
	}
	public SupplierOrder getSupplierOrder() {
		return supplierOrder;
	}
	public void setSupplierOrder(SupplierOrder supplierOrder) {
		this.supplierOrder = supplierOrder;
	}
	
	@Override
	public void reset() {
		supplierOrder = new SupplierOrder();
		stockItem = new StockItem();
	}
}
