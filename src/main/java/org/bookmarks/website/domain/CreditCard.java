package org.bookmarks.website.domain;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class CreditCard implements Serializable{
	private String creditCard1;
	
	private String creditCard2;
	
	private String creditCard3;
	
	private String creditCard4;

	private String expiryMonth;
	
	private String expiryYear;
	
	private String securityCode;
	
	private String nameOnCard;
	
	public String getNameOnCard() {
		return nameOnCard;
	}

	public void setNameOnCard(String nameOnCard) {
		this.nameOnCard = nameOnCard;
	}

	public String getCreditCard1() {
		return creditCard1;
	}

	public void setCreditCard1(String creditCard1) {
		this.creditCard1 = creditCard1;
	}

	public String getCreditCard2() {
		return creditCard2;
	}

	public void setCreditCard2(String creditCard2) {
		this.creditCard2 = creditCard2;
	}

	public String getCreditCard3() {
		return creditCard3;
	}

	public void setCreditCard3(String creditCard3) {
		this.creditCard3 = creditCard3;
	}

	public String getCreditCard4() {
		return creditCard4;
	}

	public void setCreditCard4(String creditCard4) {
		this.creditCard4 = creditCard4;
	}

	public String getExpiryMonth() {
		return expiryMonth;
	}

	public void setExpiryMonth(String expiryMonth) {
		this.expiryMonth = expiryMonth;
	}

	public String getExpiryYear() {
		return expiryYear;
	}

	public void setExpiryYear(String expiryYear) {
		this.expiryYear = expiryYear;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

}
