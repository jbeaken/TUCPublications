package org.bookmarks.main;

import java.util.Collection;

import org.bookmarks.domain.Author;
import org.bookmarks.domain.Publisher;
import org.bookmarks.domain.StockItem;
import org.bookmarks.service.PublisherService;
import org.bookmarks.service.StockItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;


/**
 * 
 * @author King Kong
 * mvn exec:java -Dexec.mainClass="org.bookmarks.main.CreateNoSyncedPublishers"
 * 
 */
public class CreateNoSyncedPublishers extends AbstractSpringBooter {
	
//	private static SessionFactory sessionFactory = getSessionFactory();	
	
	static Logger logger = LoggerFactory.getLogger(CreateNoSyncedPublishers.class);
	
	public static void main(String[] args) {
		ApplicationContext applicationContext = getApplicationContext();
		
		StockItemService stockItemService = (StockItemService) applicationContext.getBean("stockItemServiceImpl");
		PublisherService azPublisherService = (PublisherService) applicationContext.getBean("PublisherServiceImpl");
		

		//Reset sync
		//stockItemService.resetAZSync();
		while(true) {
			Collection<StockItem> stockItems = stockItemService.getNoAZPublishers(0, 100);
			if(stockItems.size() == 0) System.exit(0); //All done!
			logger.info("Loaded batch of " + stockItems.size());
			for(StockItem si : stockItems) {
				logger.info("****** Lookup of : " + si.toString());
				try {
					logger.info("Attaching publisher " + si.getPublisher().getName());
					persistPublisher(si, azPublisherService);
					log(si);
					stockItemService.update(si);
					
					logger.info("******** Success!!");
				} catch(Exception e) {
					logger.error("Error thrown while looking up, cannot persist publisher", e);

				}
			}//end for
			logger.info("Finished batch, reloading next batch");
		}//end while
	}


	private static void persistPublisher(StockItem si, PublisherService publisherService) {
		Publisher exists = publisherService.getByName(si.getPublisher().getName());
		if(exists == null) {
			exists = new Publisher(si.getPublisher().getName());
			publisherService.save(exists);
		}
		si.setPublisher(exists);
	}
	
	private static void log(StockItem si) {
		logger.info("Publisher : " + si.getPublisher());
		logger.info("Title : " + si.getTitle());
		logger.info("Dimensions : " + si.getDimensions());
		logger.info("Sell Price : " + si.getSellPrice());
		for(Author a : si.getAuthors()) {
			logger.info("Author : " + a.getName());
		}
	}
}
