package org.bookmarks.ui;

import java.util.Locale;

import org.bookmarks.domain.Event;

public class SearchEventDecorator extends AbstractBookmarksTableDecorator {
	// public String getLink() {
  //       Event event = (Event)getCurrentRowObject();
  //       return showView(event)
  //       		+ showEdit(event)
  //       		+ showSell(event)
  //       		+ showSales(event)
	// 			+ showDelete(event)
	// 			+ showUpload(event)
	// 			+ showEditNote(event);
	// }

	// private String showSell(Event event) {
	// 	return getImageAnchor("/bookmarks/events/startSelling?eventId=" + event.getId() + "&eventName=" + event.getName(), "sell.png", "Start Selling", false);
	// }

	// private String showUpload(Event event) {
	// 	return getImageAnchor("/bookmarks/events/uploadSales?eventId=" + event.getId() + "&eventName=" + event.getName(), "report.png", "Upload from Mini Beans", false);
	// }
	//
	// private String showSales(Event event) {
	// 	return getImageAnchor("/bookmarks/events/showSales?eventId=" + event.getId() + "&eventName=" + event.getName(), "report.png", "Show Sales", false);
	// }

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
		return currencyFormatter.print(event.getTotalSellPrice(), Locale.UK);
	}
}
