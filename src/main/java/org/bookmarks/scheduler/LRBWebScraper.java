package org.bookmarks.scheduler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.bookmarks.controller.bean.WebScraperResultBean;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.Publisher;
import org.bookmarks.service.AZLookupService;
import org.bookmarks.service.PublisherService;
import org.bookmarks.service.StockItemService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class LRBWebScraper extends WebScraper {
	
	private Logger logger = LoggerFactory.getLogger(LRBWebScraper.class);
	
	private String base = "http://www.londonreviewbookshop.co.uk";

	@Override
	//@Scheduled(cron = "0 0 23 * * ?")
	public void scrape() throws Exception {
	
		logger.info("Scraping LRB!!");
		
		if(isProduction() == false) return; //Should only run in production
	
		Publisher unknownPublisher = publisherService.get(2348l); //LRB
		unknownPublisher.setName("London Review of Books");
	
		logger.info("Search Url : " + base);
		
		WebScraperResultBean webScraperResultBean = new WebScraperResultBean("London Review of Books");
		 
		Document doc = Jsoup.connect(base + "/on-our-shelves").userAgent("Mozilla").timeout(136000).get();
		
		Elements categories = doc.select("h3 a");
		
		//Now cycle through categories
		for(Element e : categories) {
			String href = e.attr("href");
			
			String drilldownURL = base + href;
			
			Set<String> isbnSet = getIsbnList(drilldownURL);

			persist(isbnSet, "Politics", webScraperResultBean);
			
		} //End for
		
		log(webScraperResultBean);
		
		//sendEmail(webScraperResultBean);
	}
	


	@Override
	/**
	 */
	protected Set<String> getIsbnList(String drilldownURL) throws IOException {
		Set<String> isbnSet = new HashSet<String>();
		
		String url = drilldownURL;
		logger.info("Drilldown url : " + url);
			
		//Drilldown
		Document doc = Jsoup.connect(url)
				.userAgent("Mozilla")
				.timeout(12000)
				.get();
	
		Elements anchors = doc.select("figure a");
		for(Element e : anchors) {
			String href = e.attr("href");
			if(!href.contains("/book/")) continue;
			String isbn = href.substring(21, 34);
			
			logger.info(isbn);

			isbnSet.add(isbn);
		}
		return isbnSet;
	}

	public static void main(String[] args) {
		LRBWebScraper w = new LRBWebScraper();
		try {
			w.scrape();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
