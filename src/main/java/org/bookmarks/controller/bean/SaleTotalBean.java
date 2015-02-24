package org.bookmarks.controller.bean;

import java.math.BigDecimal;

public class SaleTotalBean {

	public SaleTotalBean() {
		super();
	}
	
	public SaleTotalBean(BigDecimal totalPrice, Long totalQuantity) {
		this();
		setTotalPrice(totalPrice);
		setTotalQuantity(totalQuantity);
	}

	private BigDecimal totalPrice;
	
	private Long totalQuantity;
	
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	public Long getTotalQuantity() {
		return totalQuantity;
	}
	
	public void setTotalQuantity(Long totalQuantity) {
		this.totalQuantity = totalQuantity;
	}	
	
}
