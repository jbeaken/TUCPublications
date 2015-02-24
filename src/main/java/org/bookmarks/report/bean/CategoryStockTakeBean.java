package org.bookmarks.report.bean;

import java.math.BigDecimal;

import org.bookmarks.domain.Category;

public class CategoryStockTakeBean {
	private Category category;
	
	private Long quantityInStock;
	
	private BigDecimal totalPublisherPrice;
	
	private BigDecimal totalSellPrice;
	
	/**
	 * Constructor for reportRepository.getCategoryStockTakeBeans()
	 * @param categoryName
	 * @param quantityInStock
	 * @param totalPublisherPrice
	 * @param totalSellPrice
	 */
	public CategoryStockTakeBean(String categoryName, Long quantityInStock, BigDecimal totalPublisherPrice, BigDecimal totalSellPrice) {
		Category category = new Category(categoryName);
		setCategory(category);
		setQuantityInStock(quantityInStock);
		setTotalPublisherPrice(totalPublisherPrice);
		setTotalSellPrice(totalSellPrice);
	}
	
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public Long getQuantityInStock() {
		return quantityInStock;
	}
	public void setQuantityInStock(Long quantityInStock) {
		this.quantityInStock = quantityInStock;
	}
	public BigDecimal getTotalPublisherPrice() {
		return totalPublisherPrice;
	}
	public void setTotalPublisherPrice(BigDecimal totalPublisherPrice) {
		this.totalPublisherPrice = totalPublisherPrice;
	}
	public BigDecimal getTotalSellPrice() {
		return totalSellPrice;
	}
	public void setTotalSellPrice(BigDecimal totalSellPrice) {
		this.totalSellPrice = totalSellPrice;
	}
}
