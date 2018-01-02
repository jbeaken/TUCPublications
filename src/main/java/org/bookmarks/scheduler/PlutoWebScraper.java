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
* <div class="book-wrapper">
* <div class="image-wrapper">
* <p class="sp__the-cover"><a href='/9780745399850/making-workers'>
* <img data-baseline-images="image" src="https://d28akss8xdipta.cloudfront.net/resized/width-298/path-assets/covers/v1/9780745399850.jpg" alt="Making Workers" /></a></p></div>
* <a class="pp-link__title" href="/9780745399850/making-workers">
* <h2 class="pp-book__title">Making Workers</h2>
* <h3 class="pp-book__subtitle">Radical Geographies of Education</h3> </a>
* <p class="pp-book__author">Katharyne Mitchell</p>
* <div class="sp__the-description">Shines a light on how modern education shapes students into becoming compliant workers.</div>
* <p class="sp__the-price ">£18.99</p>
* <a class="more-link" href="/9780745399850/making-workers">View</a></div>*
 */
@Component
public class PlutoWebScraper extends WebScraper {
	
	private Logger logger = LoggerFactory.getLogger(PlutoWebScraper.class);
	
	private String base = "https://www.plutobooks.com/books/?page_number=1";

	/**
	 * Every day at 11pm
	 */
	@Override
	@Scheduled(cron = "0 50 22 * * TUE")
	public void scrape() throws Exception {

		logger.info("Scraping Pluto!! Search Url : {}", base);
			
		if(isProduction() == false) return; //Should only run in production
				
		WebScraperResultBean webScraperResultBean = new WebScraperResultBean("Pluto");
		
		Set<String> isbnSet = getIsbnList(base);
		
		persist(isbnSet, "", webScraperResultBean);
		
		log(webScraperResultBean);
		
		sendEmail(webScraperResultBean);
	}
	


	/**
	 * Pages have pagination, last page is represented by
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
		Elements anchorElements = doc.select("div.book-wrapper");
		
		Pattern pattern = Pattern.compile("\\d{13}");
		
		for(Element e : anchorElements) {
			Element a = e.select("a.pp-link__title").first();
			String href = a.attr("href");

			Matcher matcher = pattern.matcher(href);
			
			while(matcher.find()) {
				String isbn = matcher.group();
				
				String price = e.select("p.sp__the-price").first().text();

				logger.debug("Adding {} with price {}", isbn, price);

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
