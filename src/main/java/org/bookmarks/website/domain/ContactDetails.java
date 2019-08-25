package org.bookmarks.website.domain;


import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;

import org.hibernate.annotations.Type;

@Embeddable
public class ContactDetails implements java.io.Serializable {

	@Email @NotNull private String email;

//	@Transient @Email private String confirmEmail;

//  @Type(type="encryptedString")
	private String workNumber;

	private String homeNumber;

	private String mobileNumber;

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobile) {
		this.mobileNumber = mobile;
	}

	public String getWorkNumber() {
		return workNumber;
	}

	public void setWorkNumber(String work) {
		this.workNumber = work;
	}

	public String getHomeNumber() {
		return homeNumber;
	}

	public void setHomeNumber(String home) {
		this.homeNumber = home;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
