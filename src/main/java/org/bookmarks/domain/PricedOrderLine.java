package org.bookmarks.domain;

import java.math.BigDecimal;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

@MappedSuperclass
public abstract class PricedOrderLine extends OrderLine {
	
	
	@NumberFormat(style=Style.CURRENCY)
	@Min(value=0)
	@NotNull
	private BigDecimal price;
	
	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
}
