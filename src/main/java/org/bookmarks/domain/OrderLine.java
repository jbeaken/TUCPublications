package org.bookmarks.domain;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

@MappedSuperclass
public abstract class OrderLine extends AbstractEntity {

	@NotNull
	@Min(value=0)
	private Long amount = 1l;

	@ManyToOne
	@NotNull
	private StockItem stockItem;	

	public Long getAmount() {
		return amount;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
	}
	public StockItem getStockItem() {
		return stockItem;
	}
	public void setStockItem(StockItem stockItem) {
		this.stockItem = stockItem;
	}	

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}	
}
