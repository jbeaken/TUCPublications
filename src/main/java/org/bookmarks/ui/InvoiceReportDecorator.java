package org.bookmarks.ui;

import java.util.Locale;

import org.bookmarks.domain.report.InvoiceReportLine;

public class InvoiceReportDecorator extends AbstractBookmarksTableDecorator {


	public String getIsbn() {
		InvoiceReportLine invoiceReportLine = (InvoiceReportLine) getCurrentRowObject();
		return invoiceReportLine.getIsbn();
	}

	public String getDate() {
		InvoiceReportLine invoiceReportLine = (InvoiceReportLine) getCurrentRowObject();
		if(!invoiceReportLine.isCredit()) {
			return dateFormatter.print(invoiceReportLine.getSale().getCreationDate(), Locale.UK);
		}
		return dateFormatter.print(invoiceReportLine.getDate(), Locale.UK);
	}

	public String getDeliveryType() {
		InvoiceReportLine invoiceReportLine = (InvoiceReportLine) getCurrentRowObject();
		if(invoiceReportLine.getDeliveryType() == null) return "";
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

	public String getRef() {
		InvoiceReportLine invoiceReportLine = (InvoiceReportLine) getCurrentRowObject();

		return invoiceReportLine.getRef();

		// //Invoice
		// if(!invoiceReportLine.isCredit()) {
		// 	return invoiceReportLine.getSale().getStockItem().getTitle();
		// }

		// //Credit Note
		// if(invoiceReportLine.getCreditNote().getTransactionDescription() == null) {
		// 	return invoiceReportLine.getCreditNote().getNote();
		// }

		// return invoiceReportLine.getCreditNote().getTransactionDescription();
	}

	public String getCredit() {
		InvoiceReportLine invoiceReportLine = (InvoiceReportLine) getCurrentRowObject();
		return currencyFormatter.print(invoiceReportLine.getCreditAmount(), Locale.UK);
	}
}
