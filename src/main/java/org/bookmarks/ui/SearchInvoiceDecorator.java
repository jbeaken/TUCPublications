package org.bookmarks.ui;

import java.util.Locale;

import org.bookmarks.website.domain.Address;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.Invoice;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.TelephoneDirectory;
import org.displaytag.decorator.TableDecorator;

public class SearchInvoiceDecorator extends AbstractBookmarksTableDecorator {
	public String getLink()	{
        Invoice invoice = (Invoice)getCurrentRowObject();
        return getImageAnchor("edit?id=" + invoice.getId(),
				"edit_green.png",
				"Edit", false)
				+ getImageAnchor("view?id=" + invoice.getId() + "&flow=searchInvoices",
						"view_green.png",
						"View", false)
	        	+ getImageAnchor("/bookmarks/invoice/print?invoiceId=" + invoice.getId(),
	        						"printer_green.png",
	        						"Print", true)
	        	+ confirmation("/bookmarks/invoice/delete?id=" + invoice.getId(), "delete_green.png")
				+ showEditNote(invoice, "note_green.png");
	}

	public String getIsProforma() {
		Invoice invoice = (Invoice)getCurrentRowObject();
		if(invoice.getIsProforma()) {
			return "<div style='color : green; text-align: center;'><i class='icon-ok icon-2x center'></i></div>";
		} 
		return "<div style='color : red; text-align: center;'><i class='icon-remove icon-2x center'></i></div>";
	}

	public String getPaid() {
		Invoice invoice = (Invoice)getCurrentRowObject();
		if(invoice.getPaid()) {
			return "<div style='color : green; text-align: center;'><i class='icon-ok icon-2x center'></i></div>";
		} 
		return "<div style='color : red; text-align: center;'><i class='icon-remove icon-2x center'></i></div>";
	}	

	public String getCustomerName() {
		Invoice invoice = (Invoice)getCurrentRowObject();
		Customer customer = invoice.getCustomer();
		return getCustomerName(customer, "invoiceSearch");
	//	return getAnchor("/bookmarks/customer/edit?id=" + customer.getId() + "&flow=invoiceSearch",
		//		customer.getFullName(),
		//		"Edit", true, false);
	}

	public String getCreationDate() {
		Invoice invoice = (Invoice)getCurrentRowObject();
		return dateFormatter.print(invoice.getCreationDate(), Locale.UK);
	}

	public String getAddress() {
		Invoice invoice = (Invoice)getCurrentRowObject();
		return getAddress(invoice.getCustomer());
	}

	public String getSecondHandPrice() {
		Invoice invoice = (Invoice)getCurrentRowObject();
		return CurrencyStyleFormatter.print(invoice.getSecondHandPrice(), Locale.UK);
	}

	public String getServiceCharge() {
		Invoice invoice = (Invoice)getCurrentRowObject();
		return CurrencyStyleFormatter.print(invoice.getServiceCharge(), Locale.UK);
	}

	public String getTotalPrice() {
		Invoice invoice = (Invoice)getCurrentRowObject();
		return CurrencyStyleFormatter.print(invoice.getTotalPrice(), Locale.UK);
	}

	public String getTelephoneNumber() {
		Invoice invoice = (Invoice)getCurrentRowObject();
		return getTelephoneNumber(invoice.getCustomer().getContactDetails());
	}
}
