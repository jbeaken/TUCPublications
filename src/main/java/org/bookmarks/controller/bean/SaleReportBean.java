package org.bookmarks.controller.bean;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.bookmarks.controller.AbstractSearchBean;
import org.bookmarks.domain.SalesReportType;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.Event;
import org.bookmarks.domain.StockItem;
import org.springframework.format.annotation.DateTimeFormat;

public class SaleReportBean extends ReportBean {

	private Category category;

	private Sale sale;

	private boolean isCategorySearch;

	public boolean getIsCategorySearch() {
		return isCategorySearch;
	}

	public void setIsCategorySearch(boolean isCategorySearch) {
		this.isCategorySearch = isCategorySearch;
	}

/* Can be books only, merchandies only etc */
	private Integer status;

	@NotNull
	private SalesReportType salesReportType;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}


	public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
	}
	public String isbn;

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public SalesReportType getSalesReportType() {
		return salesReportType;
	}

	public void setSalesReportType(SalesReportType salesReportType) {
		this.salesReportType = salesReportType;
	}

	public SaleReportBean() {
		super();
		Sale sale = new Sale();
		sale.setEvent(new Event());
		sale.setStockItem(new StockItem());
		setSale(sale);
		sale.getStockItem().setType(null);
		setSortOrder("1");
//		setSortColumn("sum(s.quantity)");
	}

	//Used by EventController to set sale list report for this event only
	public SaleReportBean(Event event) {
		this();
		getSale().setEvent(event);
		setIsDateAgnostic(Boolean.TRUE);
		setSalesReportType(SalesReportType.SALE_LIST);
		setStartDate(event.getStartDate());
		setEndDate(event.getEndDate());
	}
}
