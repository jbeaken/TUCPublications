package org.bookmarks.controller;

import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.SupplierOrderLine;
import org.bookmarks.domain.SupplierOrderLineStatus;

public class SupplierOrderLineSearchBean extends AbstractSearchBean {

	//Constructors
	public SupplierOrderLineSearchBean() {
		supplierOrderLine = new SupplierOrderLine();
//		supplierOrderLine.setType(null);
		supplierOrderLine.setPriority(null);
		stockItem = new StockItem();
		// setGroupBy(true);
	}
	public SupplierOrderLineSearchBean(Long customerOrderLineId) {
		this();
		CustomerOrderLine col = new CustomerOrderLine(customerOrderLineId);
		setCustomerOrderLine(col);
	}
	
	public SupplierOrderLineSearchBean(CustomerOrderLine customerOrderLine) {
		this();
		getSupplierOrderLine().setSupplierOrderLineStatus(null);
		setCustomerOrderLine(customerOrderLine);
	}	
	
	private SupplierOrderLine supplierOrderLine;
	
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
	public SupplierOrderLine getSupplierOrderLine() {
		return supplierOrderLine;
	}
	public void setSupplierOrderLine(SupplierOrderLine supplierOrderLine) {
		this.supplierOrderLine = supplierOrderLine;
	}
	
	@Override
	public void reset() {
		supplierOrderLine = new SupplierOrderLine();
		stockItem = new StockItem();
	}
}
