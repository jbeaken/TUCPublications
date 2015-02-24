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
public class ZedWebScraper extends WebScraper {
	
	private Logger logger = LoggerFactory.getLogger(ZedWebScraper.class);
	
	private String base = "http://www.zedbooks.co.uk";

	/**
	 * Every day at 11pm
	 */
	@Override
	@Scheduled(cron = "0 0 23 * * SAT")
	public void scrape() throws Exception {
			
			if(isProduction() == false) return; //Should only run in production
		
			logger.info("Scraping Zed!!");
			logger.info("Search Url : " + base);
			
			Document doc = Jsoup.connect(base).userAgent("Mozilla").timeout(136000).get();
			 
			Elements categories = doc.select("span.field-content a[href^=/subjects/]");
			
			WebScraperResultBean webScraperResultBean = new WebScraperResultBean("Zed");
			
			
			//Now cycle through categories
			for(Element e : categories) {
				String href = e.attr("href");
				
				String categoryName = e.text();
				if(categoryName.isEmpty()) continue;
				
				String drilldownURL = base + href;
				
				//Have url of form /books/subject/ - ignore these
				logger.info("Category name : " + categoryName);
				
				Set<String> isbnSet = getIsbnList(drilldownURL);

				persist(isbnSet, categoryName, webScraperResultBean);
				
			} //End for
			
			log(webScraperResultBean);
			
			sendEmail(webScraperResultBean);
	}
	


	/**
	 * Pages have pagination, last page is represented by
	 * <li class="pager-item"><a href="/subjects/Economics?page=3" title="Go to page 4" class="active">4</a></li>
	 * This is placed into intLastPage, needed as setting page parameter high still brings back last page. Need to know
	 * when to stop
	 * Some categories have no pagination (one page only) so need to check for this
	 */
	@Override
	protected Set<String> getIsbnList(String drilldownURL) throws IOException {
		int page = 0;
		Set<String> isbnSet = new HashSet<String>();
		
		Integer intLastPage = null;
		
		//cycle through pages
		while(true) {
			String url = drilldownURL;
			if(page > 0) url = url + "?page=" + page;
			logger.info("Drilldown url : " + url);
			
				
			//Drilldown
			Document doc = Jsoup.connect(url).userAgent("Mozilla").timeout(136000).get();
		
			Elements img = doc.select("img.book-cover");
			
			//Last page
			if(intLastPage == null) {
				Element elastPage = doc.select("li.pager-item").last();
				if(elastPage == null) { //One page only
					intLastPage = 0; 
				} else {
					String lastPage = elastPage.select("a").text();
					intLastPage = Integer.parseInt(lastPage);
				}
				logger.info("Last page: " + intLastPage);
			}
			
			for(Element e : img) {
				String src = e.attr("src");
				String isbn = src.substring(35, 48);
				
				logger.info(isbn);

				isbnSet.add(isbn);
			}
			page++;
			if(page > intLastPage) break;
		}
		return isbnSet;
	}

	public static void main(String[] args) {
		ZedWebScraper w = new ZedWebScraper();
		try {
			w.scrape();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
