package org.bookmarks.domain;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name="supplierdelivery")
public class SupplierDelivery extends AbstractEntity {
	
	@OneToMany(fetch=FetchType.EAGER)
	@Cascade(value={CascadeType.ALL})
	private Collection<SupplierDeliveryLine> supplierDeliveryLine;
	
	@ManyToOne
	@NotNull
	private Supplier supplier;
	
	@NotEmpty
	private String invoiceNumber;
	
	@Transient
	private Long noOfLines;
	
	public Long getNoOfLines() {
		return noOfLines;
	}

	public void setNoOfLines(Long noOfLines) {
		this.noOfLines = noOfLines;
	}

	public SupplierDelivery() {
		super();
		setSupplierDeliveryLine(new HashSet<SupplierDeliveryLine>());
	}

	public SupplierDelivery(Supplier supplier) {
		this();
		setSupplier(supplier);
	}
	//select new SupplierDelivery(sd.id, sd.creationDate, sd.invoiceNumber, sd.supplier.name, sup.telephone1, sup.supplierAccount.accountNumber, count(sdl)
	public SupplierDelivery(Long id, Date creationDate, String invoiceNumber, String supplierName, String telephone1, String accountNumber, Long noOfLines) {
		this();
		setId(id);
		setInvoiceNumber(invoiceNumber);
		setCreationDate(creationDate);
		Supplier supplier = new Supplier(supplierName);
		SupplierAccount sa = new SupplierAccount();
		sa.setAccountNumber(accountNumber);
		supplier.setSupplierAccount(sa);
		supplier.setTelephone1(telephone1);
		setSupplier(supplier);
		setNoOfLines(noOfLines);
	}
	
	public SupplierDelivery(Long id, Date creationDate, String supplierName, String telephone1, String accountNumber, Long noOfLines) {
		this();
		setId(id);
		setCreationDate(creationDate);
		Supplier supplier = new Supplier(supplierName);
		SupplierAccount sa = new SupplierAccount();
		sa.setAccountNumber(accountNumber);
		supplier.setSupplierAccount(sa);
		supplier.setTelephone1(telephone1);
		setSupplier(supplier);
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public Collection<SupplierDeliveryLine> getSupplierDeliveryLine() {
		return supplierDeliveryLine;
	}

	public void setSupplierDeliveryLine(
			Collection<SupplierDeliveryLine> supplierDeliveryLine) {
		this.supplierDeliveryLine = supplierDeliveryLine;
	}
}
