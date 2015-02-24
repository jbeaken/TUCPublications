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
/**
 * Sends an email of similar format to DailyReport but only lists urgent 
 * customer orders
 * @author jack
 *
 */
@Component
public class DailyAttentionReport extends AbstractScheduler {
	
	@Autowired private EmailReportService emailReportService;
	
	@Autowired private EmailService emailService;
	
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
	
	private Logger logger = LoggerFactory.getLogger(DailyAttentionReport.class);
	
	private int noOfLinesOfPostiveEmotions = 225;
	
	private int noOfLinesInEmotionsFile = 225;

	//@Scheduled(cron = "0 30 18 * * ?")
	public void process() {
		
		if(isProduction() == false) return; //Should only run in production
		
		StringBuilder buffer = new StringBuilder(1000);
		int defcon = appendStatus(buffer, CustomerOrderLineStatus.OUT_OF_STOCK, 0);
		
		emailService.sendDailyAttentionReport(buffer.toString());
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
