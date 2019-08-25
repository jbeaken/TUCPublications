package org.bookmarks.service;

import java.util.Collection;

import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.Publisher;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.StockItemSales;
import org.bookmarks.repository.Repository;
import org.bookmarks.repository.StockItemSalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockItemSalesServiceImpl extends AbstractService<StockItemSales> implements StockItemSalesService {

	@Autowired
	private StockItemSalesRepository stockItemSalesRepository;
	

	@Override
	public Repository<StockItemSales> getRepository() {
		return stockItemSalesRepository;
	}
	

}
