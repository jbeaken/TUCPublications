package org.bookmarks.service;

import java.util.Collection;
import java.util.Map;

import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.CustomerOrderLineStatus;
import org.bookmarks.domain.SupplierDeliveryLine;
import org.bookmarks.domain.StockItem;

public interface CustomerOrderLineService extends Service<CustomerOrderLine>{

	Collection<CustomerOrderLine> findOpenOrdersForStockItem(StockItem stockItem);

	void fill(CustomerOrderLine customerOrderLine, SupplierDeliveryLine goodsIntoStockOrderLine);

	void updateStatus(Long id, CustomerOrderLineStatus status);

	void markAsPaid(Long customerOrderLineId);

	void fill(CustomerOrderLine customerOrderLine);

	void save(Collection<CustomerOrderLine> customerOrderLines);

	void updateToOrdered(Map<Long, Long> customerOrderLineMap);

	void saveOrUpdate(Collection<CustomerOrderLine> customerOrderLinesToSaveOrUpdate);

	void complete(CustomerOrderLineStatus status, Long customerOrderLineId);

	String complete(CustomerOrderLine customerOrderLine);

	void wipeCCDetails();

	void makeNote(Long id, CustomerOrderLineStatus customerOrderStatus);


	void updateHasPrintedLabel(boolean b, Long id);
}
