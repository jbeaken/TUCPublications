package org.bookmarks.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

@Embeddable
public class BookmarksAccount implements Serializable{

	@Enumerated(type=STRING)
	private SponsorType sponsorType;
	
	private Date sponsorStartDate;

	@NotNull
	private Boolean accountHolder = false;

	private String tsbMatch;

	private String tsbMatchSecondary;

	private Boolean paysInMonthly = false;

	private String comment;

	private BigDecimal currentBalance = new BigDecimal(0);

	public String getTsbMatch() {
		return tsbMatch;
	}

	public void setTsbMatch(String tsbMatch) {
		this.tsbMatch = tsbMatch;
	}


	public String getTsbMatchSecondary() {
		return tsbMatchSecondary;
	}

	public void setTsbMatchSecondary(String tsbMatchSecondary) {
		this.tsbMatchSecondary = tsbMatchSecondary;
	}	

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

	public SponsorType getSponsorType() {
		return sponsor;
	}

	public void setSponsorType(SponsorType sponsor) {
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
