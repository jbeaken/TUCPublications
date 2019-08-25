package org.bookmarks.scheduler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.bookmarks.bean.ScraperISBNHolder;
import org.bookmarks.bean.ScraperUrl;
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
public class GuardianWebScraper extends WebScraper {
	
	private Logger logger = LoggerFactory.getLogger(GuardianWebScraper.class);
	
	private String base = "http://www.guardianbookshop.co.uk/BerteShopWeb/showSubCategories.do?";
	
	private ScraperUrl[] urlsToScrap = {
			new ScraperUrl("", "Politics"),  //Home page, with latest
			new ScraperUrl("category=1758", "Music"),
			new ScraperUrl("category=1758", "Music"),
			new ScraperUrl("category=1758&first=21&sort=null", "Music"), 
			new ScraperUrl("category=1758&first=31&sort=null", "Music"), 
			new ScraperUrl("category=1764", "Politics"),
			new ScraperUrl("category=1764&first=11&sort=null", "Politics"),
			new ScraperUrl("category=1764&first=21&sort=null", "Politics"),
			new ScraperUrl("category=1764&first=31&sort=null", "Politics"),
			new ScraperUrl("category=1826&intcmp=LatestReviews", "Politics")
	};

	/**
	 * Every day at 11pm
	 * Dont' run this! It brings back crap books
	 */
	@Override
	//@Scheduled(cron = "0 00 23 * * *")
	public void scrape() throws Exception {
			
		if(isProduction() == false) return; //Should only run in production
	
		logger.info("Scraping Guardian!!");
	
		WebScraperResultBean webScraperResultBean = new WebScraperResultBean("Guardian");
		
		Set<ScraperISBNHolder> scraperISBNHolderSet = getIsbnHolderList();
		
		persist(scraperISBNHolderSet, webScraperResultBean);
		
		log(webScraperResultBean);
		
		sendEmail(webScraperResultBean);
		
		//Now deal with new releases
		Set<String> newReleasesIsbn = getIsbnList("http://www.guardianbookshop.co.uk/BerteShopWeb/home.do");
		
		stockItemService.updateNewReleases(newReleasesIsbn);
	}
	


//	@Override
	/**
	 * Pages have pagination, last page is represented by
	 * <li class="pager-item"><a href="/subjects/Economics?page=3" title="Go to page 4" class="active">4</a></li>
	 * This is placed into intLastPage, needed as setting page parameter high still brings back last page. Need to know
	 * when to stop
	 * Some categories have no pagination (one page only) so need to check for this
	 */
	protected Set<ScraperISBNHolder> getIsbnHolderList() throws IOException {
		
		Set<ScraperISBNHolder> scraperISBNHolderSet = new HashSet<ScraperISBNHolder>();
		
		for(ScraperUrl scraperUrl : urlsToScrap) {
			//Drilldown
			Document doc = Jsoup.connect(base + scraperUrl.getUrl()).userAgent("Mozilla").timeout(136000).get();
		
			Elements isbnElements = doc.select("a[href*=ISBN]");
			
			int count = 0;
			for(Element e : isbnElements) {
				String href = e.attr("href");
				int index = href.indexOf("ISBN=") + 5;
				String isbn = href.substring(index);
				ScraperISBNHolder holder = new ScraperISBNHolder();
				holder.setIsbn(isbn);
				holder.setCategoryName(scraperUrl.getCategoryName());
				scraperISBNHolderSet.add(holder);
				
				logger.info(isbn);
				count++;
			}
			logger.info("Have got " + count + " isbns from url " + scraperUrl.getUrl() + " for category " + scraperUrl.getCategoryName());
		}
		return scraperISBNHolderSet;
	}

	public static void main(String[] args) {
		GuardianWebScraper w = new GuardianWebScraper();
		try {
			w.scrape();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	@Override
	protected Set<String> getIsbnList(String drilldownURL) throws IOException {
		//Drilldown
		Document doc = Jsoup.connect(drilldownURL).userAgent("Mozilla").timeout(136000).get();
	
		Elements isbnElements = doc.select("div#carousel_content ul li");
		
		Set<String> isbnSet = new HashSet<String>();
		
		int count = 0;
		for(Element e : isbnElements) {
			String href = e.select("a").first().attr("href");
//			logger.info(href);
//			String href = e.attr("href");
			int index = href.indexOf("ISBN=") + 5;
			String isbn = href.substring(index);
			isbnSet.add(isbn);
			logger.info(isbn);
			count++;
		}
		logger.info("Have got " + count + " isbns from url " + drilldownURL + " for new releases ");
		return isbnSet;
			
	}

}
