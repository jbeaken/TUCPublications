package org.bookmarks.service;

import org.bookmarks.controller.bean.WebScraperResultBean;
import org.bookmarks.domain.CustomerOrder;

public interface EmailService {

	//void receive(MimeMessage[] mimeMessage);

	void sendDailyReport(String emotion, String message);

	void emailErrorToJack(Exception ex);

	void sendWebScraperReport(WebScraperResultBean webScraperResultBean);

	void sendGardnersAvailabiltyReport(int count);

	void sendWipedCCDetails();

	void sendDailyAttentionReport(String string);

	void sendWebScraperFailedReport(WebScraperResultBean webScraperResultBean);

	void sendCustomerOrderConfirmationEmail(CustomerOrder customerOrder);
}
