package org.bookmarks.ui;

import java.util.Locale;

import org.bookmarks.domain.report.InvoiceReportLine;

public class InvoiceReportDecorator extends AbstractBookmarksTableDecorator {
	

	public String getIsbn() {
		InvoiceReportLine invoiceReportLine = (InvoiceReportLine) getCurrentRowObject();
		if(!invoiceReportLine.isCredit()) {
			return invoiceReportLine.getSale().getStockItem().getIsbn();
		}
		return "";
	}	
	
	public String getDate() {
		InvoiceReportLine invoiceReportLine = (InvoiceReportLine) getCurrentRowObject();
		if(!invoiceReportLine.isCredit()) {
			return dateFormatter.print(invoiceReportLine.getSale().getCreationDate(), Locale.UK);
		}
		return dateFormatter.print(invoiceReportLine.getCreditNote().getCreationDate(), Locale.UK);
	}
	
	public String getDeliveryType() {
		InvoiceReportLine invoiceReportLine = (InvoiceReportLine) getCurrentRowObject();
		return invoiceReportLine.getDeliveryType().getDisplayName();
	}		
	
	public String getDiscount() {
		InvoiceReportLine invoiceReportLine = (InvoiceReportLine) getCurrentRowObject();
		if(!invoiceReportLine.isCredit()) {
			return invoiceReportLine.getSale().getDiscount() + "%";
		}
		return "-";
	}	
	
	public String getTotalPrice() {
		InvoiceReportLine invoiceReportLine = (InvoiceReportLine) getCurrentRowObject();
		if(!invoiceReportLine.isCredit()) {
			return currencyFormatter.print(invoiceReportLine.getSale().getTotalPrice(), Locale.UK);
		}
		return "-";
	}	
	
	public String getCurrentBalance() {
		InvoiceReportLine invoiceReportLine = (InvoiceReportLine) getCurrentRowObject();
			return currencyFormatter.print(invoiceReportLine.getCurrentBalance(), Locale.UK);
	}	
	
	public String getTitle() {
		InvoiceReportLine invoiceReportLine = (InvoiceReportLine) getCurrentRowObject();
		if(!invoiceReportLine.isCredit()) {
			return invoiceReportLine.getSale().getStockItem().getTitle();
		}
		return "";
	}	
	
	public String getCredit() {
		InvoiceReportLine invoiceReportLine = (InvoiceReportLine) getCurrentRowObject();
		if(invoiceReportLine.isCredit()) {
			return currencyFormatter.print(invoiceReportLine.getCreditNote().getAmount(), Locale.UK);
		}
		return "-";
	}
}
