package org.bookmarks.ui;

import org.bookmarks.domain.StockItem;
import org.displaytag.decorator.TableDecorator;

public class InvoiceSearchDecorator extends TableDecorator {
	public String getLink()
	{
	        StockItem stockItem = (StockItem)getCurrentRowObject();
	        long Id= stockItem .getId();

	        return "<a href=\"select?id=" + Id + "\">S</a>";
	        
	}
}
