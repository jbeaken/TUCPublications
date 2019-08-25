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
public class PolityWebScraper extends WebScraper {
	
	private Logger logger = LoggerFactory.getLogger(PolityWebScraper.class);
	
	@Override
//	@Scheduled(cron = "0 0 3 * * SAT")  //Saturday at 3am
	public void scrape() throws Exception {
			
			logger.info("Scraping Polity!!");
			
			if(isProduction() == false) return; //Should only run in production
		
			WebScraperResultBean webScraperResultBean = new WebScraperResultBean("Polity");
			
			Set<String> isbnSet = getIsbnList("http://www.polity.co.uk/searchres.asp");

			persist(isbnSet, "Politics", webScraperResultBean);
			
			log(webScraperResultBean);
			
			sendEmail(webScraperResultBean);
	}
	
	@Override
	protected Set<String> getIsbnList(String drilldownURL) throws IOException {
		int page = 0;
		Set<String> isbnSet = new HashSet<String>();
		
		Integer intLastPage = 47; //37 pages at the moment, needs to be future proof
		
		//cycle through pages
		while(true) {
			String url = drilldownURL;
			if(page > 0) url = url + "?page=" + page;
			logger.info("Drilldown url : " + url);
			
				
			//Drilldown
			Document doc = Jsoup.connect(url).userAgent("Mozilla").timeout(136000).get();
		
			Elements dd = doc.select("a.taggedLink");
			
			//Last page
//			if(intLastPage == null) {
//				Element elastPage = doc.select("li.pager-last a").first();
//				if(elastPage == null) { //One page only
//					//intLastPage = 0; 
//				} else {
//					String lastPage = elastPage.attr("href").substring(20);
//					intLastPage = Integer.parseInt(lastPage);
//				}
//				logger.info("Last page: " + intLastPage);
//			}
			
			for(Element a : dd) {
				String href = a.attr("href");
				if(!href.contains("book.asp?ref=")) continue;
				String isbn = href.substring(13);
				logger.info(isbn);

				isbnSet.add(isbn);
			}
			page++;
			if(page > intLastPage) break;
		}
		return isbnSet;
	}

	public static void main(String[] args) {
		PolityWebScraper w = new PolityWebScraper();
		try {
			w.scrape();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
