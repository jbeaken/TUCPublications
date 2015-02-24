package org.bookmarks.controller.bean;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.Supplier;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.Event;
import org.bookmarks.domain.SupplierOrderLine;
import org.springframework.format.annotation.DateTimeFormat;

public class ReorderReviewStockItemBean {

	public ReorderReviewStockItemBean() {
		super();
	}
	
	private StockItem stockItem;
	
	private MonthlySaleReportBean monthlySaleReportBean;

	private SupplierOrderLine supplierOrderLine;
	
	public MonthlySaleReportBean getMonthlySaleReportBean() {
		return monthlySaleReportBean;
	}

	public void setMonthlySaleReportBean(MonthlySaleReportBean monthlySaleReportBean) {
		this.monthlySaleReportBean = monthlySaleReportBean;
	}

	private boolean populated = false;
	
	private boolean processed = false;
	
	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	private Long originalSupplierOrderAmount;
	
	private Supplier originalSupplier;
	
	public Supplier getOriginalSupplier() {
		return originalSupplier;
	}

	public void setOriginalSupplier(Supplier originalSupplier) {
		this.originalSupplier = originalSupplier;
	}

	public Long getOriginalSupplierOrderAmount() {
		return originalSupplierOrderAmount;
	}

	public void setOriginalSupplierOrderAmount(Long originalSupplierOrderAmount) {
		this.originalSupplierOrderAmount = originalSupplierOrderAmount;
	}

	public boolean isPopulated() {
		return populated;
	}

	public void setPopulated(boolean populated) {
		this.populated = populated;
	}

	public SupplierOrderLine getSupplierOrderLine() {
		return supplierOrderLine;
	}

	public void setSupplierOrderLine(SupplierOrderLine supplierOrderLine) {
		this.supplierOrderLine = supplierOrderLine;
	}

//	public Supplier getSupplier() {
//		return supplier;
//	}
//
//	public void setSupplier(Supplier supplier) {
//		this.supplier = supplier;
//	}

	public StockItem getStockItem() {
		return stockItem;
	}

	public void setStockItem(StockItem stockItem) {
		this.stockItem = stockItem;
	}
}	
