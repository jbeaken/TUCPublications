package org.bookmarks.domain;

import java.math.BigDecimal;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.format.annotation.NumberFormat;

@Entity
@Table(name="supplierdeliveryline")
public class SupplierDeliveryLine extends PricedOrderLine {

	public SupplierDeliveryLine() {
		super();
		setAmount(0l);
		setPrice(new BigDecimal(0));
	}	

	private Long amountForCustomerOrders;
	
	@Min(value=0)
	@Max(value=100)
	@NotNull
	@NumberFormat(pattern="#.#")
	private BigDecimal discount;
	
	@Min(value=0)
	@NotNull
	@NumberFormat(pattern="#.##")
	private BigDecimal costPrice;	
	
	@Min(value=0)
	@NotNull
	@NumberFormat(pattern="#.##")
	private BigDecimal sellPrice;	
	
	@Min(value=0)
	@NotNull
	@NumberFormat(pattern="#.##")
	private BigDecimal publisherPrice;	

	//TRANSIENTS
	@Transient
	private Boolean hasCustomerOrderLines = Boolean.FALSE;
	
	@Transient
	private Boolean updateStockItemDiscount = Boolean.TRUE;		
	
	@Transient
	private Boolean updateStockItemSellPrice = Boolean.TRUE;		
	
	@Transient
	private Boolean updateStockItemCostPrice = Boolean.TRUE;

	@Transient
	private Boolean updateStockItemPublisherPrice = Boolean.TRUE;	

	public Boolean getUpdateStockItemCostPrice() {
		return updateStockItemCostPrice;
	}

	public void setUpdateStockItemCostPrice(Boolean updateStockItemCostPrice) {
		this.updateStockItemCostPrice = updateStockItemCostPrice;
	}

	public Long getAmountForCustomerOrders() {
		return amountForCustomerOrders;
	}

	public void setAmountForCustomerOrders(Long amountForCustomerOrders) {
		this.amountForCustomerOrders = amountForCustomerOrders;
	}	

	public Boolean getUpdateStockItemDiscount() {
		return updateStockItemDiscount;
	}

	public void setUpdateStockItemDiscount(Boolean updateStockItemDiscount) {
		this.updateStockItemDiscount = updateStockItemDiscount;
	}

	public Boolean getUpdateStockItemSellPrice() {
		return updateStockItemSellPrice;
	}

	public void setUpdateStockItemSellPrice(Boolean updateStockItemSellPrice) {
		this.updateStockItemSellPrice = updateStockItemSellPrice;
	}

	public Boolean getUpdateStockItemPublisherPrice() {
		return updateStockItemPublisherPrice;
	}

	public void setUpdateStockItemPublisherPrice(
			Boolean updateStockItemPublisherPrice) {
		this.updateStockItemPublisherPrice = updateStockItemPublisherPrice;
	}	

	public BigDecimal getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}

	public BigDecimal getPublisherPrice() {
		return publisherPrice;
	}

	public void setPublisherPrice(BigDecimal publisherPrice) {
		this.publisherPrice = publisherPrice;
	}

	public BigDecimal getCostPrice() {
		return costPrice;
	}

	public void setCostPrice(BigDecimal costPrice) {
		this.costPrice = costPrice;
	}	

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}		

	public Boolean getHasCustomerOrderLines() {
		return hasCustomerOrderLines;
	}

	public void setHasCustomerOrderLines(Boolean hasCustomerOrderLines) {
		this.hasCustomerOrderLines = hasCustomerOrderLines;
	}
}
