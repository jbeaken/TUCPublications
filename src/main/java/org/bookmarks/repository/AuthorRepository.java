package org.bookmarks.repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.domain.Author;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.StockItem;

public interface AuthorRepository extends Repository<Author>{

	Author findByName(String name);

	Collection<Author> findByNameLike(String name);

	List<Author> findByStockItem(StockItem stockItem);

	void moveAndDelete(Author authorToDelete, Author authorToKeep);
}
