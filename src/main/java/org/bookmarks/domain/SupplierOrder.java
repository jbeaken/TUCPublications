package org.bookmarks.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name="supplierorder")
public class SupplierOrder extends AbstractEntity {

	public SupplierOrder(Supplier supplier) {
		this();
		setSupplier(supplier);
	}

	public SupplierOrder() {
		super();
		setSupplierOrderStatus(SupplierOrderStatus.PENDING);
		setSupplierOrderLines(new ArrayList<SupplierOrderLine>());
//		setSupplier(new Supplier());
	}

	//from supplierOrderController.search
	//new SupplierOrder(so.id, so.supplierOrderStatus, so.sendDate, so.supplier.name, count(sol))	
		public SupplierOrder(Long id, SupplierOrderStatus status, Date sendDate, String supplierName, String telephone1, String accountNumber, Long noOfLines) {
			super();
			setId(id);
			setSupplierOrderStatus(status);
			setSendDate(sendDate);
			Supplier supplier = new Supplier(supplierName);
			supplier.setTelephone1(telephone1);
			SupplierAccount supplierAccount = new SupplierAccount();
			supplierAccount.setAccountNumber(accountNumber);
			supplier.setSupplierAccount(supplierAccount);
			setSupplier(supplier);
			setNoOfLines(noOfLines);
		}	
	
	public SupplierOrder(Long id) {
		this();
		setId(id);
	}


	@Transient
	private Long noOfLines;
	
	public Long getNoOfLines() {
		return noOfLines;
	}

	public void setNoOfLines(Long noOfLines) {
		this.noOfLines = noOfLines;
	}


	@OneToMany(fetch=FetchType.EAGER)
	@Cascade(value={CascadeType.ALL})
	private Collection<SupplierOrderLine> supplierOrderLines;

	private Date sendDate;

	@ManyToOne
	@NotNull
	private Supplier supplier;

	@NotNull
	@Enumerated(EnumType.STRING)
	private SupplierOrderStatus supplierOrderStatus;


	public Collection<SupplierOrderLine> getSupplierOrderLines() {
		return supplierOrderLines;
	}

	public void setSupplierOrderLines(Collection<SupplierOrderLine> supplierOrderLines) {
		this.supplierOrderLines = supplierOrderLines;
	}


	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public SupplierOrderStatus getSupplierOrderStatus() {
		return supplierOrderStatus;
	}

	public void setSupplierOrderStatus(SupplierOrderStatus supplierOrderStatus) {
		this.supplierOrderStatus = supplierOrderStatus;
	}

	public void addSupplierOrderLine(SupplierOrderLine supplierOrderLine) {
		getSupplierOrderLines().add(supplierOrderLine);
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public SupplierOrderLine getSupplierOrderLine(Long id) {
		for(SupplierOrderLine sol : getSupplierOrderLines()) {
			if(sol.getId().equals(id)) {
				return sol;
			}
		}
		return null;
	}
	
	public SupplierOrderLine getSupplierOrderLineByStockItemId(Long stockItemId) {
		for(SupplierOrderLine sol : getSupplierOrderLines()) {
			if(sol.getStockItem().getId().equals(stockItemId)) {
				return sol;
			}
		}
		return null;
	}

	public void removeSupplierOrderLine(Long id) {
		SupplierOrderLine solToRemove = null;
		for(SupplierOrderLine sol : getSupplierOrderLines()) {
			if(sol.getId().equals(id)) {
				solToRemove = sol;
			}
		}
		getSupplierOrderLines().remove(solToRemove);
	}

	public SupplierOrder getPending() {
		SupplierOrder newPending = new SupplierOrder();
		newPending.setSupplier(getSupplier());
		newPending.setSupplierOrderStatus(SupplierOrderStatus.PENDING);
		return newPending;
	}

	public void removeSupplierOrderLineByStockItemId(Long stockItemId) {
		SupplierOrderLine solToRemove = null;
		for(SupplierOrderLine sol : getSupplierOrderLines()) {
			if(sol.getStockItem().getId().equals(stockItemId)) {
				solToRemove = sol;
			}
		}
		if(solToRemove == null) return; //Nothing to remove
		getSupplierOrderLines().remove(solToRemove);
		
	}
}
