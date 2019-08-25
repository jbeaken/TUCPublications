package org.bookmarks.service;

import java.util.Collection;
import java.util.List;

import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;


public interface CategoryService extends Service<Category>{

	List<StockItem> getStickies(Long id);

	void saveStickies(List<StockItem> stockItems, Category category);
}
