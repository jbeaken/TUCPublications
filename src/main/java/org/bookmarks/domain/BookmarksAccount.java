package org.bookmarks.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

@Embeddable
public class BookmarksAccount implements Serializable{

	@NotNull
	private Boolean sponsor = false;

	@NotNull
	private Boolean accountHolder = false;

	private Boolean paysInMonthly = false;

	private String comment;

	private BigDecimal currentBalance = new BigDecimal(0);

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public BigDecimal getOpeningBalance() {
		return openingBalance;
	}

	public void setOpeningBalance(BigDecimal openingBalance) {
		this.openingBalance = openingBalance;
	}

	private BigDecimal amountPaidInMonthly;

	private BigDecimal openingBalance;

	public Boolean getSponsor() {
		return sponsor;
	}

	public void setSponsor(Boolean sponsor) {
		this.sponsor = sponsor;
	}

	public Boolean getAccountHolder() {
		return accountHolder;
	}

	public void setAccountHolder(Boolean accountHolder) {
		this.accountHolder = accountHolder;
	}

	public Boolean getPaysInMonthly() {
		return paysInMonthly;
	}

	public void setPaysInMonthly(Boolean paysInMonthly) {
		this.paysInMonthly = paysInMonthly;
	}

	public BigDecimal getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(BigDecimal amount) {
		this.currentBalance = amount;
	}

	public BigDecimal getAmountPaidInMonthly() {
		return amountPaidInMonthly;
	}

	public void setAmountPaidInMonthly(BigDecimal amountPaidInMonthly) {
		this.amountPaidInMonthly = amountPaidInMonthly;
	}



}
