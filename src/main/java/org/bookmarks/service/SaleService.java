package org.bookmarks.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.Event;
import org.bookmarks.domain.InvoiceOrderLine;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.SaleOrReturn;
import org.bookmarks.domain.StockItem;


public interface SaleService extends Service<Sale>{
//	Sale sell(StockItem stockItem);

	Collection<Sale> get(Long stockID, Date firstDayOfYear, Date date);

	Collection<Sale> getAll(Date startDate, Date endDate);

	Collection<Sale> getFull(Date startDate, Date endDate);

	void sell(Sale sale);

	void sell(Sale sale, Boolean skipUpdatingStockRecord);

	void updateWithStockRecord(Sale sale, boolean updateStockRecord);

	Sale sellCustomerOrder(CustomerOrderLine col);

	Sale sell(StockItem stockItem, Event event);

	void sell(SaleOrReturn saleOrReturn);

	Long getTotalQuantityForPeriod(StockItem stockItem, Date startDate,
			Date endDate);

	List getAllForCsv();


//	void sell(InvoiceOrderLine invoiceOrderLine);
}
