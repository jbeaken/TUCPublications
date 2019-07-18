package org.bookmarks.ui;

import java.util.Locale;

import org.bookmarks.domain.Event;

public class SearchEventDecorator extends AbstractBookmarksTableDecorator {

	public String getStartDate() {
		Event event = (Event)getCurrentRowObject();
		return dateFormatter.print(event.getStartDate(), Locale.UK);
	}

	public String getEndDate() {
		Event event = (Event)getCurrentRowObject();
		return dateFormatter.print(event.getEndDate(), Locale.UK);
	}

	public String getTotalSellPrice() {
		Event event = (Event)getCurrentRowObject();
		return CurrencyStyleFormatter.print(event.getTotalSellPrice(), Locale.UK);
	}
}
