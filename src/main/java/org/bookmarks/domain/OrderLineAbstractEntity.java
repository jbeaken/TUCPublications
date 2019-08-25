package org.bookmarks.domain;

import java.util.Collection;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@MappedSuperclass
public abstract class OrderLineAbstractEntity  extends AbstractEntity {

	@OneToMany
	@Cascade(value={CascadeType.ALL})
private Collection<StockItem> stockItems;

public Collection<StockItem> getStockItems() {
	return stockItems;
}

public void setStockItems(Collection<StockItem> stockItems) {
	this.stockItems = stockItems;
}
	
	

	
}
