package org.bookmarks.report.bean;

import java.math.BigDecimal;

public class DailyReportBean {
	private Long noOfItemsInDatabase;
	private Long noOfItemsOnWebsite;
	private Long noOfItemsWithImages;
	private BigDecimal webOrderTotal; //Amount of money made from web orders
	
	
	public Long getNoOfItemsInDatabase() {
		return noOfItemsInDatabase;
	}
	public void setNoOfItemsInDatabase(Long noOfItemsInDatabase) {
		this.noOfItemsInDatabase = noOfItemsInDatabase;
	}
	public Long getNoOfItemsOnWebsite() {
		return noOfItemsOnWebsite;
	}
	public void setNoOfItemsOnWebsite(Long noOfItemsOnWebsite) {
		this.noOfItemsOnWebsite = noOfItemsOnWebsite;
	}
	public Long getNoOfItemsWithImages() {
		return noOfItemsWithImages;
	}
	public void setNoOfItemsWithImages(Long noOfItemsWithImages) {
		this.noOfItemsWithImages = noOfItemsWithImages;
	}
	public BigDecimal getWebOrderTotal() {
		return webOrderTotal;
	}
	public void setWebOrderTotal(BigDecimal webOrderTotal) {
		this.webOrderTotal = webOrderTotal;
	}
	
}
