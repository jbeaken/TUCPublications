package org.bookmarks.domain;

import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;


@Entity
@Table(name="stockitem_sales")
public class StockItemSales extends AbstractEntity {

	public StockItemSales() {
		super();
	}
	
	public StockItemSales(Long id) {
		this();
		setId(id);
	}	
	
	@NotNull
	private Integer year;

	@NotNull
	private String sales;

	@Transient
	private List<Long> salesList = new ArrayList<Long>();

	@ManyToOne
	@JoinColumn(name="stockitem_id", nullable=false)
	private StockItem stockItem;

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getSales() {
		return sales;
	}

	public void setSales(String sales) {
		this.sales = sales;
	}

	public StockItem getStockItem() {
		return stockItem;
	}

	public void setStockItem(StockItem stockItem) {
		this.stockItem = stockItem;
	}

	public List<Long> getSalesList() {
		return salesList;
	}

	public void setSalesList(List<Long> salesList) {
		this.salesList = salesList;
	}
}