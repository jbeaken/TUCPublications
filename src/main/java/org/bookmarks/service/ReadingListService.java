package org.bookmarks.service;

import java.util.Collection;

import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.ReadingList;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;


public interface ReadingListService extends Service<ReadingList>{

	ReadingList findByName(String name);
}
