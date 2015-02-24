package org.bookmarks.domain;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name="supplierreturn")
public class SupplierReturn extends AbstractEntity {
	
	@OneToMany(fetch=FetchType.EAGER)
	@Cascade(value={CascadeType.ALL})
	@JoinColumn(name="supplier_return_id")
	private Collection<SupplierReturnLine> supplierReturnLine;
	
	@ManyToOne
	@NotNull
	private Supplier supplier;
	
	@NotEmpty
	@Column(name="number")
	private String returnsNumber;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private SupplieReturnStatus status;
	
	@Transient
	private Long noOfLines;
	
	public Long getNoOfLines() {
		return noOfLines;
	}

	public void setNoOfLines(Long noOfLines) {
		this.noOfLines = noOfLines;
	}

	public SupplierReturn() {
		super();
		setSupplierReturnLine(new HashSet<SupplierReturnLine>());
		setStatus(SupplieReturnStatus.PENDING);
	}

	public SupplierReturn(Supplier supplier) {
		this();
		setSupplier(supplier);
	}
	//select new SupplierReturn(sd.id, sd.creationDate, sd.invoiceNumber, sd.supplier.name, sup.telephone1, sup.supplierAccount.accountNumber, count(sdl)
	public SupplierReturn(Long id, Date creationDate, String returnsNumber, String supplierName, String telephone1, String accountNumber, Long noOfLines) {
		this();
		setId(id);
		setReturnsNumber(returnsNumber);
		setCreationDate(creationDate);
		Supplier supplier = new Supplier(supplierName);
		SupplierAccount sa = new SupplierAccount();
		sa.setAccountNumber(accountNumber);
		supplier.setSupplierAccount(sa);
		supplier.setTelephone1(telephone1);
		setSupplier(supplier);
		setNoOfLines(noOfLines);
	}
	
	public SupplierReturn(Long id, Date creationDate, String supplierName, String telephone1, String accountNumber, Long noOfLines) {
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


	public Collection<SupplierReturnLine> getSupplierReturnLine() {
		return supplierReturnLine;
	}

	public void setSupplierReturnLine(
			Collection<SupplierReturnLine> supplierReturnLine) {
		this.supplierReturnLine = supplierReturnLine;
	}

	public String getReturnsNumber() {
		return returnsNumber;
	}

	public void setReturnsNumber(String returnsNumber) {
		this.returnsNumber = returnsNumber;
	}

	public SupplieReturnStatus getStatus() {
		return status;
	}

	public void setStatus(SupplieReturnStatus status) {
		this.status = status;
	}
}
