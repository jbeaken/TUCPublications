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

import java.util.Date;


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
	private SupplierReturnStatus status;

	@Transient
	private Long noOfLines;

	private Date dateSentToSupplier;

	public Long getNoOfLines() {
		return noOfLines;
	}

	public void setNoOfLines(Long noOfLines) {
		this.noOfLines = noOfLines;
	}

	public SupplierReturn() {
		super();
		setSupplierReturnLine(new HashSet<SupplierReturnLine>());
		setStatus(SupplierReturnStatus.ON_SHELVES);
	}

	public SupplierReturn(Supplier supplier) {
		this();
		setSupplier(supplier);
	}
	//select new SupplierReturn(sd.id, sd.status, sd.creationDate, sd.invoiceNumber, sd.supplier.name, sup.telephone1, sup.supplierAccount.accountNumber, count(sdl)
	public SupplierReturn(Long id, SupplierReturnStatus status, Date creationDate, Date dateSentToSupplier, String returnsNumber, String supplierName, String telephone1, String accountNumber, Long noOfLines) {
		this();
		setId(id);
		this.dateSentToSupplier = dateSentToSupplier;
		setStatus(status);
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

	public void setSupplierReturnLine(Collection<SupplierReturnLine> supplierReturnLine) {
		this.supplierReturnLine = supplierReturnLine;
	}

	public String getReturnsNumber() {
		return returnsNumber;
	}

	public void setReturnsNumber(String returnsNumber) {
		this.returnsNumber = returnsNumber;
	}

		public SupplierReturnStatus getStatus() {
			return status;
		}

		public void setStatus(SupplierReturnStatus status) {
			this.status = status;
		}

	public Date getDateSentToSupplier() {
		return dateSentToSupplier;
	}

	public void setDateSentToSupplier(Date dateSentToSupplier) {
		this.dateSentToSupplier = dateSentToSupplier;
	}
}
