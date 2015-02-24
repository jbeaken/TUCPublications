package org.bookmarks.ui;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.bookmarks.website.domain.Address;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.TelephoneDirectory;
import org.displaytag.decorator.TableDecorator;
import org.springframework.format.datetime.DateFormatter;

public class ExtrasDecorator extends AbstractBookmarksTableDecorator {
	

	
	public String getType() {
		StockItem stockItem = (StockItem) getCurrentRowObject();
		String colour = null;
		switch (stockItem.getType()) {
			case BOOK:
				colour = "red";
				break;
			case MUG:
				colour = "green";
				break;
			case TEA_TOWEL:
				colour = "grey";
				break;			
			default:
				colour = "black";
				break;
		}
		return "<span style='color : " + colour + ">" + stockItem.getType().getDisplayName() + "</span>";
	}

}
