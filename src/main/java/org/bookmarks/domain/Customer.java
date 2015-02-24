package org.bookmarks.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.bookmarks.website.domain.Address;
import org.bookmarks.website.domain.ContactDetails;
import org.bookmarks.website.domain.CreditCard;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="customer")
public class Customer extends AbstractEntity {
	
	@DateTimeFormat(style="S-")
	@Column(name="joinedDate")
	private Date joinedDate = new Date();

	public ContactDetails getContactDetails() {
		return contactDetails;
	}
	public void setContactDetails(ContactDetails contactDetails) {
		this.contactDetails = contactDetails;
	}

	@DateTimeFormat(style="S-")
	private Date lastRegisterdDate= new Date();
	
	@DateTimeFormat(style="S-")
	@Column(name="leftDate")
	private Date leftDate= new Date();
	
	@Embedded
	@Valid
	private Address address;
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "address1", column = @Column(name = "webaddress1")),
		@AttributeOverride(name = "address2", column = @Column(name = "webaddress2")),
		@AttributeOverride(name = "address3", column = @Column(name = "webaddress3")),
		@AttributeOverride(name = "city", column = @Column(name = "webcity")),
		@AttributeOverride(name = "postcode", column = @Column(name = "webpostcode")),
		@AttributeOverride(name = "country", column = @Column(name = "webcountry"))
			
	})
	private Address webAddress;
	
	@Embedded
	private BookmarksAccount bookmarksAccount;

	@NotNull
	@Size(min=1, max = 55)
	@Column(name="firstName")
	private String firstName;
	
	@NotNull
	@Size(min=1, max = 55)
	@Column(name="lastName")
	private String lastName;
	
	@Embedded
	private CreditCard creditCard;
	
	public Customer(Long customerId) {
		setId(customerId);
	}

	@NotNull
	@Embedded	
	private ContactDetails contactDetails;
	
	@Transient
	private BigDecimal creditAmount;
	
	public BigDecimal getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(BigDecimal creditAmount) {
		this.creditAmount = creditAmount;
	}

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name="customerType")
	private CustomerType customerType = CustomerType.CUSTOMER;
	
	private BigDecimal bookmarksDiscount;

	private BigDecimal nonBookmarksDiscount;
	
	public Customer() {
		super();
		address = new Address();
		contactDetails = new ContactDetails();
		bookmarksAccount = new BookmarksAccount();
	}

	public Customer(String firstName, String lastName, String mobile, String work, String home) {
			this();
			setFirstName(firstName);
			setLastName(lastName);
			getContactDetails().setHomeNumber(home);
			getContactDetails().setMobileNumber(mobile);
			getContactDetails().setWorkNumber(work);
	}
	
	//From AutoComplete
	//new Customer(c.id, c.firstName, c.surname, c.address.postcode)
	public Customer(Long id, String firstName, String lastName, String postcode) {
			this();
			setId(id);
			setFirstName(firstName);
			setLastName(lastName);
			address.setPostcode(postcode);
			setAddress(address);
	}
	//From getMinimal
	//new Customer(c.id, c.firstName, c.surname, c.address.postcode)
	public Customer(Long id, String firstName, String lastName, Boolean accountHolder) {
		this();
		setId(id);
		setFirstName(firstName);
		setLastName(lastName);
		getBookmarksAccount().setAccountHolder(accountHolder);
	}
	
	public CustomerType getCustomerType() {
		return customerType;
	}
	
	public void setCustomerType(CustomerType customerType) {
		this.customerType = customerType;
	}
	
	public BigDecimal getBookmarksDiscount() {
		return bookmarksDiscount;
	}

	public void setBookmarksDiscount(BigDecimal bookmarksDiscount) {
		this.bookmarksDiscount = bookmarksDiscount;
	}

	public BigDecimal getNonBookmarksDiscount() {
		return nonBookmarksDiscount;
	}

	public void setNonBookmarksDiscount(BigDecimal nonBookmarksDiscount) {
		this.nonBookmarksDiscount = nonBookmarksDiscount;
	}

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	public Address getWebAddress() {
		return webAddress;
	}

	public void setWebAddress(Address webAddress) {
		this.webAddress = webAddress;
	}

	public String getFullName() {
		return getFirstName() + " " + getLastName();
	}
	
	public String getFullAddress() {
		StringBuffer buffer = new StringBuffer();
		Address address = getAddress();
		if(address.getAddress1() != null) {
			buffer.append(address.getAddress1());
		}
		if(address.getAddress2() != null) {
			buffer.append(", " + address.getAddress2());
		}
		if(address.getAddress3() != null) {
			buffer.append(", " + address.getAddress3());
		}
		if(address.getCity() != null) {
			buffer.append(", " + address.getCity());
		}
		if(address.getPostcode() != null) {
			buffer.append(", " + address.getPostcode());
		}
		return buffer.toString();
	}
	
	public String getFullAddressWithBreaks() {
		StringBuffer buffer = new StringBuffer();
		Address address = getAddress();
		if(address.getAddress1() != null && !address.getAddress1().isEmpty()) {
			buffer.append(address.getAddress1());
		}
		if(address.getAddress2() != null && !address.getAddress2().isEmpty()) {
			buffer.append("<br/>" + address.getAddress2());
		}
		if(address.getAddress3() != null && !address.getAddress3().isEmpty()) {
			buffer.append("<br/>" + address.getAddress3());
		}
		if(address.getCity() != null && !address.getCity().isEmpty()) {
			buffer.append("<br/>" + address.getCity());
		}
		if(address.getPostcode() != null && !address.getPostcode().isEmpty()) {
			buffer.append("<br/>" + address.getPostcode());
		}
		if(buffer.length() == 0) return "No address supplied";
		return buffer.toString();
	}
	
	public String getFullPhoneNumber() {
		StringBuffer buffer = new StringBuffer();
		ContactDetails contactDetails = getContactDetails();
		if(contactDetails.getHomeNumber() != null) {
			buffer.append("H: " + contactDetails.getHomeNumber());
		}
		if(contactDetails.getMobileNumber() != null) {
			buffer.append(", M: " + contactDetails.getMobileNumber());
		}
		if(contactDetails.getWorkNumber() != null) {
			buffer.append(", W: " + contactDetails.getWorkNumber());
		}
		return buffer.toString();
	}
	
	public String getFullPhoneNumberWithBreaks() {
		StringBuffer buffer = new StringBuffer();
		ContactDetails contactDetails = getContactDetails();
		if(contactDetails == null) return "No phone number";
		if(contactDetails.getHomeNumber() != null) {
			buffer.append("Home: " + contactDetails.getHomeNumber());
		}
		if(contactDetails.getMobileNumber() != null) {
			buffer.append("<br/>Mobile: " + contactDetails.getMobileNumber());
		}
		if(contactDetails.getWorkNumber() != null) {
			buffer.append("<br/>Work: " + contactDetails.getWorkNumber());
		}
		return buffer.toString();
	}	
	
	@OneToMany
	private Set<CustomerOrderLine> CustomerOrderLines;
	
	public BookmarksAccount getBookmarksAccount() {
		return bookmarksAccount;
	}

	public void setBookmarksAccount(BookmarksAccount bookmarksAccount) {
		this.bookmarksAccount = bookmarksAccount;
	}	
	 
	public Set<CustomerOrderLine> getCustomerOrderLines() {
		return CustomerOrderLines;
	}
	public void setCustomerOrderLines(Set<CustomerOrderLine> CustomerOrderLines) {
		this.CustomerOrderLines = CustomerOrderLines;
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
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}	
	public Date getJoinedDate() {
		return joinedDate;
	}
	public void setJoinedDate(Date joinedDate) {
		this.joinedDate = joinedDate;
	}
	public Date getLastRegisterdDate() {
		return lastRegisterdDate;
	}
	public void setLastRegisterdDate(Date lastRegisterdDate) {
		this.lastRegisterdDate = lastRegisterdDate;
	}
	public Date getLeftDate() {
		return leftDate;
	}
	public void setLeftDate(Date leftDate) {
		this.leftDate = leftDate;
	}
}
