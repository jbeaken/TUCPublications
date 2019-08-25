package org.bookmarks.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TelephoneDirectory implements Serializable {
	
	@Column(name="mobileNumber")
	private String mobileNumber;
	
	@Column(name="homeNumber")
	private String homeNumber;
	
	@Column(name="workNumber")
	private String workNumber;
	
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getHomeNumber() {
		return homeNumber;
	}
	public void setHomeNumber(String homeNumber) {
		this.homeNumber = homeNumber;
	}
	public String getWorkNumber() {
		return workNumber;
	}
	public void setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
	}
}
