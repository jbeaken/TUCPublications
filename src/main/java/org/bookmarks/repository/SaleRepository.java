package org.bookmarks.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.bookmarks.controller.bean.SaleTotalBean;
import org.bookmarks.controller.SearchBean;
import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.domain.Sale;
import org.bookmarks.controller.bean.SaleTotalBean;
import org.bookmarks.domain.StockItem;

public interface SaleRepository extends Repository<Sale>{

	void save(Sale sale);

	Collection<Sale> get(Long stockItemID, Date startDate, Date endDate);

	Collection<Sale> get(Date startDate, Date endDate);

	Collection<Sale> getFull(Date startDate, Date endDate);
	
	SaleTotalBean getSaleTotalBean(SearchBean searchBean);

	Long getTotalQuantityForPeriod(StockItem stockItem, Date startDate,
			Date endDate);

	List getAllForCsv();


}
