package org.bookmarks.domain;

import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.validation.constraints.NotNull;

import javax.persistence.Enumerated;
import javax.persistence.Column;


import org.springframework.format.annotation.DateTimeFormat;

@Embeddable
public class SponsorshipDetails implements java.io.Serializable {

	@NotNull
	@Column(name="sponsorshipStartDate")
	@DateTimeFormat(pattern="dd/MM/yy")
	private Date startDate;

	@NotNull
	@Column(name="sponsorshipEndDate")
	@DateTimeFormat(pattern="dd/MM/yy")
	private Date endDate;	

	//@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name="sponsorshipType")
	private SponsorshipType type;

	//@NotNull
	@Column(name="sponsorshipAmount")
	private Integer amount;

	@Column(columnDefinition="text", name = "sponsorshipComment")
	private String comment;

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Integer getAmount() {
		return amount;
	}	

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}


	public void setType(SponsorshipType type) {
		this.type = type;
	}

	public SponsorshipType getType() {
		return type;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getEndDate() {
		return endDate;
	}	

}
