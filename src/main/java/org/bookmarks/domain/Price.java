package org.bookmarks.domain;

import java.math.BigDecimal;

public class Price {
	
	public Price() {
		super();
	}

	public Price(long price) {
		this();
		setPrice(new BigDecimal(price));
	}
	
	private BigDecimal price;

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
}
