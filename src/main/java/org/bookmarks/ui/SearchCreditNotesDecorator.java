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

	public String getTransactionReference() {
		CreditNote e = (CreditNote)getCurrentRowObject();
		if(e.getTransactionReference() == null) return e.getNote();

		return e.getTransactionReference();
	}

	public String getAmountRaw() {
		CreditNote e = (CreditNote)getCurrentRowObject();
		return e.getAmount().toString();
	}

	public String getCustomerRaw() {
		CreditNote e = (CreditNote)getCurrentRowObject();
		return e.getCustomer().getFullName();
	}



	public String getAmount() {

		String color = "black";

		CreditNote e = (CreditNote)getCurrentRowObject();

		BigDecimal amount = e.getAmount();

		if(amount.longValue() < 0) {
			color = "red";
		}
		return "<span style='color : " + color + ";'>" + CurrencyStyleFormatter.print(e.getAmount(), Locale.UK) + "</span>";
	}
}
