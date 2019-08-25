package org.bookmarks.ui;

import java.util.Locale;

import org.bookmarks.domain.AbstractEntity;
import org.bookmarks.domain.StockTakeLine;
import org.bookmarks.domain.StockItem;
import org.displaytag.decorator.TableDecorator;

public class StockTakeDecorator extends AbstractBookmarksTableDecorator {
	public String getLink() {
	    StockTakeLine stockRecord =(StockTakeLine)getCurrentRowObject();
	   return 	showDelete(stockRecord) + "&nbsp;" + showEdit(stockRecord);
	}
	
	protected  String showDelete(StockTakeLine e) {
		return getImageAnchor("delete?id=" + e.getId(), 
				"delete_medium.png", 
				"Delete", false, false);
	}	
	

	protected String showEdit(StockTakeLine e) {
		return getImageAnchor("edit?id=" + e.getId() + "&flow=search", 
				"write_medium.png", 
				"Edit", false, false);
	}	
}
