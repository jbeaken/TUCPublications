package org.bookmarks.ui;

import org.bookmarks.domain.StockItem;
import org.displaytag.decorator.TableDecorator;

public class CustomerOrderStockItemSearchDecorator extends SearchStockItemsDecorator {
	public String getLink()	{
        StockItem stockItem = (StockItem)getCurrentRowObject();
        Long id = stockItem.getId();
        if(id == null) id = -1l;
        return getImageAnchor("addSingleStockItem?id=" + id, 
				"add.png", 
				"Add to order", false);
	}
}
