package org.bookmarks.website.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class LayoutStockItem extends AbstractEntity {
	
	@ManyToOne
	@JoinColumn(name="layout_id", nullable = false)
	private Layout layout;
	
	@ManyToOne
	@JoinColumn(name="stockitem_id", nullable = false)
	private StockItem stockItem;
	
	@NotNull
	private Integer position;

	public Layout getLayout() {
		return layout;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	public StockItem getStockItem() {
		return stockItem;
	}

	public void setStockItem(StockItem stockItem) {
		this.stockItem = stockItem;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}
	
	
}
