package org.bookmarks.repository;

import java.util.Date;
import java.util.List;
import java.util.Collection;

import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.StockItemSales;
import org.bookmarks.domain.StockItemType;
import org.bookmarks.domain.Supplier;
import org.bookmarks.controller.InvoiceSearchBean;
import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.domain.StockLevel;
import org.bookmarks.domain.Level;
import org.bookmarks.report.bean.DailyReportBean;

public interface StockItemRepository extends Repository<StockItem> {

	void updateForReorderReview(StockItem stockItem);

	void updateImageFilename(StockItem stockItem);

	Collection<StockItem> searchIndex(StockItemSearchBean searchBean);
	
	StockItem get(String isbn);
	
	Collection<StockItem> getStockItemsBelowKeepInStockLevel();
	
	void toggleIsForMarxism(Long id, boolean isForMarxism);

	void updateQuantityInStock(StockItem stockItem, Long quantityChange);

	StockItem getByISBNAsNumber(Long isbn);

	Collection<StockItem> getSupplierStockItems(Long supplierId, StockLevel stockLevel);

	void updateImageURL(StockItem stockItem, String url);

	Long getNextID();

	void updateQuantityOnOrder(StockItem stockItem, Long quantityChange);

	void updateQuantityOnOrderAbsolutely(StockItem stockItem, Long quantity);

	void updateForSupplierDelivery(StockItem stockItem);

	void updateQuantities(StockItem stockItem, Long quantityInStock,
			Long quantityOnLoan, Long quantityOnOrder,
			Long quantityForCustomerOrder, Long quantityReadyForCustomer);

	StockItem getResearchStockItem();

	List<StockItem> getFrontPageStockItems();

	StockItem getBookOfMonth();

	void updateLastReorderReviewDate(StockItem stockItem, Date date);

	void updatePreferredSupplier(StockItem stockItem, Supplier supplier);

	void setQuantityInStock(StockItem stockItem, Long quantityInStock);

	StockItem getByISBNAsNumberForStockTake(Long isbnAsNumber);

	void buildIndex();

	void updateTwentyTwelveSales(StockItem stockItem);

	void updateTwentyThirteenSales(StockItem stockItem);

	Collection<StockItem> getKeepInStockItems();

	void resetAZSync();

	void updateSyncedWithAZ(StockItem si, boolean b);

	Collection<StockItem> getNoAZAuthors(Integer offset, Integer noOfResults);

	Collection<StockItem> getNoAZPublishers(Integer offset, Integer noOfResults);

	Collection<String> getISBNsWithPagination(Integer offset, Integer noOfResults);

	Collection<StockItem> getStockItemsWithPagination(Integer offset, Integer noOfResults);

	Collection<StockItem> getUnsynchedWithAZAndIsOnWebsite(Integer offset, Integer noOfResults);

	Collection<StockItem> getUnsynchedWithAZ(Integer offset, Integer noOfResults);

	StockItem getFullStockItemByISBNAsNumber(String isbn);

	List<StockItem> getStickies(StockItemType type);

	List<StockItem> getBounciesAndStickies();

	void saveSticky(StockItem si, Long long1);

	void resetStickies(StockItemType type);

	void resetBouncies();

	void saveBouncy(StockItem si, Long long1);

	void removeFromWebsite(Long id);

	void putOnWebsite(Long id);

	DailyReportBean getDailyReportBean();

	Collection<StockItem> getBouncies();

	void updateTwentyFourteenSales(StockItem stockItem);

	Long getTotalSales(StockItem stockItem);

	Long getSalesLastYear(StockItem stockItem);

	boolean exists(Long id);

	boolean exists(String isbn);

	void setIsAvailableAtSuppliers(String isbn, boolean availability);

	void setGardnersStockLevel(String isbn, Long noInStock);

	void resetGardnersStockLevel();

	Collection<StockItem> getExtras();

	void resetNewReleases();

	void setNewRelease(String isbn, boolean isNewRelease);

	Collection<StockItem> unsold(InvoiceSearchBean invoiceSearchBean);

	Collection<StockItem> getMerchandise();

	List<StockItemSales> getStockItemSales(StockItem stockItem);
}
