package org.bookmarks.scheduler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javassist.tools.web.Webserver;

import org.bookmarks.controller.bean.WebScraperResultBean;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.Publisher;
import org.bookmarks.domain.StockItem;
import org.bookmarks.service.AZLookupService;
import org.bookmarks.service.CategoryService;
import org.bookmarks.service.EmailService;
import org.bookmarks.service.PublisherService;
import org.bookmarks.service.StockItemService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class VersoWebScraper extends WebScraper {

	private Logger logger = LoggerFactory.getLogger(VersoWebScraper.class);

	private String base = "http://www.versobooks.com";

	/**
	 * http://www.versobooks.com/books
	 * http://www.versobooks.com/books?page=2
	 * http://www.versobooks.com/books?page=3
	 * etc.
	 * @throws Exception
	 */
	// @Scheduled(cron = "0 30 21 * * *")
	@Override
	public void scrape() throws Exception {

		logger.info("Scraping Verso!!");

		if(isProduction() == false) return; //Should only run in production

		String url = base + "/books";

		logger.info("Search Url : " + url);

		Document doc = Jsoup.connect(url).userAgent("Mozilla").timeout(12000).get();

		Elements categories = doc.select("a[href^=/books/subject/]");

		WebScraperResultBean webScraperResultBean = new WebScraperResultBean("Verso");

		//Now cycle through categories
		for(Element e : categories) {
			String href = e.attr("href");
			String categoryName = e.text().replace("&amp;", "&");
			String drilldownURL = base + href;

			//Have url of form /books/subject/ - ignore these
			logger.info("Category name : " + categoryName);
			logger.info("Drilldown Url : " + drilldownURL);

			Set<String> isbnSet = getIsbnList(drilldownURL);

			persist(isbnSet, categoryName, webScraperResultBean);

		} //End for

		log(webScraperResultBean);

		sendEmail(webScraperResultBean);
	}

	protected Set<String> getIsbnList(String drilldownURL) throws IOException {
		int page = 1;
		Set<String> isbnSet = new HashSet<String>();

		//cycle through pages
		while(true) {
			String url = drilldownURL;
			if(page > 1) url = url + "?page=" + page;
			logger.info("Drilldown url : " + url);

			//Drilldown
			Document doc = Jsoup.connect(url)
					.userAgent("Mozilla")
					.timeout(12000)
					.get();

			Elements li = doc.select("li.edition_item span.image > a");
			if(li.isEmpty()) {
				//end of the line
				break;
			}
			for(Element e : li) {
				String href = e.attr("href");
				doc = Jsoup.connect(base + href).userAgent("Mozilla").timeout(12000).get();
				Element isbnElement = doc.select("div#metadata div p").get(1); //ISBN
				String isbn = isbnElement.text().substring(6);

				logger.info(isbn);

				isbnSet.add(isbn);
			}
			page++;
		}
		return isbnSet;
	}
}
