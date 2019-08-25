package org.bookmarks.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="supplierreturnline")
public class SupplierReturnLine extends OrderLine {

	public SupplierReturnLine() {
		super();

	}

	public SupplierReturnLine(StockItem stockItem) {
		this();
		setAmount(1l);
		setStockItem(stockItem);
	}

}
