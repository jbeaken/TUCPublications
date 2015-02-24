package org.bookmarks.domain;

import java.math.BigDecimal;
import java.util.Collection;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.bookmarks.website.domain.Address;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name="supplier")
public class Supplier extends AbstractNamedEntity {


	public Supplier() {
		super();
	}

	public Supplier(Long id) {
		this();
		setId(id);
	}
	
	public Supplier(Long id, String name) {
		this(id);
		setName(name);
	}

	public Supplier(String name) {
		this();
		setName(name);
	}
	
	public Supplier(Publisher publisher) {
		this();
		setName(publisher.getName());
	}

	@NotNull
	private Long defaultDiscount = 40l;

	@Embedded
	private Address address;
	
	@Length(max = 20)
	private String telephone1;

	@Length(max = 20)
	private String telephone2;

	private String contactName;

	private String email;

    @Embedded
 	private SupplierAccount supplierAccount;

	public Long getDefaultDiscount() {
		return defaultDiscount;
	}

	public void setDefaultDiscount(Long defaultDiscount) {
		this.defaultDiscount = defaultDiscount;
	}

 	public SupplierAccount getSupplierAccount() {
		return supplierAccount;
	}

	public void setSupplierAccount(SupplierAccount supplierAccount) {
		this.supplierAccount = supplierAccount;
	}

	public String getTelephone1() {
		return telephone1;
	}

	public void setTelephone1(String telephone1) {
		this.telephone1 = telephone1;
	}

	public String getTelephone2() {
		return telephone2;
	}

	public void setTelephone2(String telephone2) {
		this.telephone2 = telephone2;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@OneToMany
	private Collection<SupplierDelivery> supplierInvoices;

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public Collection<SupplierDelivery> getSupplierInvoices() {
		return supplierInvoices;
	}

	public void setSupplierInvoices(Collection<SupplierDelivery> supplierInvoices) {
		this.supplierInvoices = supplierInvoices;
	}
}
