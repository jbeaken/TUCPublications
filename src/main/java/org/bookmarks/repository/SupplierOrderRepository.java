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

public interface SupplierOrderRepository extends Repository<SupplierOrder>{

	Collection<SupplierOrder> getPending();

	SupplierOrder getPending(Long id);

	Collection<StockItem> getReorderReview(SaleReportBean saleSearchBean);
	
	Collection<StockItem> getReorderReview(ReorderReviewBean reorderReviewBean);

	SupplierOrderLine getPendingSupplierOrderLineFromStockItem(StockItem stockItem);

//	List<StockItem> getReorderReview(Date startDate, Date endDate);	

}
