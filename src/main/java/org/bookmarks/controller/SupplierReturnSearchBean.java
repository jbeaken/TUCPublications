package org.bookmarks.controller;

import javax.validation.Valid;

import org.bookmarks.domain.Supplier;
import org.bookmarks.domain.SupplierReturn;
import org.bookmarks.domain.StockItem;

public class SupplierReturnSearchBean extends AbstractSearchBean {
	
	public SupplierReturnSearchBean(SupplierReturn supplierReturn) {
		this();
		this.supplierReturn = supplierReturn;
	}
	
	public SupplierReturnSearchBean() {
		super();
		setStockItem(new StockItem());
		SupplierReturn sd = new SupplierReturn(new Supplier());
		setSupplierReturn(sd);
		setGroupBy(true);
		setSortOrder("DESC");
		setSortColumn("sd.id");
	}

	private StockItem stockItem;
	
	public StockItem getStockItem() {
		return stockItem;
	}
	public void setStockItem(StockItem stockItem) {
		this.stockItem = stockItem;
	}
	
	@Valid
	public SupplierReturn supplierReturn;

	public SupplierReturn getSupplierReturn() {
		return supplierReturn;
	}

	public void setSupplierReturn(SupplierReturn supplierReturn) {
		this.supplierReturn = supplierReturn;
	}

	@Override
	public void reset() {
		setSupplierReturn(new SupplierReturn());
		
	}
	
}
