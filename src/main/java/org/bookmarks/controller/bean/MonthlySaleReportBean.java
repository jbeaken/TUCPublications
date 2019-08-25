package org.bookmarks.controller.bean;

import java.util.Collection;
import java.util.Date;

import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItemSales;
import org.bookmarks.domain.SupplierDelivery;
import org.bookmarks.domain.SupplierDeliveryLine;

public class MonthlySaleReportBean implements java.io.Serializable {

	public MonthlySaleReportBean() {
		super();
	}

	private Collection<Date> lastSaleDates;

	private Collection<Sale> lastSales;

	private Integer currentYear;

	private Collection<StockItemSales> sales;

	private Collection<SupplierDeliveryLine> lastSupplierDeliveryLines;

	private SupplierDeliveryLine lastSupplierDeliveryLine;

	private SupplierDelivery lastSupplierDelivery;


	public SupplierDeliveryLine getLastSupplierDeliveryLine() {
		return lastSupplierDeliveryLine;
	}

	public void setLastSupplierDeliveryLine(SupplierDeliveryLine lastSupplierDeliveryLine) {
		this.lastSupplierDeliveryLine = lastSupplierDeliveryLine;
	}

	public SupplierDelivery getLastSupplierDelivery() {
		return lastSupplierDelivery;
	}

	public void setLastSupplierDelivery(SupplierDelivery lastSupplierDelivery) {
		this.lastSupplierDelivery = lastSupplierDelivery;
	}

	public Collection<Date> getLastSaleDates() {
		return lastSaleDates;
	}

	public void setLastSaleDates(Collection<Date> lastSaleDates) {
		this.lastSaleDates = lastSaleDates;
	}

	public Collection<Sale> getLastSales() {
		return lastSales;
	}

	public void setLastSales(Collection<Sale> lastSales) {
		this.lastSales = lastSales;
	}

	public Collection<SupplierDeliveryLine> getLastSupplierDeliveryLines() {
		return lastSupplierDeliveryLines;
	}

	public void setLastSupplierDeliveryLines(Collection<SupplierDeliveryLine> lastSupplierDeliveryLines) {
		this.lastSupplierDeliveryLines = lastSupplierDeliveryLines;
	}

	public Collection<StockItemSales> getSales() {
		return sales;
	}

	public void setSales(Collection<StockItemSales> sales) {
		this.sales = sales;
	}

	public Integer getCurrentYear() {
		return currentYear;
	}

	public void setCurrentYear(Integer currentYear) {
		this.currentYear = currentYear;
	}
}
