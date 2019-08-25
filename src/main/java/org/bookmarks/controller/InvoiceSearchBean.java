package org.bookmarks.controller;

import java.util.Date;

import org.bookmarks.domain.Binding;
import org.bookmarks.domain.Invoice;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.StockItemType;
import org.bookmarks.domain.Supplier;

public class InvoiceSearchBean extends AbstractSearchBean {

	//Constructors
	public InvoiceSearchBean() {
		invoice = new Invoice();
		stockItem = new StockItem();
		setSortColumn("i.id");
		setSortOrder("asc");
		setPage("1");
		setGroupBy(true);
	}
	private Invoice invoice;
	
	private StockItem stockItem;
	
	private Date startDate;
	
	private Date endDate;
	
	public StockItem getStockItem() {
		return stockItem;
	}
	public void setStockItem(StockItem stockItem) {
		this.stockItem = stockItem;
	}
	public Invoice getInvoice() {
		return invoice;
	}
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	
	@Override
	public void reset() {
		invoice = new Invoice();
		stockItem = new StockItem();
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
