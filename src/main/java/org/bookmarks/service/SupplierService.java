package org.bookmarks.service;

import java.util.Collection;

import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.Publisher;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.Supplier;


public interface SupplierService extends Service<Supplier>{

	void updateMarxismSupplier(Supplier supplier, Supplier marxismSupplier);

	Supplier getSupplier(Publisher publisher);
	
	Collection<Supplier> getAllSortedExcludingMarxismSuppliers();

	Collection<Supplier> getForAutoComplete(String term);
}
