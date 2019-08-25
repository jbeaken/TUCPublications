package org.bookmarks.controller;

import java.util.Collection;

import org.bookmarks.domain.AbstractEntity;
import org.bookmarks.domain.Category;
import org.bookmarks.service.Service;

public interface BookmarkController<E extends AbstractEntity> {
//	Collection<Category> getCategories();
	 
	Service<E> getService();
}
