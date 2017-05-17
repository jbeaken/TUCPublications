package org.bookmarks.domain;

import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.validation.constraints.NotNull;

import javax.persistence.Enumerated;
import javax.persistence.Column;

@Embeddable
public class SponsorshipDetails implements java.io.Serializable {

	@NotNull
	@Column(name="sponsorshipStartDate")
	private Date startDate;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name="sponsorshipType")
	private SponsorshipType type;

	@Column(columnDefinition="text", name = "sponsorshipComment")
	private String comment;

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getStartDate() {
		return startDate;
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

}
