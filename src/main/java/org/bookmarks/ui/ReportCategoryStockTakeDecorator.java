package org.bookmarks.ui;

import java.util.Locale;

import org.bookmarks.report.bean.CategoryStockTakeBean;
import org.displaytag.decorator.TableDecorator;

import org.springframework.format.number.CurrencyStyleFormatter;

public class ReportCategoryStockTakeDecorator extends TableDecorator {
	CurrencyStyleFormatter formatter = new CurrencyStyleFormatter();
	
	public String getTotalPublisherPrice() {
		CategoryStockTakeBean categoryStockTakeBean = (CategoryStockTakeBean)getCurrentRowObject();
		return formatter.print(categoryStockTakeBean.getTotalPublisherPrice(),  Locale.UK);
	}	
	
	public String getTotalSellPrice() {
		CategoryStockTakeBean categoryStockTakeBean = (CategoryStockTakeBean)getCurrentRowObject();
		return formatter.print(categoryStockTakeBean.getTotalSellPrice(),  Locale.UK);
	}	
	
}
