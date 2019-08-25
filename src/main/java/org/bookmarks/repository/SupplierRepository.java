package org.bookmarks.repository;

import java.util.Collection;
import java.util.List;

import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.Publisher;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.Supplier;

public interface SupplierRepository extends Repository<Supplier>{

	void updateMarxismSupplier(Supplier supplier, Supplier marxismSupplier);
	
	Collection<Supplier> getAllSortedExcludingMarxismSuppliers();

	Collection<Supplier> getForAutoComplete(String searchString);

}
