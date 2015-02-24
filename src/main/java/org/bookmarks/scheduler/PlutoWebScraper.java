package org.bookmarks.scheduler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.miscellaneous.PatternAnalyzer;
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

/**
 * Just scrapes new releases once a week,
 * One page, has collection of elements :
 * <a href="display.asp?K=9780745333991&dtspan=60%3A0&ds=New+Releases&sort=sort%5Fpluto&sf1=format%5Fcode&st1=bp&%3c%=PROP%25%3E&m=1&dc=7">
 * @author pod
 *
 */
@Component
public class PlutoWebScraper extends WebScraper {
	
	private Logger logger = LoggerFactory.getLogger(PlutoWebScraper.class);
	
	private String base = "http://www.plutobooks.com/results.asp?dtspan=60:0&ds=New%20Releases&SORT=sort_pluto&SF1=format_code&ST1=bp&%3C%=PROP%%3E";

	/**
	 * Every day at 11pm
	 */
	@Override
	@Scheduled(cron = "0 0 19 * * SUN")
	public void scrape() throws Exception {
			
			if(isProduction() == false) return; //Should only run in production
		
			logger.info("Scraping Pluto!!");
			logger.info("Search Url : " + base);
			
			WebScraperResultBean webScraperResultBean = new WebScraperResultBean("Pluto");
			
			Set<String> isbnSet = getIsbnList(base);
			
			persist(isbnSet, "", webScraperResultBean);
			
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
		
		Set<String> isbnSet = new HashSet<String>();
		
		Document doc = Jsoup.connect(base).userAgent("Mozilla").timeout(136000).get();
		 
		//Of form:
	    //display.asp?K=9780745334677
		//display.asp?K=9780745333991&dtspan=60%3A0&ds=New+Releases&sort=sort%5Fpluto&sf1=format%5Fcode&st1=bp&%3c%=PROP%25%3E&m=1&dc=7
		Elements anchorElements = doc.select("a[href^=display.asp");
		
		Pattern pattern = Pattern.compile("\\d{13}");
		
		for(Element a : anchorElements) {
			String href = a.attr("href");
			
			Matcher matcher = pattern.matcher(href);
			
			while(matcher.find()) {
				String isbn = matcher.group();
				System.out.println(isbn);
				isbnSet.add(isbn);
			}
		}
		return isbnSet;
	}

	public static void main(String[] args) {
		PlutoWebScraper w = new PlutoWebScraper();
		try {
			w.scrape();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
