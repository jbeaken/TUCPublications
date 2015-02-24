package org.bookmarks.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

@Embeddable
public class WebsiteInfo implements java.io.Serializable {

	private String titleOnWebsite;
	
	private String authorOnWebsite;
	
	//The puts, what should be on the website?
	
	
	@Length(max=5000)
	private String review1;
	
	@Length(max=500)
	private String review2;
	
	@Length(max=500)
	private String review3;
	public String getReview1() {
		return review1;
	}

	public String getReview2() {
		return review2;
	}

	public void setReview2(String review2) {
		this.review2 = review2;
	}

	public String getReview3() {
		return review3;
	}

	public void setReview3(String review3) {
		this.review3 = review3;
	}


	public void setReview1(String review1) {
		this.review1 = review1;
	}	
}
