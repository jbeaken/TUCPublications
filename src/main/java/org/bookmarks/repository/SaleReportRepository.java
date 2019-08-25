package org.bookmarks.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.SupplierDeliveryLine;
import org.bookmarks.domain.SupplierDelivery;
import org.bookmarks.report.bean.CategoryStockTakeBean;
import org.bookmarks.report.bean.PublisherStockTakeBean;

public interface SaleReportRepository {

	Collection<CategoryStockTakeBean> getCategoryStockTakeBeans();
	Collection<PublisherStockTakeBean> getPublisherStockTakeBeans();
	SupplierDeliveryLine getLastSupplierDeliveryLine(StockItem stockItem);
	SupplierDelivery getLastSupplierDelivery(StockItem stockItem);
	Collection<Date> getLastSaleDates(StockItem stockItem);
	Collection<Sale> getLastSales(StockItem stockItem);
	Collection<SupplierDeliveryLine> getLastSupplierDeliveryLines(StockItem stockItem);
}
