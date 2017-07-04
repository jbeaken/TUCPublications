package org.bookmarks.scheduler;

import java.io.IOException;
import java.util.Collection;

import org.apache.http.client.ClientProtocolException;
import org.bookmarks.service.ChipsService;
import org.bookmarks.service.EmailService;
import org.bookmarks.website.domain.WebsiteCustomer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ChipsOrdersManager extends AbstractScheduler {

	@Autowired private EmailService emailService;

	@Autowired private ChipsService chipsService;

	@Value("#{ applicationProperties['chips.get.orders'] }")
	private Boolean chipsGetOrders;

	private Logger logger = LoggerFactory.getLogger(ChipsOrdersManager.class);

	//Every 30 mins past the hour
	@Scheduled(cron = "0 30 * * * ?")
	public void process() throws ClientProtocolException, IOException {

		logger.info("Auto request for getOrders started");

<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
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
=======
		if (chipsGetOrders != true) {
			logger.info("Aborting getOrders(), turned off in configuration");
>>>>>>> 407a726... Cleaned up basic auth
=======
		if (chipsDownloadOrders != true) {
=======
		if (chipsGetOrders != true) {
>>>>>>> 8e7be17... Adding code to turn off get orders
			logger.info("Aborting auto getOrders(), turned off in configuration");
>>>>>>> 0b8750a... Converted getOrders to use Spring RestTemplate
			return;
		}
>>>>>>> 1978e06... Fixing session search for supplier returns

		try {

			Collection<WebsiteCustomer>  chipsCustomers = chipsService.getOrders();

			logger.info("Have retrieved " + chipsCustomers.size() + " orders from chips");

			logger.info("Auto request for getOrders successful");

		} catch (Exception e) {
			logger.error("Cannot get orders from chips", e);
			emailService.sendErrorEmail(e, "Cannot get orders from chips");
		}


	}

}
