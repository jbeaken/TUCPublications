package org.bookmarks.service;

import java.util.Collection;
import java.util.List;

import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.controller.bean.ReorderReviewStockItemBean;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.SupplierOrder;


public interface ReorderReviewService extends Service<SupplierOrder>{ 
	
	void save(List<ReorderReviewStockItemBean> reorderReviewStockItemBeans);

	List<ReorderReviewStockItemBean> getReorderReview();

	void populate(ReorderReviewStockItemBean reorderReviewStockItemBean);

	void process(ReorderReviewStockItemBean originalBean, ReorderReviewStockItemBean reorderReviewStockItemBean);

	List<ReorderReviewStockItemBean> getReorderReview(Collection<StockItem> stockItems, StockItemSearchBean stockItemSearchBean);
}
