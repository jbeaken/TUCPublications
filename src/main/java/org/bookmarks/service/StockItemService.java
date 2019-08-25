package org.bookmarks.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.Supplier;
import org.bookmarks.domain.StockLevel;
import org.bookmarks.domain.StockItemType;
import org.bookmarks.controller.StockItemSearchBean;
import org.springframework.web.multipart.MultipartFile;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;


public interface StockItemService extends Service<StockItem> {

	void removeFromExtras(Long id);

	void updateForReorderReview(StockItem stockItem);

	StockItem get(String isbn);

	Collection<StockItem> searchIndex(StockItemSearchBean searchBean);

	void toggleIsForMarxism(Long id, boolean isForMarxism);

	void updateImageFilename(StockItem stockItem);

	void updateQuantityInStock(StockItem stockItem, Long quantityChange);

	void updateQuantityOnOrder(StockItem stockItem, Long amount);

	void updateQuantityOnOrderAbsolutely(StockItem stockItem, Long quantity);

	void updateQuantities(StockItem stockItem, Long quantityInStock, Long quantityOnLoan, Long quantityOnOrder, Long quantityForCustomerOrder, Long quantityReadyForCustomer);

	void setQuantityInStock(StockItem stockItem, Long quantityInStock);

	StockItem getByISBNAsNumber(Long isbn);

	StockItem getByISBNAsNumber(String isbn);

	void updateImageURL(StockItem stockItem, String url);

	Long getNextID();

	void updateLastReorderReviewDate(StockItem stockItem, Date date);

	void updateForSupplierDelivery(StockItem stockItem);

	String getImageURL(StockItem stockItem);

	Collection<StockItem> getSupplierStockItems(Long supplierId, StockLevel stockLevel);

	StockItem getResearchStockItem();

	List<StockItem> getFrontPageStockItems();

	StockItem getBookOfMonth();

	void updatePreferredSupplier(StockItem stockItem, Supplier supplier);

	StockItem getByISBNAsNumberForStockTake(Long isbnAsNumber);

	void buildIndex();

	Collection<StockItem> getKeepInStockItems();

	void resetAZSync();

	void updateSyncedWithAZ(StockItem stockItem, Boolean syncedWithAZ);

	void create(StockItem stockItem);

	Collection<StockItem> getNoAZAuthors(Integer offset, Integer noOfResults);

	Collection<StockItem> getNoAZPublishers(Integer offset, Integer noOfResults);

	Collection<String> getISBNsWithPagination(Integer offset, Integer noOfResults);

	Collection<StockItem> getStockItemsWithPagination(Integer offset, Integer noOfResults);

	Collection<StockItem> getUnsynchedWithAZAndIsOnWebsite(Integer offset, Integer noOfResults);

	Collection<StockItem> getUnsynchedWithAZ(Integer offset, Integer noOfResults);

	StockItem getFullStockItemByISBNAsNumber(String isbn);

	List<StockItem> getStickies(StockItemType type);

	void saveStickies(List<StockItem> stockItems, StockItemType type);

	List<StockItem> getBounciesAndStickies();

	void saveBouncies(List<StockItem> stockItems);

	void addImageToStockItem(StockItem stockItem, MultipartFile file) throws IOException, SftpException, JSchException;

	Collection<StockItem> getBouncies();

	boolean exists(String isbn);

	boolean exists(Long id);

	void setIsAvailableAtSuppliers(String isbn, boolean availability);

	void setGardnersStockLevel(String isbn, Long noInStock);

	void resetGardnersStockLevel();

	Collection<StockItem> getExtras();

	void updateNewReleases(Set<String> newReleasesIsbn);

	Collection<StockItem> getMerchandise();
}
