package org.bookmarks.ui;

import java.util.Locale;

import org.bookmarks.domain.AbstractEntity;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;
import org.displaytag.decorator.TableDecorator;

public class SaleDecorator extends AbstractBookmarksTableDecorator {
	public String getLink() {
	    Sale sale =(Sale)getCurrentRowObject();
	    if(sale.isSecondHand()) {
	    	return showDelete(sale);
	    } else return 	showDelete(sale) + "&nbsp;" + showEdit(sale);
	}
	
	public String getTitle() {
		Sale sale =(Sale)getCurrentRowObject();
		return getAnchor(contextPath + "/stock/edit?id=" + sale.getStockItem().getId(),
		        				sale.getStockItem().getTitle(),
        				"Edit", true,false);
	}
	
	public String getSellPrice() {
		Sale sale =(Sale)getCurrentRowObject();
		return CurrencyStyleFormatter.print(sale.getSellPrice(), Locale.UK);
	}
	
	protected  String showDelete(Sale e) {
		return getImageAnchor("delete?id=" + e.getId(), 
				"delete_medium.png", 
				"Delete", false, false);
	}	
	

	protected String showEdit(Sale e) {
		return getImageAnchor("edit?id=" + e.getId() + "&flow=search", 
				"write_medium.png", 
				"Edit", false, false);
	}	
	
	
	public String getDiscount() {
		Sale sale =(Sale)getCurrentRowObject();
		return sale.getDiscount() + "%";
	}	
}
