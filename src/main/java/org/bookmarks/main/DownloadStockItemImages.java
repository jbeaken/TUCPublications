package org.bookmarks.main;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.imgscalr.Scalr.*;

import org.bookmarks.domain.Author;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.WebsiteInfo;
import org.bookmarks.service.AZLookupService;
import org.bookmarks.service.AuthorService;
import org.bookmarks.service.StockItemService;
import org.imgscalr.Scalr.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * 
 * @author King Kong
 * mvn exec:java -Dexec.mainClass="org.bookmarks.main.DownloadStockItemImages"
 * 
 * Download all images using the img_url property of stockitem
 * If img_url = null, skip
 * 
 */
public class DownloadStockItemImages extends AbstractSpringBooter {
	
	static Logger logger = LoggerFactory.getLogger(DownloadStockItemImages.class);
	
	static boolean resize = false;
	
	public static void main(String[] args) {
		ApplicationContext applicationContext = getApplicationContext();

		StockItemService stockItemService = (StockItemService) applicationContext.getBean("stockItemServiceImpl");
		AZLookupService azLookupService = (AZLookupService) applicationContext.getBean("AZLookupServiceImpl");
		
		int count = 29000;
		int batch = 100;
		while(true) {
			Collection<StockItem> stockItems = stockItemService.getStockItemsWithPagination(count, batch);
			logger.info("Loaded batch of " + stockItems.size());
			if(stockItems.size() == 0) System.exit(0); //All done!
			for(StockItem si : stockItems) {
				logger.info("Download image for : " + si.toString() + " imageUrl " + si.getImageURL());
				if(si.getImageURL() != null) {
					try {
						azLookupService.saveImageToFileSystem(si.getImageURL(), si, resize);
						logger.info("******** Saved Image!!");
					} catch(Exception e) {
						logger.error("Error thrown while looking up, cannot get az info", e);
					}
				} else logger.info("******** Skipping....");
			}//end for
			logger.info("Finished batch, reloading next batch");
			count = count + batch;
		}//end while
	}
}
