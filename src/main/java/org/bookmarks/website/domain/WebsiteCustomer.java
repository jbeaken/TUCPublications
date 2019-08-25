package org.bookmarks.website.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


//@Entity
public class WebsiteCustomer extends AbstractEntity {

    @OneToMany(cascade = CascadeType.ALL, mappedBy="customer", fetch=FetchType.EAGER)
    private Set<OrderLine> orders = new HashSet<OrderLine>();
    
    private Long beansId;
    
	@NotNull
	@Size(min=1, max = 55)
	@Column(name="firstName")
	private String firstName;
	
	@NotNull
	@Size(min=1, max = 55)
	@Column(name="lastName")
	private String lastName;
	
	private CreditCard creditCard;
	
	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(name="paymentType")
	private PaymentType paymentType = PaymentType.CREDIT_CARD;
	
	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(name="deliveryType")
	private DeliveryType deliveryType = DeliveryType.MAIL;	
	
    @Embedded
    private ContactDetails contactDetails;
    
    @Embedded
    private Address address;
    
    //Constructors
    
    public WebsiteCustomer() {
    	super();
    	setAddress(new Address());
    	setCreditCard(new CreditCard());
    }

	public String getFullName() {
		return getFirstName() + " " + getLastName();
	}

	public Set<OrderLine> getOrders() {
		return orders;
	}

	public void setOrders(Set<OrderLine> orders) {
		this.orders = orders;
	}

	public Long getBeansId() {
		return beansId;
	}

	public void setBeansId(Long beansId) {
		this.beansId = beansId;
	}
	
	public ContactDetails getContactDetails() {
		return contactDetails;
	}
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setContactDetails(ContactDetails contactDetails) {
		this.contactDetails = contactDetails;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public DeliveryType getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(DeliveryType deliveryType) {
		this.deliveryType = deliveryType;
	}
}
