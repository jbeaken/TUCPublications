package org.bookmarks.controller.bean;

import java.util.ArrayList;
import java.util.List;

import org.bookmarks.domain.Publisher;
import org.bookmarks.domain.StockItem;

public class WebScraperResultBean {
	private List<StockItem> stockItemsAdded = new ArrayList<StockItem>();
	private List<StockItem> stockItemsFailed = new ArrayList<StockItem>();
	private List<String> stockItemsNotOnAz = new ArrayList<String>();
	
	private String siteName;
	
	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public WebScraperResultBean(String siteName) {
		this();
		this.siteName = siteName;
	}
	
	public WebScraperResultBean() {
		super();
	}

	public List<StockItem> getStockItemsAdded() {
		return stockItemsAdded;
	}
	public void setStockItemsAdded(List<StockItem> stockItemsAdded) {
		this.stockItemsAdded = stockItemsAdded;
	}
	public List<StockItem> getStockItemsFailed() {
		return stockItemsFailed;
	}
	public void setStockItemsFailed(List<StockItem> stockItemsFailed) {
		this.stockItemsFailed = stockItemsFailed;
	}
	public List<String> getStockItemsNotOnAz() {
		return stockItemsNotOnAz;
	}
	public void setStockItemsNotOnAz(List<String> stockItemsNotOnAz) {
		this.stockItemsNotOnAz = stockItemsNotOnAz;
	}
	
	public String getMessage() {
		StringBuilder builder = new StringBuilder(1000);
		for(StockItem si : getStockItemsAdded()) {
			builder.append("Have added " + si.getIsbn() + " " + si.getTitle() + "\n");
		}
		for(StockItem si : getStockItemsFailed()) {
			builder.append("Failed " + si.getIsbn() + " " + si.getTitle() + "\n");
		}	
		for(String si : getStockItemsNotOnAz()) {
			builder.append("Not on AZ " + si + "\n");
		}	
		
		return builder.toString();
	}
}
