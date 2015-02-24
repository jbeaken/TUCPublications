package org.bookmarks.ui;

import org.bookmarks.domain.StockItem;

public class SaleOrReturnStockItemSearchDecorator extends SearchStockItemsDecorator {
	public String getLink()	{
        StockItem stockItem = (StockItem)getCurrentRowObject();
        return getImageAnchor("addStockItem?id=" + stockItem.getId(), 
				"blue-write.png", 
				"Add to order", false);       				
	}
	
	public String getSellPrice() {
		StockItem stockItem = (StockItem)getCurrentRowObject();
		return "&pound;" + stockItem.getSellPrice().toString();
	}	
}
