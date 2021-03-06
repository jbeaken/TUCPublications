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

		if (chipsGetOrders != true) {
			logger.info("Aborting auto getOrders(), turned off in configuration");
			return;
		}

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
