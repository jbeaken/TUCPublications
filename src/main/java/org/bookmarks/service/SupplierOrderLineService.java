package org.bookmarks.service;

import java.util.Collection;

import org.bookmarks.controller.SupplierOrderLineSearchBean;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.SupplierOrderLine;
import org.bookmarks.domain.SupplierOrderLineStatus;
import org.bookmarks.domain.SupplierOrderLineType;


public interface SupplierOrderLineService extends Service<SupplierOrderLine> {

	void removeSupplierOrderLine(StockItem stockItem, SupplierOrderLineType type);

	void toggleOnHold(SupplierOrderLine supplierOrderLine);

	void updateStatus(SupplierOrderLine supplierOrderLine, SupplierOrderLineStatus status);

	void create(SupplierOrderLine supplierOrderLine); 

	SupplierOrderLine createForCustomerOrder(CustomerOrderLine customerOrderLine);

	SupplierOrderLine getByStockItemId(Long id);

	SupplierOrderLine getByStockItemId(Long id, SupplierOrderLineType type);

	SupplierOrderLine getByCustomerOrderLineId(Long customerOrderLineId);

	void setSupplier(SupplierOrderLine sol, StockItem stockItem);

	void reconcileKeepInStock(StockItem stockItem, boolean isSale);

	void sendToSupplier(SupplierOrderLine supplierOrderLine);

	void sendAllToSupplier(SupplierOrderLineSearchBean supplierOrderLineSearchBean);
	
	void putAllOnHold(SupplierOrderLineSearchBean supplierOrderLineSearchBean);

	void takeAllOffHold(SupplierOrderLineSearchBean supplierOrderLineSearchBean);

	void takeCustomerOrdersOffHold(SupplierOrderLineSearchBean supplierOrderLineSearchBean);

	void putCustomerOrdersOnHold(SupplierOrderLineSearchBean supplierOrderLineSearchBean);

	void reconcileAllKeepInStockItems();
}
