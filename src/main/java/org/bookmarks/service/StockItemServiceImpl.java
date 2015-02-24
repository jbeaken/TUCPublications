package org.bookmarks.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.bookmarks.controller.SearchBean;
import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.domain.Author;
import org.bookmarks.domain.Binding;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.Publisher;
import org.bookmarks.domain.Review;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.Supplier;
import org.bookmarks.domain.StockLevel;
import org.bookmarks.domain.WebsiteInfo;
import org.bookmarks.exceptions.BookmarksException;
import org.bookmarks.repository.Repository;
import org.bookmarks.repository.SaleRepository;
import org.bookmarks.repository.StockItemRepository;
import org.bookmarks.domain.StockItemType;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;

import static org.imgscalr.Scalr.*;

@Service
public class StockItemServiceImpl extends AbstractService<StockItem> implements StockItemService {
	
	@Autowired
	private SupplierOrderLineService supplierOrderLineService;

	@Autowired
	private AuthorService authorService;
	
	@Autowired
	private PublisherService publisherService;
	
	@Autowired
	private ChipsService chipsService;	
	
	@Autowired
	private StaticDataService staticDataService;
	
	@Value("#{ applicationProperties['imageFileLocation'] }")
	private String imageFileLocation;
	
	@Value("#{ applicationProperties['imageLocalURL'] }")
	private String imageLocalURL;
	
	@Override
	public Collection<StockItem> search(SearchBean searchBean) {
		Collection<StockItem> stockItems =  getRepository().search(searchBean);
		
		//Get authors
		for(StockItem si : stockItems) {
			Collection<Author> authors = authorService.findByStockItem(si);
			Set<Author> a = new HashSet<Author>(authors);
			si.setAuthors(a);
		}
		return stockItems;
	}	
	

	@Override
	public void saveOrUpdate(StockItem stockItem) {
		
		prepareStockItemForSaveOrUpdate(stockItem);
		
//		stockItem = super.merge(stockItem); 
		
		super.update(stockItem);
		
		//TO-DO remove, must persist stockItem before persisting sol
		supplierOrderLineService.reconcileKeepInStock(stockItem, false);
	}
	
	private void prepareGeneratedStockItem(StockItem stockItem) {
		//Need to make up isbn
		Long nextID = getNextID();
		String s = nextID.toString();
		StringBuffer isbn = new StringBuffer(13);
		int length = s.length();
		for(int i = 0; i < (13 - length); i++){
			isbn.append('1');
		}
		isbn.append(nextID);
		stockItem.setIsbn(isbn.toString());
		stockItem.setIsImageOnAZ(false);
		stockItem.setIsOnAZ(false);
		stockItem.setIsReviewOnAZ(false);
		stockItem.setSyncedWithAZ(true);
	}

	@Override
	public void updateImageFilename(StockItem stockItem) {
		stockItemRepository.updateImageFilename(stockItem);
	}
	
	private void prepareStockItemForSaveOrUpdate(StockItem stockItem) {

		stockItem.setIsbnAsNumber(Long.parseLong(stockItem.getIsbn()));
		stockItem.setOriginalTitle(stockItem.getTitle());
		
		//Why?? Otherwise get object references an unsaved transient instance - save the transient
		if(stockItem.getPreferredSupplier() == null || stockItem.getPreferredSupplier().getId() == null) {
			stockItem.setPreferredSupplier(null); 
		}
		
		//Postage
		if(stockItem.getPostage() == null) {
			if(stockItem.getType() == StockItemType.PAMPHLET) {
				stockItem.setPostage(new BigDecimal(0.25));
			} else stockItem.setPostage(new BigDecimal(0.75));
		}
		
		//Remove tags from review
/*		String reviewAsText = stockItem.getReviewAsText();		
		if(reviewAsText != null) {
			String escapedText = Jsoup.parse(reviewAsText).html();
			String htmlText = Jsoup.parse(reviewAsText).text();
			//stockItem.setReviewAsText(reviewAsText);
		}*/
		
		String reviewAsText = stockItem.getReviewAsText();		
		if(reviewAsText == null || reviewAsText.isEmpty()) {
			stockItem.setReviewAsText(null);
		}
		
		String reviewAsHTML = stockItem.getReviewAsHTML();		
		if(reviewAsHTML == null || reviewAsHTML.isEmpty()) {
			stockItem.setReviewAsHTML(null);
		}		
		
		//Sort out images
		if(stockItem.getImageFilename() != null && stockItem.getImageFilename().isEmpty()) {
			stockItem.setImageFilename(null);
		}
		if(stockItem.getImageURL() != null && stockItem.getImageURL().isEmpty()) {
			stockItem.setImageURL(null);
		}		
		
		//Binding
		if(!stockItem.isBook()) {
			stockItem.setBinding(Binding.OTHER);
		}
		
		stockItem.setLastReorderReviewDate(new Date());
	}	

	@Autowired
	private StockItemImageService stockItemImageService;

	public void setStockItemRepository(StockItemRepository stockItemRepository) {
		this.stockItemRepository = stockItemRepository;
	}
	
	@Override
	public Collection<StockItem> searchIndex(StockItemSearchBean searchBean) {
		return stockItemRepository.searchIndex(searchBean);
	}
	
	@Override
	public void buildIndex() {
		stockItemRepository.buildIndex();
	}

	@Autowired
	private StockItemRepository stockItemRepository;
	
	public StockItemServiceImpl() {
		super();
	}
	
	public StockItemServiceImpl(StockItemRepository stockItemRepository) {
		this();
		this.stockItemRepository = stockItemRepository;
	}

	@Override
	public Repository<StockItem> getRepository() {
		return stockItemRepository;
	}

	@Override
	public StockItem get(String isbn) {
		return stockItemRepository.get(isbn);
	}

	@Override
	public void toggleIsForMarxism(Long id, boolean isForMarxism) {
	}
	
	@Override
	public StockItem getByISBNAsNumber(Long isbn) {
		return stockItemRepository.getByISBNAsNumber(isbn);
	}
	

	@Override
	public StockItem getByISBNAsNumber(String isbn) {
		Long isbnAsNumber = Long.parseLong(isbn);
		return getByISBNAsNumber(isbnAsNumber);
	}	

	@Override
	public void updateQuantityInStock(StockItem stockItem, Long quantityChange) {
		stockItemRepository.updateQuantityInStock(stockItem, quantityChange);
		stockItem.setQuantityInStock(stockItem.getQuantityInStock() + quantityChange); //Set it locally as well
	}

	@Override
	public void updateImageURL(StockItem stockItem, String imageURL) {
		stockItemRepository.updateImageURL(stockItem, imageURL);
		stockItem.setImageURL(imageURL);
	}

	@Override
	public Long getNextID() {
		return stockItemRepository.getNextID();
	}

	@Override
	public void updateQuantityOnOrder(StockItem stockItem, Long quantityChange) {
		stockItemRepository.updateQuantityOnOrder(stockItem, quantityChange);		
	}

	@Override
	public void updateQuantityOnOrderAbsolutely(StockItem stockItem, Long quantityChange) {
		stockItemRepository.updateQuantityOnOrderAbsolutely(stockItem, quantityChange);		
	}	

	@Override
	public void updateForSupplierDelivery(StockItem stockItem) {
		stockItemRepository.updateForSupplierDelivery(stockItem);		
	}
	
	@Override
	public String getImageURL(StockItem stockItem) {
		String imageURL = stockItemImageService.getImageURL(stockItem.getIsbn());
		if(imageURL != null) {
			updateImageURL(stockItem, imageURL);
		}
		return imageURL;
	}

	@Override
	public Collection<StockItem> getSupplierStockItems(Long supplierId, StockLevel stockLevel) {
		return stockItemRepository.getSupplierStockItems(supplierId, stockLevel);
	}

	@Override
	public StockItem getResearchStockItem() {
		return stockItemRepository.get(1000l);
	}

	@Override
	public List<StockItem> getFrontPageStockItems() {
		// TODO Auto-generated method stub
		return stockItemRepository.getFrontPageStockItems();
	}

	@Override
	public StockItem getBookOfMonth() {
		return stockItemRepository.getBookOfMonth();
	}

	@Override
	public void updateLastReorderReviewDate(StockItem stockItem, Date date) {
		stockItemRepository.updateLastReorderReviewDate(stockItem, date);
	}

	@Override
	public void updateForReorderReview(StockItem stockItem) {
		stockItemRepository.updateForReorderReview(stockItem);
	}	

	@Override
	public void updatePreferredSupplier(StockItem stockItem, Supplier supplier) {
		stockItemRepository.updatePreferredSupplier(stockItem, supplier);
	}

	@Override
	public void updateQuantities(StockItem stockItem, 
		    Long quantityInStock,
			Long quantityOnLoan, 
			Long quantityOnOrder,
			Long quantityForCustomerOrder,
			Long quantityReadyForCustomer) {
			stockItemRepository.updateQuantities(stockItem, quantityInStock, quantityOnLoan, quantityOnOrder, quantityForCustomerOrder, quantityReadyForCustomer);		
	}

	@Override
	public void setQuantityInStock(StockItem stockItem, Long quantityInStock) {
		stockItemRepository.setQuantityInStock(stockItem, quantityInStock);
	}

	@Override
	public StockItem getByISBNAsNumberForStockTake(Long isbnAsNumber) {
		return stockItemRepository.getByISBNAsNumberForStockTake(isbnAsNumber);
	}

	@Override
	public Collection<StockItem> getKeepInStockItems() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resetAZSync() {
		stockItemRepository.resetAZSync();
	}


	@Override
	public void updateSyncedWithAZ(StockItem si, Boolean false1) {
		stockItemRepository.updateSyncedWithAZ(si, false);
		
	}

	@Override
	public Collection<StockItem> getUnsynchedWithAZAndIsOnWebsite(Integer offset,	Integer noOfResults) {
		return stockItemRepository.getUnsynchedWithAZAndIsOnWebsite(offset, noOfResults);
	}
	
	@Override
	public Collection<StockItem> getUnsynchedWithAZ(Integer offset,	Integer noOfResults) {
		return stockItemRepository.getUnsynchedWithAZ(offset, noOfResults);
	}	

	/**
	 * Persist a new stockitem to the db. If putOnWebsite and putImageOnWebsite
	 * are true, upload image to web server by sftp
	 */
	@Override
	public void create(StockItem stockItem) {
		if(stockItem.getGenerateISBN() == true) {
			prepareGeneratedStockItem(stockItem);
		} else stockItem.convertToISBN13();
		
		prepareStockItemForSaveOrUpdate(stockItem);
		
		stockItem.setSyncedWithAZ(true); 
		
		//Publisher publisher = publisherService.get(stockItem.getPublisher().getId());
		//stockItem.setPublisher(publisher);
//		persistAuthors(stockItem);
//		persitAZPublisher(stockItem);

		super.save(stockItem);
		
		//May have added a new publisher
		staticDataService.resetPublishers();
		
		//TODO remove, must persist stockItem before persisting sol
		supplierOrderLineService.reconcileKeepInStock(stockItem, false);
	}
	


	/**
	 * Used by create, ie a new stockitem
	 * @param stockItem
	 */
	private void persitAZPublisher(StockItem stockItem) {
		if(stockItem.getPublisher() == null || stockItem.getPublisher().getId() == null) { //Not from a lookup at az
			Publisher publisher = publisherService.get(stockItem.getPublisher().getId());
			Publisher exists = publisherService.getByName(publisher.getName());
			if(exists == null) {
				exists = new Publisher(publisher.getName());
				publisherService.save(exists);
				staticDataService.resetPublishers();
			}
			stockItem.setPublisher(exists);
		}
	}

	/**
	 * Used by create, ie a new stockitem
	 * @param stockItem
	 */
	private void persistAuthors(StockItem stockItem) {
		Set<Author> authorsToAdd = new HashSet<Author>();
//		if(stockItem.getAuthors() == null || stockItem.getAuthors().isEmpty()) {
//			Author author = new Author();
//			author.setName(stockItem.getConcatenatedAuthors());
//			stockItem.getAuthors().add(author);
//		}
		for(Author author : stockItem.getAuthors()) {
			Author exists = authorService.findByName(author.getName());
			if(exists == null) { //This author isn't in the bookmarks database, save it
//				logger.info("Adding new author : " + author.getName());
				authorService.save(author);
				authorsToAdd.add(author);
				//si.getAuthors().add(author);
			} else {
				authorsToAdd.add(exists);
				//si.getAuthors().add(exists);
			}
		}
		stockItem.setAuthors(authorsToAdd);
	}

	@Override
	public Collection<StockItem> getNoAZAuthors(Integer offset, 	Integer noOfResults) {
		return stockItemRepository.getNoAZAuthors(offset, noOfResults);
	}

	@Override
	public Collection<StockItem> getNoAZPublishers(Integer offset,	Integer noOfResults) {
		return stockItemRepository.getNoAZPublishers(offset, noOfResults);
	}

	@Override
	public Collection<String> getISBNsWithPagination(Integer offset, Integer noOfResults) {
		return stockItemRepository.getISBNsWithPagination(offset, noOfResults);
	}

	@Override
	public Collection<StockItem> getStockItemsWithPagination(Integer offset, Integer noOfResults) {
		return stockItemRepository.getStockItemsWithPagination(offset, noOfResults);
	}

	@Override
	public StockItem getFullStockItemByISBNAsNumber(String isbn) {
		return stockItemRepository.getFullStockItemByISBNAsNumber(isbn);
	}

	@Override
	public List<StockItem> getStickies(StockItemType type) {
		return stockItemRepository.getStickies(type);
	}

	@Override
	public void saveStickies(List<StockItem> stockItems, StockItemType type) {
		//Reset stickies
		stockItemRepository.resetStickies(type);
		
		//Go through stockitems, saving index as stickyCategory
		Long index = (long) stockItems.size();
		for(StockItem si : stockItems) {
			stockItemRepository.saveSticky(si, index--);
		}
		
	}

	@Override
	public List<StockItem> getBounciesAndStickies() {
		return stockItemRepository.getBounciesAndStickies();
	}
	
	@Override
	public void saveBouncies(List<StockItem> stockItems) {
		//Reset bouncies
		stockItemRepository.resetBouncies();
		
		//Go through stockitems, saving index as stickyCategory
		Long index = (long) stockItems.size();
		for(StockItem si : stockItems) {
			stockItemRepository.saveBouncy(si, index--);
		}
		
//		//Now resize and crop
//		for(StockItem si : stockItems) {
//			stockItemRepository.saveBouncy(si, index--);
//			BufferedImage image = null;
//			try {
//				File imageFile = new File(imageFileLocation + File.separator + si.getImageFilename());
//
//				image = ImageIO.read(imageFile);
//				
//				BufferedImage croppedImage = resize(image, Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH, 107, 160);
//				croppedImage = Scalr.crop(croppedImage, 107, 160, Scalr.OP_ANTIALIAS);
//				
//				File outputFile = new File(imageFileLocation + File.separator + "bouncy" + File.separator + si.getImageFilename());
//				if (!imageFile.exists()) {
//					imageFile.createNewFile();
//				}
//				ImageIO.write(croppedImage, "jpg", outputFile);
//				
//				image.flush(); //Free up resources
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
	}

	/**
	 * From displayStockItemUPloadImage.jsp, upload a image, save it to the filesystem, sftp to chips and then inform chips of 
	 * filename information changes.
	 */
	@Override
	public void addImageToStockItem(StockItem stockItem, MultipartFile file) throws IOException, SftpException, JSchException {
			InputStream is = file.getInputStream();
			
			//Normally /home/bookmarks/images/orginal/isbn.jpg
			File originalFile = new File(imageFileLocation + "original" + File.separator + stockItem.getIsbn() + ".jpg");
			
			OutputStream os = new FileOutputStream(originalFile);
 
			// if file doesnt exists, then create it
			if (!originalFile.exists()) {
				originalFile.createNewFile();
			}
			
			//Save file to beans local file system
			IOUtils.copy(is, os);
			
			//Resize
			BufferedImage originalImage = javax.imageio.ImageIO.read(originalFile);
			BufferedImage resizedImage = resize(originalImage, Method.ULTRA_QUALITY, Mode.FIT_TO_WIDTH, 150, OP_ANTIALIAS);
			File outputFile = new File(imageFileLocation + "150" + File.separator + stockItem.getIsbn() + ".jpg");
			javax.imageio.ImageIO.write(resizedImage, "jpg", outputFile); //Save to local filesystem
			
			stockItem.setImageFilename(stockItem.getIsbn() + ".jpg");
			stockItem.setImageURL(imageLocalURL + "original" + File.separator + stockItem.getIsbn() + ".jpg");
			
			//Now sync if necessary
			if(stockItem.getPutOnWebsite() == true) {
				chipsService.syncStockItemWithChips(stockItem);
			}
			
			//Everything has gone okay, now update stockitem with filename information
			updateImageFilename(stockItem); //Save to db			
	}

	@Override
	public Collection<StockItem> getBouncies() {
		return stockItemRepository.getBouncies();
	}

	@Override
	public boolean exists(String isbn) {
		return stockItemRepository.exists(isbn);
	}
	
	@Override
	public boolean exists(Long id) {
		return stockItemRepository.exists(id);
	}

	@Override
	public void setIsAvailableAtSuppliers(String isbn, boolean availability) {
		stockItemRepository.setIsAvailableAtSuppliers(isbn, availability);
	}	
	
	@Override
	public void setGardnersStockLevel(String isbn, Long noInStock) {
		stockItemRepository.setGardnersStockLevel(isbn, noInStock);
	}

	@Override
	public void resetGardnersStockLevel() {
		stockItemRepository.resetGardnersStockLevel();
		
	}

	@Override
	public Collection<StockItem> getExtras() {
		Collection<StockItem> stockItems = stockItemRepository.getExtras();
//		Map<String, List<StockItem>> map = new HashMap<String, List<StockItem>>();
//		for(StockItem si : stockItems) {
//			String type = si.getType().getDisplayName();
//			List<StockItem> list = map.get(type);
//			if(list == null) {
//				list = new ArrayList();
//				map.put(type, list);
//			}
//			list.add(si);
//		}
		return stockItems;
		
	}


	@Override
	public void updateNewReleases(Set<String> newReleasesIsbn) {
		
		stockItemRepository.resetNewReleases();
		
		for(String isbn : newReleasesIsbn) {
			stockItemRepository.setNewRelease(isbn, true);
		}
		
	}


	@Override
	public Collection<StockItem> getMerchandise() {
		return stockItemRepository.getMerchandise();
	}	
}
