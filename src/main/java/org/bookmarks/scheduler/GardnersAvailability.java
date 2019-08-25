package org.bookmarks.scheduler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bookmarks.controller.bean.WebScraperResultBean;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.Publisher;
import org.bookmarks.domain.StockItem;
import org.bookmarks.service.AZLookupService;
import org.bookmarks.service.CategoryService;
import org.bookmarks.service.ChipsService;
import org.bookmarks.service.EmailService;
import org.bookmarks.service.PublisherService;
import org.bookmarks.service.StockItemService;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
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
public class GardnersAvailability extends AbstractScheduler {

	@Autowired protected StockItemService stockItemService;

	@Autowired private EmailService emailService;

	protected Logger logger = LoggerFactory.getLogger(GardnersAvailability.class);

	private String url = "http://www.gardners.com/gardners/StockSearch.aspx";


	/**
	 * POST /gardners/StockSearch.aspx HTTP/1.1
	 * @throws IOException
	 */
	//@Scheduled(cron = "0 0 3 * * WED,SAT")//Three in the morning on Wednesday and Saturday
	public void checkAvailability() throws IOException {

		//if(isProduction() == false) return; //Should only run in production

		//Reset
		stockItemService.resetGardnersStockLevel();

		int offset = 0;
		int count = 0;
		while(true) {
			Collection<String> isbns = stockItemService.getISBNsWithPagination(offset, 100);
			offset += 100;

			if(isbns.isEmpty()) break;

			for(String isbn: isbns) {
				Long noInStock = 0l;

				//Skip if doesn't start with 978
				if(!isbn.startsWith("978")) {
					continue;
				}

				Document doc = Jsoup.connect(url)
						.userAgent("Mozilla")
						.timeout(12000)
						.data("qu", isbn)
						.data("submit.x", "35")
						.data("submit.y", "8")
						.post();

				Element elementNoInStock = doc.select("span#ctl00_CP_uxAvailabilityLabel").first();
				if(elementNoInStock != null) {
					noInStock = Long.parseLong(elementNoInStock.text());
				}
				if(noInStock > 0) {
					stockItemService.setGardnersStockLevel(isbn, noInStock);
					logger.debug("Count : " + count + " ISBN: " + isbn + " -- " + noInStock);
					count++;
				}
			}
		}
		emailService.sendGardnersAvailabiltyReport(count);
	}

	protected void login() throws IOException {
		Connection.Response res = Jsoup.connect(url)
			    .method(Method.GET)
			    .execute();

		Map<String, String> cookies = res.cookies();

		Connection connection = Jsoup.connect(url);

		 for (Map.Entry<String, String> cookie : cookies.entrySet()) {
		     connection.cookie(cookie.getKey(), cookie.getValue());
		 }
		 connection.data("qu", "9780007169900").data("submit.x", "35").data("submit.y", "8");

	   Document doc=  connection.post();
	}

	public static void main(String[] args) {
		GardnersAvailability ga = new GardnersAvailability();
		try {
			ga.login();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
