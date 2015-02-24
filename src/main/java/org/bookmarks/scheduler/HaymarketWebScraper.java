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
import org.jfree.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HaymarketWebScraper extends WebScraper {
	
	private Logger logger = LoggerFactory.getLogger(HaymarketWebScraper.class);
	
	private String base = "http://www.haymarketbooks.org";

	/**
	 * Every day at 11pm
	 */
	@Override
	@Scheduled(cron = "0 0 2 * * *")
	public void scrape() throws Exception {
			
		if(isProduction() == false) return; //Should only run in production
	
		logger.info("Scraping Haymarket!!");
		WebScraperResultBean webScraperResultBean = new WebScraperResultBean("Haymarket");
		
		start(webScraperResultBean);
	}
	
	@Override
	/**
	 * Pages have pagination, last page is represented by
	 * <li class="pager-item"><a href="/subjects/Economics?page=3" title="Go to page 4" class="active">4</a></li>
	 * This is placed into intLastPage, needed as setting page parameter high still brings back last page. Need to know
	 * when to stop
	 * Some categories have no pagination (one page only) so need to check for this
	 */
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
		
			Elements dd = doc.select("a.imagecache");
			
			//Last page
			if(intLastPage == null) {
				Element elastPage = doc.select("li.pager-last a").first();
				if(elastPage == null) { //One page only
					intLastPage = 0; 
				} else {
					String lastPage = elastPage.attr("href").substring(20);
					intLastPage = Integer.parseInt(lastPage);
				}
				logger.info("Last page: " + intLastPage);
			}
			
			for(Element a : dd) {
				String href = a.attr("href");
				if(!href.contains("/pb")) continue;
				doc = Jsoup.connect(base + href).userAgent("Mozilla").timeout(136000).get();
				String isbn = doc.select("div#isbn span.value").first().text();
				logger.info(isbn);

				isbnSet.add(isbn);
			}
			page++;
			if(page > intLastPage) break;
		}
		return isbnSet;
	}

	public static void main(String[] args) {
		HaymarketWebScraper w = new HaymarketWebScraper();
		try {
			w.scrape();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
