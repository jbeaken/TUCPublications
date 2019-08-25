package org.bookmarks.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.bookmarks.website.domain.Address;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name="publisher")
public class Publisher extends AbstractNamedEntity {

	public Publisher() {
		super();
	}

	public Publisher(String name) {
		this();
		setName(name);
	}
	
	public Publisher(Long id, String name) {
		this(name);
		setId(id);
	}

	public Publisher(Long publisherId) {
		this();
		setId(publisherId);
	}

	public Publisher(Supplier supplier) {
		this();
		setSupplier(supplier);
	}

	@Embedded
	private Address address;

	@Length(max = 20)
	private String telephone1;

	@Length(max = 20)
	private String telephone2;

	private String contactName;

	private String email;
	
	@NotNull
	@ManyToOne
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	private Supplier supplier;

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
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

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
}
