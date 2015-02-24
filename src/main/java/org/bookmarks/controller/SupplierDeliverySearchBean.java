package org.bookmarks.controller;

import javax.validation.Valid;

import org.bookmarks.domain.Supplier;
import org.bookmarks.domain.SupplierDelivery;
import org.bookmarks.domain.StockItem;

public class SupplierDeliverySearchBean extends AbstractSearchBean {
	public SupplierDeliverySearchBean(SupplierDelivery supplierDelivery) {
		this();
		this.supplierDelivery = supplierDelivery;
	}
	
	public SupplierDeliverySearchBean() {
		super();
		setStockItem(new StockItem());
		SupplierDelivery sd = new SupplierDelivery(new Supplier());
		setSupplierDelivery(sd);
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
	public SupplierDelivery supplierDelivery;

	public SupplierDelivery getSupplierDelivery() {
		return supplierDelivery;
	}

	public void setSupplierDelivery(SupplierDelivery supplierDelivery) {
		this.supplierDelivery = supplierDelivery;
	}

	@Override
	public void reset() {
		setSupplierDelivery(new SupplierDelivery());
		
	}
	
}
