package org.bookmarks.ui;

import java.util.Locale;

import org.bookmarks.report.bean.PublisherStockTakeBean;
import org.displaytag.decorator.TableDecorator;

import org.springframework.format.number.CurrencyFormatter;

public class ReportPublisherStockTakeDecorator extends TableDecorator {
	CurrencyFormatter formatter = new CurrencyFormatter();
	
	public String getTotalPublisherPrice() {
		PublisherStockTakeBean publisherStockTakeBean = (PublisherStockTakeBean)getCurrentRowObject();
		return formatter.print(publisherStockTakeBean.getTotalPublisherPrice(),  Locale.UK);
	}	
	
	public String getTotalSellPrice() {
		PublisherStockTakeBean publisherStockTakeBean = (PublisherStockTakeBean)getCurrentRowObject();
		return formatter.print(publisherStockTakeBean.getTotalSellPrice(),  Locale.UK);
	}	
	
}
