package org.bookmarks.ui;

import java.math.BigDecimal;
import java.util.Locale;

import org.bookmarks.domain.VTTransaction;

public class SearchVTTransactionsDecorator extends AbstractBookmarksTableDecorator {
	
//	public String getDate() {
//		VTTransaction e = (VTTransaction)getCurrentRowObject();
//		return dateFormatter.print(e.getDate(), Locale.UK);
//	}

	
	public String getTotal() {
		
		String color = "black";
		
		VTTransaction e = (VTTransaction)getCurrentRowObject();
		
		Float amount = e.getTotal();
		
		if(amount.longValue() < 0) {
			color = "red";
		}
		return "<span style='color : " + color + ";'>" + currencyFormatter.print(e.getTotal(), Locale.UK) + "</span>";
	}	
}
