package org.bookmarks.service;



import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.bookmarks.controller.bean.WebScraperResultBean;
import org.bookmarks.domain.CustomerOrder;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.scheduler.DailyReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

@Service
public class EmailServiceImpl implements EmailService {
	
	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

   public void setVelocityEngine(VelocityEngine velocityEngine) {
      this.velocityEngine = velocityEngine;
   }
	private JavaMailSender mailSender;
	private VelocityEngine velocityEngine;
	
	@Autowired private EmailReportService emailReportService;
	
	private static final String fromEmail = "beans@bookmarks.com";
	
	@Value("#{  emailProperties['email.dailyReport.cron'] }")
	private String cronJob;
	
	@Value("#{ emailProperties['email.address.buyer1'] }")
	private String buyer1Email;
	
	@Value("#{ emailProperties['email.address.buyer2'] }")
	private String buyer2Email;
	
	@Value("#{ emailProperties['email.address.mailorder'] }")
	private String mailOrderEmail;
	
	@Value("#{ emailProperties['email.address.admin'] }")
	private String adminEmail;	
	
	@Value("#{ emailProperties['email.address.manager'] }")
	private String managerEmail;	
	
	@Value("#{ emailProperties['email.address.publications'] }")
	private String publicationsEmail;

	private Logger logger = LoggerFactory.getLogger(DailyReport.class);

	@Override
	public void sendDailyReport(String emotion, String message) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom(fromEmail);
		
		//String[] emails = {buyer1Email, buyer2Email, mailOrderEmail, managerEmail, publicationsEmail, adminEmail};
		String[] emails = {buyer1Email, mailOrderEmail, managerEmail, publicationsEmail, adminEmail};
		//String[] emails = {"jack747@gmail.com"};
		msg.setTo(emails);
		
		msg.setSubject("Hello From Beans. Today I am feeling " + emotion);
		msg.setText(message);
		
		try{
			this.mailSender.send(msg);
		} catch(MailException ex) {
			// simply log it and go on...
			logger.error("Cannot send daily report", ex.getMessage());            
		}
	}
	
	@Override
	public void sendWebScraperReport(WebScraperResultBean webScraperResultBean) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom(fromEmail);
		
//		String[] emails = {buyer1Email, mailOrderEmail, managerEmail, adminEmail};
		String[] emails = {"jack747@gmail.com"};
		msg.setTo(emails);
		
		msg.setText(webScraperResultBean.getMessage());
		
		msg.setSubject("Beans reporting on web scrapper for  " + webScraperResultBean.getSiteName());

		try {
			this.mailSender.send(msg);
		} catch(MailException ex) {
			// simply log it and go on...
			logger.error("Cannot send web scraper", ex.getMessage());            
		}
	}	
	
	@Override
	public void emailErrorToJack(Exception exception) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromEmail);
        msg.setSubject("Error");
        msg.setTo("jack747@gmail.com");
        msg.setText(exception.getStackTrace().toString());
        try{
            this.mailSender.send(msg);
        }
        catch(MailException ex) {
            System.err.println(ex.getMessage());            
        }
	}

	@Override
	public void sendGardnersAvailabiltyReport(int count) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom(fromEmail);
		
		String[] emails = {adminEmail};
//		String[] emails = {buyer1Email, mailOrderEmail, managerEmail, adminEmail};
		msg.setTo(emails);
		
		msg.setSubject("Beans reporting on gardners availability, " + count + " items in stock");
		msg.setText("Beans reporting on gardners availability, " + count + " items in stock");

		try{
			this.mailSender.send(msg);
		} catch(MailException ex) {
			// simply log it and go on...
			logger.error("Cannot send web scraper", ex.getMessage());            
		}
	}

	@Override
	public void sendWipedCCDetails() {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom(fromEmail);
		
		String[] emails = {adminEmail};
//		String[] emails = {buyer1Email, mailOrderEmail, managerEmail, adminEmail};
		msg.setTo(emails);
//		msg.setTo("jack747@gmail.com");
		
		msg.setSubject("Beans has run wipe cc details programme successfully!");
		msg.setText("Beans has run wipe cc details programme successfully!");
		
		try{
			this.mailSender.send(msg);
		} catch(MailException ex) {
			// simply log it and go on...
			logger.error("Cannot wipe cc details email", ex.getMessage());            
		}
	}

	@Override
	public void sendDailyAttentionReport(String message) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom(fromEmail);
		
		String[] emails = {adminEmail, mailOrderEmail};
//		String[] emails = {buyer1Email, mailOrderEmail, managerEmail, adminEmail};
		msg.setTo(emails);
//		msg.setTo("jack747@gmail.com");
		
		msg.setSubject("Customer Orders need urgent attention!");
		msg.setText(message);
		
		try{
			this.mailSender.send(msg);
		} catch(MailException ex) {
			// simply log it and go on...
			logger.error("Cannot daily attention email", ex.getMessage());            
		}
		
	}

	@Override
	public void sendWebScraperFailedReport(WebScraperResultBean webScraperResultBean) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom(fromEmail);
		
//		String[] emails = {buyer1Email, mailOrderEmail, managerEmail, adminEmail};
		String[] emails = {"jack747@gmail.com"};
		msg.setTo(emails);
		
		msg.setText(webScraperResultBean.getMessage());
		
		msg.setSubject("FAILED : Beans reporting on web scrapper for  " + webScraperResultBean.getSiteName());

		try {
			this.mailSender.send(msg);
		} catch(MailException ex) {
			// simply log it and go on...
			logger.error("Cannot send web scraper", ex.getMessage());            
		}		
	}

	@Override
	public void sendCustomerOrderConfirmationEmail(final CustomerOrder customerOrder) {
		
//		String[] emails = {buyer1Email, mailOrderEmail, managerEmail, adminEmail};
		final String[] emails = {"jack747@gmail.com","info@bookmarksbookshop.co.uk"};
		StringBuilder builder = new StringBuilder(300);
		builder.append("<html>");
		builder.append("<a href='http://bookmarksbookshop.co.uk'><img src='http://bookmarksbookshop.co.uk/resources/images/bookmarks_logo_400.png'/></a>");
		builder.append("<h1>Confirmation of Order</h1>");
		builder.append("<br/><br/>");
		
		for(CustomerOrderLine col : customerOrder.getCustomerOrderline()) {
			builder.append("<div>" + col.getStockItem().getTitle() + "</div>");
		}
		
		builder.append("</html>");
		
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
	         public void prepare(MimeMessage mimeMessage) throws Exception {
	            MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
	            message.setTo(emails);
	            message.setFrom(fromEmail); // could be parameterized...
	            Map<String, Object> model = new HashMap<String, Object>();
	            model.put("customerOrder", customerOrder);
	            String text = VelocityEngineUtils.mergeTemplateIntoString(
	               velocityEngine, "email/customer-order-confirmation.vm", "utf-8", model);
	            message.setText(text, true);
	         }
	      };

//		try {
//			this.mailSender.send(preparator);
//		} catch(MailException ex) {
//			logger.error("Cannot send confirmation order", ex.getMessage());            
//		}	
	}
}
