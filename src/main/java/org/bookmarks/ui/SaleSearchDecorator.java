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

public class SaleSearchDecorator extends AbstractBookmarksTableDecorator {
	
	public String getLink() {
	    Sale sale = (Sale)getCurrentRowObject();
	    return 	showDelete(sale) + showEdit(sale);
	}
	
	public String getCreationDate() {
		Sale sale = (Sale)getCurrentRowObject();
		return dateTimeFormatter.print(sale.getCreationDate(), Locale.UK);
	}
	
	public String getTotalPrice() {
		Sale sale = (Sale)getCurrentRowObject();
		return CurrencyStyleFormatter.print(sale.getDiscountedPrice().multiply(new BigDecimal(sale.getQuantity())), Locale.UK);
	}
	public String getSellPrice() {
		Sale sale = (Sale)getCurrentRowObject();
		return CurrencyStyleFormatter.print(sale.getSellPrice(), Locale.UK);
	}
}
