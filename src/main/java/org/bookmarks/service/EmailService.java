package org.bookmarks.service;

import org.bookmarks.controller.bean.WebScraperResultBean;
import org.bookmarks.domain.CustomerOrder;

public interface EmailService {

	void sendDailyReport(String emotion, String message);

	void sendWebScraperReport(WebScraperResultBean webScraperResultBean);

	void sendGardnersAvailabiltyReport(int count);

	void sendWipedCCDetails();

	void sendDailyAttentionReport(String string);

	void sendWebScraperFailedReport(WebScraperResultBean webScraperResultBean);

	void sendCustomerOrderConfirmationEmail(CustomerOrder customerOrder);
<<<<<<< HEAD
=======

	void sendCustomerOrderLinePostedEmail(CustomerOrderLine customerOrderLine);

	void sendErrorEmail(Exception exception, String subject);
>>>>>>> 407a726... Cleaned up basic auth
}
