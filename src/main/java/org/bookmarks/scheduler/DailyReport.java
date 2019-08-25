package org.bookmarks.scheduler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.CustomerOrderLineStatus;
import org.bookmarks.domain.StockItem;
import org.bookmarks.report.bean.DailyReportBean;
import org.bookmarks.service.EmailReportService;
import org.bookmarks.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DailyReport extends AbstractScheduler {
	
	@Autowired private EmailReportService emailReportService;
	
	@Autowired private EmailService emailService;
	
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
	
	private Logger logger = LoggerFactory.getLogger(DailyReport.class);
	
	private int noOfLinesOfPostiveEmotions = 225;
	
	private int noOfLinesInEmotionsFile = 225;

	//Everyday at 4pm
	@Scheduled(cron = "0 0 16 * * ?")
	public void process() {
		
		if(isProduction() == false) return; //Should only run in production
		
		StringBuilder buffer = new StringBuilder(1000);
		
		DailyReportBean dailyReportBean = emailReportService.getDailyReportBean();
		
		buffer.append("No. of items in database : "  + dailyReportBean.getNoOfItemsInDatabase() + "\n");
		buffer.append("No. of items on website : "  + dailyReportBean.getNoOfItemsOnWebsite() + "\n");
		buffer.append("No. of items with images : "  + dailyReportBean.getNoOfItemsWithImages() + "\n");
		buffer.append("Total sales from web : "  + NumberFormat.getInstance().format(dailyReportBean.getWebOrderTotal().doubleValue()) + "\n\n");
		
		Integer pissedOffLevel = 0;
		
	    pissedOffLevel += appendStatus(buffer, CustomerOrderLineStatus.READY_TO_POST, 0);
	    pissedOffLevel += appendStatus(buffer, CustomerOrderLineStatus.OUT_OF_STOCK, 0);
		appendStatus(buffer, CustomerOrderLineStatus.ON_ORDER, 5);
		appendStatus(buffer, CustomerOrderLineStatus.PENDING_ON_ORDER, 3);
//		pissedOffLevel += appendStatus(buffer, CustomerOrderLineStatus.RESEARCH, 0);
		
		appendStockItemsBelowKeepInStockLevel(buffer);
		
		String emotion = getBeansEmotion(pissedOffLevel);
		
		logger.info("--------------------------------");
		logger.info("Sending daily report : {}", emotion);
		logger.info("No. of items in database : "  + dailyReportBean.getNoOfItemsInDatabase() + "\n");
		logger.info("No. of items on website : "  + dailyReportBean.getNoOfItemsOnWebsite() + "\n");
		logger.info("No. of items with images : "  + dailyReportBean.getNoOfItemsWithImages() + "\n");
		logger.info("Total sales from web : "  + NumberFormat.getInstance().format(dailyReportBean.getWebOrderTotal().doubleValue()) + "\n\n");
		logger.info("--------------------------------");
		
		emailService.sendDailyReport(emotion, buffer.toString());
	}

	private String getBeansEmotion(Integer pissedOffLevel) {
		int index =  new Random().nextInt(noOfLinesOfPostiveEmotions) + pissedOffLevel;
		String emotion = "relaxed";
		if(index > noOfLinesInEmotionsFile) index = noOfLinesInEmotionsFile;
		
		ClassPathResource resource = new ClassPathResource("/META-INF/emotions.txt");
		try {
			Reader reader = new FileReader(resource.getFile());
			BufferedReader buffer = new BufferedReader(reader);
			for(int i = 0; i < index + pissedOffLevel; i++) {
				buffer.readLine();
			}
			emotion = buffer.readLine();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(emotion == null) emotion = "relaxed";
		return emotion;
	}
	
	private int appendStatus(StringBuilder buffer, CustomerOrderLineStatus status, int daysOverdue) {
		buffer.append("**********************************************************\n");
		buffer.append("Orders with status: " + status.getDisplayName() + "\n");
		List<CustomerOrderLine> cols = emailReportService.getCustomerOrderLines(status, daysOverdue);
		
		if(cols.size() != 0) {
			buffer.append("Id\tCreate\t\t\tName\t\tTitle\n");
			for(CustomerOrderLine col : cols) {
				buffer.append(col.getId() + "\t"
						+ dateFormatter.format(col.getCreationDate()) + "\t"
						+ col.getCustomer().getFullName() + "\t\t"); 	
				if(status == CustomerOrderLineStatus.RESEARCH) {
					buffer.append(col.getNote() + "\n");
				} else {
					buffer.append(col.getStockItem().getTitle() + "\n");
				}
			}
		}
		buffer.append("\n");
		return cols.size();
	}
	
	private void appendStockItemsBelowKeepInStockLevel(StringBuilder buffer) {
		buffer.append("**********************************************************\n");
		buffer.append("Stock fallen below keep in stock level\n\n");
		Collection<StockItem> sis = emailReportService.getStockItemsBelowKeepInStockLevel();
		if(sis.size() != 0) {
			buffer.append("Title\tISBN\t\t\tStock Level\t\tKeep in Stock Level\n");
			for(StockItem si : sis) {
				buffer.append(si.getTitle() + "\n"
					+ si.getIsbn() + "\t"
					+ si.getQuantityInStock() + "\t"
					+ si.getQuantityToKeepInStock() + " *********************\n");
			}
		}
	}
}
