package org.bookmarks.domain;

import java.math.BigDecimal;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

/**
 *
 */
@Entity
public class SaleOrReturnOrderLine extends OrderLine {


	@NotNull
	//Snap shot of current price
	private BigDecimal sellPrice;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="sor_id")
	private SaleOrReturn saleOrReturn;
	
	@Transient
	private Long originalAmount;
	
	@Transient
	private Long originalAmountRemainingWithCustomer;
	
	public Long getOriginalAmountRemainingWithCustomer() {
		return originalAmountRemainingWithCustomer;
	}

	public void setOriginalAmountRemainingWithCustomer(Long originalRemainingWithCustomer) {
		this.originalAmountRemainingWithCustomer = originalRemainingWithCustomer;
	}

	public Long getOriginalAmountSold() {
		return originalAmountSold;
	}

	public void setOriginalAmountSold(Long originalAmountSold) {
		this.originalAmountSold = originalAmountSold;
	}

	public Long getAmountRemainingWithCustomer() {
		return amountRemainingWithCustomer;
	}

	public void setAmountRemainingWithCustomer(Long amountRemainingWithCustomer) {
		this.amountRemainingWithCustomer = amountRemainingWithCustomer;
	}

	@Transient
	private Long originalAmountSold;
	
	//@NotNull
	private Long amountRemainingWithCustomer = 0l;
	
//	@NotNull
	private Long amountSold;


	public Long getAmountSold() {
		return amountSold;
	}

	public void setAmountSold(Long amountSold) {
		this.amountSold = amountSold;
	}

	public Long getOriginalAmount() {
		return originalAmount;
	}

	public void setOriginalAmount(Long originalAmount) {
		this.originalAmount = originalAmount;
	}

	public SaleOrReturnOrderLine() {
		super();
	}

	//Called from saleOrReturnController.addStockItem
	public SaleOrReturnOrderLine(StockItem stockItem) {
		this();
		setStockItem(stockItem);
		setSellPrice(stockItem.getSellPrice());
		setOriginalAmount(0l);
	}

	public BigDecimal getPrice() {
		return sellPrice.multiply(new BigDecimal(getAmount()));
	}

	public BigDecimal getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}

	public SaleOrReturn getSaleOrReturn() {
		return saleOrReturn;
	}

	public void setSaleOrReturn(SaleOrReturn saleOrReturn) {
		this.saleOrReturn = saleOrReturn;
	}
}
