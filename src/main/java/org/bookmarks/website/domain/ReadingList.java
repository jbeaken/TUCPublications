package org.bookmarks.website.domain;


import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;


//@Entity
public class ReadingList extends AbstractNamedEntity {

	//@OneToMany(mappedBy="readingList")
	//private Set<ReadingListStockItem>  readingListStockItems;
	
	@Column(name="is_on_website") private Boolean isOnWebsite = Boolean.TRUE;
	@Column(name="is_on_sidebar") private Boolean isOnSidebar = Boolean.FALSE;	
	
	//Constructors
	public ReadingList() {
		super();
	}
	
	public ReadingList(Long id) {
		this();
		setId(id);
	}	
	public ReadingList(Long id, String name) {
		this(id);
		setName(name);
	}
	
	//Accesors
/*	public List<StockItem> getStockItems() {
		return stockItems;
	}

	public void setStockItems(List<StockItem> stockItems) {
		this.stockItems = stockItems;
	}*/

	public Boolean getIsOnWebsite() {
		return isOnWebsite;
	}

	public void setIsOnWebsite(Boolean isOnWebsite) {
		this.isOnWebsite = isOnWebsite;
	}

	public Boolean getIsOnSidebar() {
		return isOnSidebar;
	}

	public void setIsOnSidebar(Boolean isOnSidebar) {
		this.isOnSidebar = isOnSidebar;
	}
/*
	public Set<ReadingListStockItem> getReadingListStockItems() {
		return readingListStockItems;
	}

	public void setReadingListStockItems(
			Set<ReadingListStockItem> readingListStockItems) {
		this.readingListStockItems = readingListStockItems;
	}
*/
}
