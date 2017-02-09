package org.bookmarks.ui;

import java.math.BigDecimal;
import java.util.Locale;

import org.bookmarks.domain.CreditNote;

public class SearchCreditNotesDecorator extends AbstractBookmarksTableDecorator {
	
	public String getDate() {
		CreditNote e = (CreditNote)getCurrentRowObject();
		return dateFormatter.print(e.getDate(), Locale.UK);
	}
	
	public String getCustomer() {
		CreditNote e = (CreditNote)getCurrentRowObject();
		return getCustomerName(e.getCustomer());
	}	
	
	public String getAmount() {
		
		String color = "black";
		
		CreditNote e = (CreditNote)getCurrentRowObject();
		
		BigDecimal amount = e.getAmount();
		
		if(amount.longValue() < 0) {
			color = "red";
		}
		return "<span style='color : " + color + ";'>" + currencyFormatter.print(e.getAmount(), Locale.UK) + "</span>";
	}	
}
