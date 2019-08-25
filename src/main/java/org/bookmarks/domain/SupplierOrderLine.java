package org.bookmarks.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Table;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;

/**
 * @author Administrator
 *
 */
@Entity
@Table(name="supplierorderline")
public class SupplierOrderLine extends OrderLine {


	public SupplierOrderLine() {
		super();
		setAmount(0l);
		setPriority(Level.LOW);
		setSupplier(new Supplier());
	}	

	//select new SupplierOrderLine(sol.id, sol.amount, s.id, s.title, s.isbn, sol.priority, sol.supplierOrderLineStatus, sol.sendDate, sol.supplier.name) " +
	public SupplierOrderLine(Long id, Long amount, Long stockId, String title, String isbn, Level priority, SupplierOrderLineStatus status, Date sendDate, String supplierName, Long supplierId, SupplierOrderLineType type, String note) {
		super();
		setAmount(amount);
		StockItem stockItem = new StockItem(title, isbn);
		stockItem.setId(stockId);
		setStockItem(stockItem);
		setSupplier(new Supplier(supplierId, supplierName));
		setPriority(priority);
		setId(id);
		setSupplierOrderLineStatus(status);
		setSendDate(sendDate);
		setType(type);
		setNote(note);
	}	

	//CustomerOrderService.save()
	public SupplierOrderLine(CustomerOrderLine customerOrderLine) {
		setType(SupplierOrderLineType.CUSTOMER_ORDER);
		setPriority(Level.HIGH);
		setSupplierOrderLineStatus(SupplierOrderLineStatus.READY_TO_SEND);
		setStockItem(customerOrderLine.getStockItem());
		setCustomerOrderLine(customerOrderLine);
		//Invert
		customerOrderLine.setSupplierOrderLine(this);
	}

	@NotNull
	@Enumerated(EnumType.STRING)
	private SupplierOrderLineStatus supplierOrderLineStatus = SupplierOrderLineStatus.READY_TO_SEND;
	
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private SupplierOrderLineType type;

	private Date sendDate;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Level priority;

	@OneToOne(mappedBy="supplierOrderLine")
	private CustomerOrderLine customerOrderLine;
	
	@Transient
	private Boolean changePreferredSupplier = Boolean.FALSE;
	
	@Transient
	private Boolean sendDirectToSupplier = Boolean.FALSE;
	
	@Transient
	private String userInitials;
	
	@ManyToOne
	@NotNull
	private Supplier supplier;
	
	@Transient
	@Min(value=0)
	private Long marxismAmount;
	
	
	public String getUserInitials() {
		return userInitials;
	}

	public void setUserInitials(String userInitials) {
		this.userInitials = userInitials;
	}

	public SupplierOrderLineType getType() {
		return type;
	}

	public void setType(SupplierOrderLineType type) {
		this.type = type;
	}

	public Level getPriority() {
		return priority;
	}

	public void setPriority(Level priority) {
		this.priority = priority;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}	
	
	public Long getMarxismAmount() {
		return marxismAmount;
	}

	public void setMarxismAmount(Long marxismAmount) {
		this.marxismAmount = marxismAmount;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Boolean getSendDirectToSupplier() {
		return sendDirectToSupplier;
	}

	public void setSendDirectToSupplier(Boolean sendDirectToSupplier) {
		this.sendDirectToSupplier = sendDirectToSupplier;
	}

	public Boolean getChangePreferredSupplier() {
		return changePreferredSupplier;
	}

	public void setChangePreferredSupplier(Boolean changePreferredSupplier) {
		this.changePreferredSupplier = changePreferredSupplier;
	}
	
	public CustomerOrderLine getCustomerOrderLine() {
		return customerOrderLine;
	}

	public void setCustomerOrderLine(CustomerOrderLine customerOrderLine) {
		this.customerOrderLine = customerOrderLine;
	}

	public SupplierOrderLineStatus getSupplierOrderLineStatus() {
		return supplierOrderLineStatus;
	}

	public void setSupplierOrderLineStatus(SupplierOrderLineStatus supplierOrderLineStatus) {
		this.supplierOrderLineStatus = supplierOrderLineStatus;
	}

	public SupplierOrderLine copy() {
		SupplierOrderLine sol = new SupplierOrderLine();
		sol.setStockItem(getStockItem());
		sol.setSendDirectToSupplier(false);
		sol.setChangePreferredSupplier(false);		
		return sol;
	}

}
