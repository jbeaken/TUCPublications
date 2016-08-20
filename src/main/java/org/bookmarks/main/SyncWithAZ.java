package org.bookmarks.main;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bookmarks.domain.Author;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.WebsiteInfo;
import org.bookmarks.service.AZLookupService;
import org.bookmarks.service.PublisherService;
import org.bookmarks.service.AuthorService;
import org.bookmarks.service.StockItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * 
 * @author King Kong
 * Cannot getPrice for 9781850658436
 * mvn exec:java -Dexec.mainClass="org.bookmarks.main.SyncWithAZ"
 * 
 * 
 * Get all stockitems where synced_with_az = false and use isbn to lookup at AZ
 * Transfer sellPrice, publisherPrice, publishedDate, Authors, Publisher, imageURL info
 * Update is_on_az, is_image_on_az, synced_with_az
 * 
 * 
 * 
 */
public class SyncWithAZ extends AbstractSpringBooter {
	
	private static boolean syncPublisher = true;
	
	private static boolean syncAZAuthors = true;
	
	private static boolean downloadImagesFromAZ = false;
	
	
	static final Logger logger = LoggerFactory.getLogger(SyncWithAZ.class);
	
	public static void main(String[] args) {
		ApplicationContext applicationContext = getApplicationContext();
		
		StockItemService stockItemService = (StockItemService) applicationContext.getBean("stockItemServiceImpl");
		AZLookupService azLookupService = (AZLookupService) applicationContext.getBean("AZLookupServiceImpl");
		AuthorService authorService = (AuthorService) applicationContext.getBean("authorServiceImpl");
		PublisherService azPublisherService = (PublisherService) applicationContext.getBean("PublisherServiceImpl");
		

		//Reset sync
		stockItemService.resetAZSync();
		
		while(true) {
			Collection<StockItem> stockItems = stockItemService.getUnsynchedWithAZ(0, 100);
			logger.info("Loaded batch of " + stockItems.size());
			if(stockItems.size() == 0) System.exit(0); //All done!
			for(StockItem si : stockItems) {
				logger.info("****** Lookup of : " + si.toString());
				si.setOriginalTitle(si.getTitle());
				try {
					//Get AZ's version of this isbn
					StockItem fromAZ = azLookupService.lookupWithJSoup(si.getIsbn(), downloadImagesFromAZ);
//					StockItem fromAZ = azLookupService.lookupWithJSoup("9780002172578"); //This is only available from 3rd party sellers: The Pen and the Sword [Paperback]	Michael Foot (Author)
					if(fromAZ == null) {
						//Not on AZ or error, set availablity information
						logger.info("No az info found for " + si.getTitle());
						updateNotOnAz(si, authorService);
					} else {
						//Success, transfer all data
						transferFromAZ(si, fromAZ, authorService, azPublisherService);
						
						//Set flags
						si.setSyncedWithAZ(Boolean.TRUE);
					}
					
					log(si);
					
					//Update
					stockItemService.update(si);
					
				} catch(Exception e) {
					logger.error("Error thrown while looking up, cannot get az info", e);
					try {
						updateNotOnAz(si, authorService);
						//Update
						stockItemService.update(si);
					} catch(Exception ex) {
						logger.error("Shit has happened", ex);
					}
				}
				
				logger.info("******** Success!!");
			}//end for
			logger.info("Finished batch, reloading next batch");
		}//end while
	}
	
	private static void updateNotOnAz(StockItem si, AuthorService authorService) {
		si.setSyncedWithAZ(Boolean.TRUE);
		si.setIsOnAZ(Boolean.FALSE);
		si.setIsImageOnAZ(Boolean.FALSE);
		//This should not be neccessary after first full update
		Author author = new Author();
//		author.setName(si.getConcatenatedAuthors());
		Set<Author> authors = new HashSet<Author>();
		authors.add(author);
		si.setAuthors(authors);
		persistAuthors(si, authors, authorService);
	}

	private static void transferFromAZ(StockItem si, StockItem fromAZ, AuthorService authorService, PublisherService azPublisherService) {
		//AZ title is generally better, check if over 255 chars
		if(si.getUpdateTitle() == true) {
			si.setTitle(fromAZ.getTitle());
		}
		
		if(fromAZ.getImageURL() != null && si.getUpdateImage() == true) {
			si.setImageURL(fromAZ.getImageURL());
			si.setImageFilename(fromAZ.getImageFilename());
			si.setIsImageOnAZ(fromAZ.getIsImageOnAZ()); //Must be true
		} 
		
		if(fromAZ.getSellPrice() != null && si.getUpdateSellPrice() == true) {
			//Has there been a price change
			si.setSellPrice(fromAZ.getSellPrice());
			si.setPublisherPrice(fromAZ.getSellPrice());
		}
		
		si.setPriceAtAZ(fromAZ.getPriceAtAZ());
		si.setPriceThirdPartyCollectable(fromAZ.getPriceThirdPartyCollectable());
		si.setPriceThirdPartyNew(fromAZ.getPriceThirdPartyNew());
		si.setPriceThirdPartySecondHand(fromAZ.getPriceThirdPartySecondHand());
		
		//Availablity
		if(si.getUpdateAvailablity() == true) {
			si.setAvailability(fromAZ.getAvailability());
		}
		
		//Published date
		if(fromAZ.getPublishedDate() != null) {
			si.setPublishedDate(fromAZ.getPublishedDate());
		}
		
		//Dimensions
		si.setDimensions(fromAZ.getDimensions());
		
		//Binding
		if(fromAZ.getBinding() != null) {
			si.setBinding(fromAZ.getBinding());
		}
		
		//No of pages
		if(fromAZ.getNoOfPages() != null) {
			si.setNoOfPages(fromAZ.getNoOfPages());
		}
		
		//Type
		if(fromAZ.getType() != null) {
			si.setType(fromAZ.getType());
		}
		
		//Review
		if(si.getUpdateReview() == true) {
			if(fromAZ.getReviewAsText() != null) {
				si.setReviewAsHTML(fromAZ.getReviewAsHTML());
				si.setReviewAsText(fromAZ.getReviewAsText());
				si.setIsReviewOnAZ(true);
			}
		}
		
		//Add authors
		if(si.getUpdateAuthors() == true) {
			if(syncAZAuthors == true || si.getAuthors().isEmpty()) {
				persistAuthors(si, fromAZ.getAuthors(), authorService);
			}
		}
		
		
		//Publisher
		if(si.getUpdatePublisher() == true) {
			if(si.getPublisher() == null && fromAZ.getPublisher() != null) {
				persistPublisher(si, fromAZ, azPublisherService);
			}
		}
	}

	private static void persistPublisher(StockItem si, StockItem stockItem, PublisherService azPublisherService) {
		azPublisherService.saveOrUpdate(stockItem.getPublisher());
	}
	
	private static void log(StockItem si) {
		logger.info("ID : " + si.getId());
		logger.info("ISBN : " + si.getIsbn());
		logger.info("Publisher : " + si.getPublisher());
		logger.info("Pub Date : " + si.getPublishedDate());
		logger.info("Title : " + si.getTitle());
		logger.info("Dimensions : " + si.getDimensions());
		logger.info("No of pages : " + si.getNoOfPages());
		logger.info("Sell Price : " + si.getSellPrice());
		logger.info("AZ Price : " + si.getPriceAtAZ());
		logger.info("AZ 3rd Party New : " + si.getPriceThirdPartyNew());
		logger.info("AZ 3rd Party 2nd Hand : " + si.getPriceThirdPartySecondHand());
		logger.info("AZ 3rd Party Collectable : " + si.getPriceThirdPartyCollectable());
		logger.info("Availability : " + si.getAvailability());
		logger.info("Binding : " + si.getBinding());
		logger.info("Type : " + si.getType());
		if(si.getAuthors().isEmpty()) {
			logger.info("No Authors!");
			//System.exit(0);
		}
		for(Author a : si.getAuthors()) {
			logger.info("Author : " + a.getName());
		}
	}

	/**
	 * Do shit thing with Map as I can't get Set to now add duplicates!!
	 * @param si
	 * @param authorsFromAZ
	 * @param authorService
	 */
	private static void persistAuthors(StockItem si, Set<Author> authorsFromAZ, AuthorService authorService) {
		
		//Some music cds don't easily give author, if no author is set use concatenated
//		if(si.getAuthors().isEmpty() && authorsFromAZ.isEmpty() && !si.getConcatenatedAuthors().isEmpty()) {
//			Author author = new Author(si.getConcatenatedAuthors());
//			Set<Author> authors = new HashSet<Author>();
//			authors.add(author);
//			authorsFromAZ = authors;
//		}
		
		
		Set<Author> authorsToAdd = new HashSet<Author>();
		Map<String, Author> authorsToAddMap = new HashMap<String, Author>();
		
		//Add all db stockitem authors, unless overriding
		if(si.getAuthors() != null) {
			for(Author a : si.getAuthors()) {
				authorsToAddMap.put(a.getName(), a);
			}
		}
		//Add all from az stockitem authors
		for(Author a : authorsFromAZ) {
			authorsToAddMap.put(a.getName(), a);
		}
		
		
		
		//Check if they exist or not
		for(Author author : authorsToAddMap.values()) {
			Author exists = authorService.findByName(author.getName());
			logger.info("Looking up author from map: " + author.getName() + " " + author.getId());
			if(author.getName().equals("Danielle Taana Smith")) {
				System.out.println("WHERE");
			}
			if(exists == null) { //This author isn't in the bookmarks database, save it
				logger.info("Adding new author from map: " + author.getName() + " " + author.getId());
				authorService.save(author);
				authorsToAdd.add(author);
				//si.getAuthors().add(author);
			} else {
				authorsToAdd.add(exists);
				//si.getAuthors().add(exists);
			}
		}
		si.setAuthors(authorsToAdd);
	}
}
