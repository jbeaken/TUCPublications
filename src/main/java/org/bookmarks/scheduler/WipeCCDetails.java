package org.bookmarks.scheduler;

import org.bookmarks.service.CustomerOrderLineService;
import org.bookmarks.service.CustomerOrderService;
import org.bookmarks.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WipeCCDetails extends AbstractScheduler {
	
	@Autowired private CustomerOrderLineService customerOrderLineService;
	
	private Logger logger = LoggerFactory.getLogger(WipeCCDetails.class);

	//Every Sunday at 6am
	@Scheduled(cron = "0 0 6 * * SUN")
//	@Scheduled(cron = "0 30 * * * ?")
	public void process() {
		
		if(isProduction() == false) return; //Should only run in production
		
		logger.info("Wiping CC details");
		
		customerOrderLineService.wipeCCDetails();
		
		emailService.sendWipedCCDetails();
	}

}
