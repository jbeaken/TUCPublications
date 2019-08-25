package org.bookmarks.service;

import java.util.Collection;
import java.util.List;

import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.CustomerOrderLineStatus;
import org.bookmarks.domain.StockItem;
import org.bookmarks.report.bean.DailyReportBean;

public interface EmailReportService {

	List<CustomerOrderLine> getCustomerOrderLines(CustomerOrderLineStatus status);

	List<CustomerOrderLine> getCustomerOrderLines(CustomerOrderLineStatus research, int daysOverdue);
			
	Collection<StockItem> getStockItemsBelowKeepInStockLevel();

	DailyReportBean getDailyReportBean();

}
