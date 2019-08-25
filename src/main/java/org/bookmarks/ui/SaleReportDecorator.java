package org.bookmarks.ui;

import java.math.BigDecimal;
import java.util.Locale;

import org.bookmarks.domain.Sale;

public class SaleReportDecorator extends AbstractBookmarksTableDecorator {
	
	
	public String getLink() {
	    Sale sale = (Sale)getCurrentRowObject();
	    return 	confirmation("/saleReport/delete?id=" + sale.getId(), "delete_medium.png")
		+ confirmation("/saleReport/edit?id=" + sale.getId(), "write_medium.png");
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
