package org.bookmarks.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.controller.bean.ReorderReviewBean;
import org.bookmarks.controller.bean.SaleReportBean;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.Supplier;
import org.bookmarks.domain.SupplierDelivery;
import org.bookmarks.domain.SupplierOrder;
import org.bookmarks.domain.SupplierOrderLine;
import org.bookmarks.domain.SupplierOrderLineType;

public interface SupplierOrderLineRepository extends Repository<SupplierOrderLine> {

	SupplierOrderLine getByStockItemId(Long id);

	SupplierOrderLine getByStockItemId(Long id, SupplierOrderLineType type);

	SupplierOrderLine getByCustomerOrderLineId(Long customerOrderLineId);

	void deleteKeepInStockSupplierOrderLine(StockItem stockItem);

	void deleteAllKeepInStockSupplierOrderLines();

	void removeSupplierOrderLine(StockItem stockItem, SupplierOrderLineType type);
}