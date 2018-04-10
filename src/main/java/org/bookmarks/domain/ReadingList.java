package org.bookmarks.domain;


import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
@Table(name="reading_list")
public class ReadingList extends AbstractEntity {

	@NotNull
	@Column(unique=true)
	@Size(min=1, max = 255)
	private String name;

	@ManyToMany(cascade={javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.MERGE}, fetch=FetchType.EAGER)
	@JoinTable(name="reading_list_stockitem",
    joinColumns={@JoinColumn(name="reading_list_id", referencedColumnName="id")},
    inverseJoinColumns={@JoinColumn(name="stockItems_id", referencedColumnName="id")})
	@OrderColumn(name="stockItem_idx")
	private List<StockItem> stockItems;

	@Column(name="is_on_website") private Boolean isOnWebsite = Boolean.TRUE;
	@Column(name="is_on_sidebar") private Boolean isOnSidebar = Boolean.FALSE;

	public List<StockItem> getStockItems() {
		return stockItems;
	}

	public void setStockItems(List<StockItem> stockItems) {
		this.stockItems = stockItems;
	}

	public Boolean getIsOnWebsite() {
		return isOnWebsite;
	}

	public void setIsOnWebsite(Boolean isOnWebsite) {
		this.isOnWebsite = isOnWebsite;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getIsOnSidebar() {
		return isOnSidebar;
	}

	public void setIsOnSidebar(Boolean isOnSidebar) {
		this.isOnSidebar = isOnSidebar;
	}

}
