package org.bookmarks.repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.CustomerOrderLineStatus;
import org.bookmarks.domain.StockItem;

public interface CustomerOrderLineRepository extends Repository<CustomerOrderLine>{
	Collection<CustomerOrderLine> findOpenOrdersForStockItem(StockItem stockItem);

	void updateStatus(Long id, CustomerOrderLineStatus customerOrderStatus);

	void markAsPaid(Long customerOrderLineId);

	void updateToOrdered(Long id, Long amount);

	List<CustomerOrderLine> get(CustomerOrderLineStatus status);

	List<CustomerOrderLine> get(CustomerOrderLineStatus status, int daysOverdue);

	void wipeCCDetails();

	void updateHasPrintedLabel(boolean havePrintedLabel, Long id);
}
