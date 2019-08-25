package org.bookmarks.report.bean;

import java.math.BigDecimal;

import org.bookmarks.domain.Publisher;

public class PublisherStockTakeBean {
	private Publisher publisher;
	
	private Long quantityInStock;
	
	private BigDecimal totalPublisherPrice;
	
	private BigDecimal totalSellPrice;
	
	/**
	 * Constructor for reportRepository.getPublisherStockTakeBeans()
	 * @param publisherName
	 * @param quantityInStock
	 * @param totalPublisherPrice
	 * @param totalSellPrice
	 */
	public PublisherStockTakeBean(String publisherName, Long quantityInStock, BigDecimal totalPublisherPrice, BigDecimal totalSellPrice) {
		Publisher publisher = new Publisher(publisherName);
		setPublisher(publisher);
		setQuantityInStock(quantityInStock);
		setTotalPublisherPrice(totalPublisherPrice);
		setTotalSellPrice(totalSellPrice);
	}
	
	public Publisher getPublisher() {
		return publisher;
	}
	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
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
