package org.bookmarks.scheduler;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.bookmarks.bean.ScraperISBNHolder;
import org.bookmarks.controller.bean.WebScraperResultBean;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.StockItem;
import org.bookmarks.service.AZLookupService;
import org.bookmarks.service.CategoryService;
import org.bookmarks.service.ChipsService;
import org.bookmarks.service.EmailService;
import org.bookmarks.service.PublisherService;
import org.bookmarks.service.StockItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class WebScraper extends AbstractScheduler {
	
	@Autowired protected StockItemService stockItemService;
	
	@Autowired protected CategoryService categoryService;	
	
	@Autowired protected AZLookupService azLookupService;		
	
	@Autowired protected PublisherService publisherService;
	
	@Autowired protected EmailService emailService;
	
	@Autowired protected ChipsService chipsService;
	
	protected Logger logger = LoggerFactory.getLogger(WebScraper.class);
	
	private Category getCategory(String categoryName) {
		categoryName = categoryName.replace("&amp;", "&");
		
		if(categoryName.equals("Biography & Memoir")) { //Zed
			categoryName = "Biographies";
		}
		if(categoryName.equals("International Politics") || categoryName.equals("Political Theory")) {//Verso
			categoryName = "General Politics";
		}
		if(categoryName.equals("Film")) { //Verso
			categoryName = "Film, TV and Theatre";
		}	
		Category category = categoryService.getByName(categoryName);
		if(category == null) {
			category = new Category("Politics");
			category.setId(108l);
		}
		return category;
	}
	
	protected void start(WebScraperResultBean webScraperResultBean) {
		try {
			
			Set<String> isbnSet = getIsbnList("http://www.haymarketbooks.org/haymarket/all");
		
			persist(isbnSet, "Politics", webScraperResultBean);
			
			log(webScraperResultBean);
		
			sendEmail(webScraperResultBean);
			
		} catch (Exception e) {
			logger.error("Cannot scrape", e);
			sendFailureEmail(webScraperResultBean);
		}
	}
	
	protected void persist(Set<ScraperISBNHolder> scraperISBNHolderSet, WebScraperResultBean webScraperResultBean) {
		List<StockItem> stockItemsAdded = webScraperResultBean.getStockItemsAdded();
		List<StockItem> stockItemsFailed = webScraperResultBean.getStockItemsFailed();
		List<String> stockItemsNotOnAz = webScraperResultBean.getStockItemsNotOnAz();
		
		
		//Persist
		for(ScraperISBNHolder holder : scraperISBNHolderSet) {
			
			String isbn = holder.getIsbn();
			logger.info("Checking existence of " + isbn);
			if(isbn.length() != 10 && isbn.length() != 13) {
				logger.info("Invalid isbn!");
				continue;
			}
			boolean exists = stockItemService.exists(isbn);
			if(exists) {
				logger.info("Already exists in database!");
				continue;
			} else {
				logger.info("ISBN isn't in database!");
			}
			StockItem si = null;
			try {
				si = azLookupService.lookupWithJSoup(isbn);
			} catch (Exception e) {
				logger.error("Cannot lookup", e);
			}
			if(si == null ) {
				logger.info("Cannot lookup at az for " + isbn);
				stockItemsNotOnAz.add(isbn);
				continue;
			}
			if(si.getPublisherPrice() == null) {
				logger.info("Cannot get publisher price for " + si.getTitle());
				stockItemsFailed.add(si);
				continue;
			}
			//Category can be set by lookup
			if(si.getCategory() == null) {
				Category category = getCategory(holder.getCategoryName());
				si.setCategory(category);
			}
			si.setQuantityToKeepInStock(0l);
//			if( si.getPublisher() == null) {
//				si.setPublisher(webScraperResultBean.getPublisher());
//			}
			si.setIsStaffPick(false);
			
			stockItemService.create(si);
			
			stockItemsAdded.add(si);
			
			//Sync with chips
			try {
				chipsService.syncStockItemWithChips(si);
				logger.info("Have successfully added " + si.getTitle() + " to chips");
			} catch (Exception e) {
				logger.error("Cannot sync with chips: " + si.getIsbn() + " - " + si.getTitle(), e);
				stockItemsFailed.add(si);
			} 
		}
		
	}	
	
	protected void persist(Set<String> isbnSet, String categoryName, WebScraperResultBean webScraperResultBean) throws Exception {
		List<StockItem> stockItemsAdded = webScraperResultBean.getStockItemsAdded();
		List<StockItem> stockItemsFailed = webScraperResultBean.getStockItemsFailed();
		List<String> stockItemsNotOnAz = webScraperResultBean.getStockItemsNotOnAz();
		
		Category category = getCategory(categoryName);
		
		//Persist
		for(String isbn : isbnSet) {
			logger.info("Checking existence of " + isbn);
			if(isbn.length() != 10 && isbn.length() != 13) {
				logger.info("Invalid length of isbn!");
				continue;
			}			
			boolean exists = stockItemService.exists(isbn);
			if(exists) {
				logger.info("Already exists in database!");
				continue;
			}
			StockItem si = null;
			try {
				si = azLookupService.lookupWithJSoup(isbn);
			} catch (Exception e) {
				logger.error("Cannot lookup", e);
			}
			if(si == null ) {
				logger.info("Cannot lookup at az for " + isbn);
				stockItemsNotOnAz.add(isbn);
				continue;
			}
			if(si.getPublisherPrice() == null) {
				logger.info("Cannot get publisher price for " + si.getTitle());
				stockItemsFailed.add(si);
				continue;
			}
			//Category can be set by lookup
			if(si.getCategory() == null) {
				//It hasn't been, set to default (Politics)
				si.setCategory(category);
			}
			si.setQuantityToKeepInStock(0l);
//			if( si.getPublisher() == null) {
//				si.setPublisher(webScraperResultBean.getPublisher());
//			}
			si.setIsStaffPick(false);
			
			stockItemService.create(si);
			
			stockItemsAdded.add(si);
			
			//Sync with chips
			try {
				chipsService.syncStockItemWithChips(si);
				logger.info("Have successfully added " + si.getTitle() + " to chips");
			} catch (Exception e) {
				logger.error("Cannot sync with chips: " + si.getIsbn() + " - " + si.getTitle(), e);
				stockItemsFailed.add(si);
			} 
		}
	}
	
	protected void log(WebScraperResultBean webScraperResultBean) {
		for(StockItem si : webScraperResultBean.getStockItemsAdded()) {
			logger.info("Have added " + si.getTitle());
		}
		for(StockItem si : webScraperResultBean.getStockItemsFailed()) {
			logger.info("Failed " + si.getIsbn() + " "+ si.getTitle());
		}	
		for(String si : webScraperResultBean.getStockItemsNotOnAz()) {
			logger.info("Not on AZ " + si);
		}	
	}
	
	protected void sendEmail(WebScraperResultBean webScraperResultBean) {
		emailService.sendWebScraperReport(webScraperResultBean);
	}
	
	protected void sendFailureEmail(WebScraperResultBean webScraperResultBean) {
		emailService.sendWebScraperFailedReport(webScraperResultBean);
	}	


	protected abstract Set<String> getIsbnList(String drilldownURL) throws IOException;

	public abstract void scrape() throws Exception;
}
