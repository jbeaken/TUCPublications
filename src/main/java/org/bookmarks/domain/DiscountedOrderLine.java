package org.bookmarks.domain;

import java.math.BigDecimal;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

@MappedSuperclass
public abstract class DiscountedOrderLine extends AbstractEntity {
	
	@NotNull
	@Min(value=0)
	@Max(value=100)
	private BigDecimal discount;
	
	@Min(value=0)
	@NotNull
	private BigDecimal discountedPrice;	
	
	@NumberFormat(style=Style.CURRENCY)
	@Min(value=0)
	@NotNull
	private BigDecimal sellPrice;
	
	@ManyToOne
	@NotNull
	private StockItem stockItem;	
	
	public StockItem getStockItem() {
		return stockItem;
	}

	public void setStockItem(StockItem stockItem) {
		this.stockItem = stockItem;
	}	
	
	public BigDecimal getDiscountedPrice() {
		return discountedPrice;
	}

	public void setDiscountedPrice(BigDecimal discountedPrice) {
		this.discountedPrice = discountedPrice;
	}
	
	public BigDecimal getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}	
	
	public BigDecimal getDiscount() {
		//Represented as a whole number, need to get invers
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}	
}
