package org.bookmarks.domain;

import java.io.Serializable;
import javax.persistence.Embeddable;
import java.math.BigDecimal;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

@Embeddable
public class SupplierAccount implements Serializable {

	private Long minimumOrderQuantity;

	@NumberFormat(style=Style.CURRENCY)
	private BigDecimal minimumOrderPrice;

	private String vatNumber;

	private String accountNumber;

	public String getVatNumber() {
		return vatNumber;
	}

	public void setVatNumber(String vatNumber) {
		this.vatNumber = vatNumber;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public Long getMinimumOrderQuantity() {
		return minimumOrderQuantity;
	}

	public void setMinimumOrderQuantity(Long minimumOrderQuantity) {
		this.minimumOrderQuantity = minimumOrderQuantity;
	}

	public BigDecimal getMinimumOrderPrice() {
		return minimumOrderPrice;
	}

	public void setMinimumOrderPrice(BigDecimal minimumOrderPrice) {
		this.minimumOrderPrice = minimumOrderPrice;
	}
}
