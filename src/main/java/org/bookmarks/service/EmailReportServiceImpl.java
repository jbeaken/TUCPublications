package org.bookmarks.service;

import java.util.Collection;
import java.util.List;

import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.CustomerOrderLineStatus;
import org.bookmarks.domain.StockItem;
import org.bookmarks.report.bean.DailyReportBean;
import org.bookmarks.repository.CustomerOrderLineRepository;
import org.bookmarks.repository.StockItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailReportServiceImpl implements EmailReportService {

	@Autowired
	private CustomerOrderLineRepository customerOrderLineRepository;
	
	@Autowired
	private StockItemRepository stockItemRepository;

	@Override
	public List<CustomerOrderLine> getCustomerOrderLines(CustomerOrderLineStatus status) {
		return customerOrderLineRepository.get(status);
	}
	
	@Override
	public List<CustomerOrderLine> getCustomerOrderLines(CustomerOrderLineStatus status, int daysOverdue) {
		return customerOrderLineRepository.get(status, daysOverdue);
	}
	@Override
	public Collection<StockItem> getStockItemsBelowKeepInStockLevel() { 
		return stockItemRepository.getStockItemsBelowKeepInStockLevel();
	}

	@Override
	public DailyReportBean getDailyReportBean() {
		return stockItemRepository.getDailyReportBean();
	}
}
