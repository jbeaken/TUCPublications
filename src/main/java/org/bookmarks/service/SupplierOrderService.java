package org.bookmarks.service;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.bookmarks.controller.bean.ReorderReviewBean;
import org.bookmarks.controller.bean.SaleReportBean;
import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.Supplier;
import org.bookmarks.domain.SupplierDelivery;
import org.bookmarks.domain.SupplierOrder;
import org.bookmarks.domain.SupplierOrderLine;


public interface SupplierOrderService extends Service<SupplierOrder>{

	SupplierOrder getPendingFromRepository(Long id);
	
	Map<Long, SupplierOrder> getPendingAsMap();


	void removeSupplierOrderLine(Long id, SupplierOrder supplierOrder);

	void editSupplierOrderLine(SupplierOrder supplierOrder, SupplierOrderLine supplierOrderLine);


	void save(Collection<SupplierOrder> supplierOrders);

	Collection<SupplierOrder> getPending();

	void markAllForHold(SupplierOrder supplierOrder);

	void markAllForReadyToSend(SupplierOrder supplierOrder);

	void sendToSupplier(CustomerOrderLine customerOrderLine, boolean isDirect);

	void sendToSupplier(SupplierOrder supplierOrder);

	void sendToSupplier(SupplierOrderLine supplierOrderLine);

	void processReorderReview(Collection<SupplierOrder> supplierOrders);

	void sendMarxismSupplierOrderLineToSupplier(
			SupplierOrderLine supplierOrderLine,
			Map<Long, SupplierOrder> supplierOrderMap);

	SupplierOrder getPendingFromMap(Supplier supplier,
			Map<Long, SupplierOrder> supplierOrderMap);

	void markCustomerOrdersReadyToSend(SupplierOrder supplierOrder);
}
