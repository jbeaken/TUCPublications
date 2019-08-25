package org.bookmarks.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Contact {

	private String email;
	private String telephone;
	private String contactName;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getName() {
		return contactName;
	}
	public void setName(String name) {
		this.contactName = name;
	}
}
