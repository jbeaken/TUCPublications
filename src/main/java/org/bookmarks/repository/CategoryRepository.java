package org.bookmarks.repository;

import java.util.Collection;
import java.util.List;

import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.StockItem;

public interface CategoryRepository extends Repository<Category>{

	List<StockItem> getStickies(Long id);

	void resetStickies(Category category);

	void saveSticky(StockItem stockItem, Long index);

}
