package org.bookmarks.website.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


@Entity
public class OrderLine extends AbstractEntity {

    @NotNull private String webReference;
    
	@ManyToOne //TODO name it stock_item_id
	@NotNull private StockItem stockItem;
	
	@NotNull private Integer quantity;
	
	@NotNull private Boolean isSecondHand = false;
	
	@Min(value=0)
	@NotNull private BigDecimal sellPrice; 
	
	@Min(value=0) private BigDecimal postage; //Shouldn't really be here but customerorder is not sent over the pipe
	
	@NotNull
	@ManyToOne
	@JoinColumn(name="customer_id")
	private WebsiteCustomer customer;
	
	//ACCESSORS
	public BigDecimal getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}
	public StockItem getStockItem() {
		return stockItem;
	}	
	public void setStockItem(StockItem stockItem) {
		this.stockItem = stockItem;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	public WebsiteCustomer getCustomer() {
		return customer;
	}
	public void setCustomer(WebsiteCustomer customer) {
		this.customer = customer;
	}
	
	//METHODS
	public BigDecimal getTotalPrice() {
		return stockItem.getSellPrice().multiply(new BigDecimal(quantity));
	}
	
	public void incrementQuantity(Integer value) {
		setQuantity(getQuantity() + value);
	}
	public BigDecimal getPostage() {
		return postage;
	}
	public void setPostage(BigDecimal postage) {
		this.postage = postage;
	}
	public String getWebReference() {
		return webReference;
	}
	public void setWebReference(String webReference) {
		this.webReference = webReference;
	}
	public Boolean getIsSecondHand() {
		return isSecondHand;
	}
	public void setIsSecondHand(Boolean isSecondHand) {
		this.isSecondHand = isSecondHand;
	}
}
