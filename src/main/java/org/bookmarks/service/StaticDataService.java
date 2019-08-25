package org.bookmarks.service;

import java.util.Collection;

import org.bookmarks.domain.Category;
import org.bookmarks.domain.Publisher;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.Supplier;

public interface StaticDataService {

	Collection<Category> getCategories();
	
	Collection<Supplier> getSuppliers();
	
	Collection<Publisher> getPublishers();
	
	void resetPublishers();
	
	void resetCategories();
	
	void resetSuppliers();

	StockItem getSecondHandStockItem();
}
