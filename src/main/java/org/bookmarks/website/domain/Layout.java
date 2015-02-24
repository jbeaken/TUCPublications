package org.bookmarks.website.domain;

import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

public class Layout {
	
	private List<StockItem> bouncies;

	public List<StockItem> getBouncies() {
		return bouncies;
	}

	public void setBouncies(List<StockItem> bouncies) {
		this.bouncies = bouncies;
	}
}
