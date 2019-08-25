package org.bookmarks.service;

import org.bookmarks.domain.StockTakeLine;


public interface StockTakeLineService extends Service<StockTakeLine>{

	StockTakeLine getByStockItemId(Long id);

	void commit(Boolean resetQuantityInStock,  Boolean includeBookmarks, Boolean includeMerchandise);

	void reset();

}
