package org.bookmarks.scheduler;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.bookmarks.service.ChipsService;
import org.bookmarks.service.CustomerOrderService;
import org.bookmarks.service.EmailService;
import org.bookmarks.website.domain.WebsiteCustomer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ChipsOrdersManager extends AbstractScheduler {

	@Autowired private EmailService emailService;

	@Autowired private ChipsService chipsService;

	@Autowired private CustomerOrderService customerOrderService;

	@Autowired	private Environment environment;

	private Logger logger = LoggerFactory.getLogger(ChipsOrdersManager.class);

	//Every 30 mins past the hour
	@Scheduled(cron = "0 30 * * * ?")
	public void process() throws ClientProtocolException, IOException {

		if(isProduction() == false) return; //Should only run in production

		List<WebsiteCustomer> chipsCustomers = null;

		try {
			chipsCustomers = chipsService.getOrders();
		} catch (Exception e) {
			logger.error("Cannot get orders from chips", e);
		}
<<<<<<< HEAD
<<<<<<< HEAD
=======
=======
>>>>>>> 1f51618... Fixing session search for supplier deliveries

		if(chipsCustomers == null) {
			logger.info("No orders to process, exiting...");
			return;
		}
>>>>>>> 1978e06... Fixing session search for supplier returns

		try {
			customerOrderService.saveChipsOrders(chipsCustomers);
		} catch (Exception e) {
			logger.error("Cannot save orders", e);
		}
	}

}
