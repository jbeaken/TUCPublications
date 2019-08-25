package org.bookmarks.controller;

import org.bookmarks.domain.SaleOrReturn;
import org.bookmarks.domain.StockItem;

public class SaleOrReturnSearchBean extends AbstractSearchBean {

	
	public SaleOrReturnSearchBean() {
		super();
		setSaleOrReturn(new SaleOrReturn());
		setGroupBy(true);
	}
	
	private SaleOrReturn saleOrReturn;
	
	private Boolean overdue;
	
	public Boolean getOverdue() {
		return overdue;
	}
	public void setOverdue(Boolean overdue) {
		this.overdue = overdue;
	}

	private StockItem stockItem;
	public StockItem getStockItem() {
		return stockItem;
	}
	public void setStockItem(StockItem stockItem) {
		this.stockItem = stockItem;
	}
	public SaleOrReturn getSaleOrReturn() {
		return saleOrReturn;
	}
	public void setSaleOrReturn(SaleOrReturn saleOrReturn) {
		this.saleOrReturn = saleOrReturn;
	}
	@Override
	public void reset() {
		setSaleOrReturn(new SaleOrReturn());

	}

}
