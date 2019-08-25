package org.bookmarks.service;

import java.util.Collection;

import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.domain.ReadingList;
import org.bookmarks.domain.StockItem;
import org.bookmarks.repository.Repository;
import org.bookmarks.repository.ReadingListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReadingListServiceImpl extends AbstractService<ReadingList> implements ReadingListService{

	@Autowired
	private ReadingListRepository readingListRepository;
	
	@Override
	public Repository<ReadingList> getRepository() {
		return readingListRepository;
	}

	@Override
	public ReadingList findByName(String name) {
		return readingListRepository.findByName(name);
	}
}
