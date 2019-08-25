package org.bookmarks.repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import org.bookmarks.domain.SupplierDeliveryLine;
import org.bookmarks.domain.StockItem;

public interface ReorderReviewRepository {
	Collection<StockItem> getReorderReview();
}
