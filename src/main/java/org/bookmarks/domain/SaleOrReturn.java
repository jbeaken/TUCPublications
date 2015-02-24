package org.bookmarks.domain;

import java.util.Collection;
import java.util.Date;
import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Transient;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;





import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class SaleOrReturn extends AbstractEntity {
	
	public SaleOrReturn() {
		super();
	}

	public SaleOrReturn(Customer customer) {
		this();
		setCustomer(customer);
	}
	
//	new SaleOrReturn(s.id, s.creationDate, s.saleOrReturnStatus, s.returnDate, c, sum(sor.price), count(sor)
	public SaleOrReturn(Long id,
			Date creationDate,
			SaleOrReturnStatus saleOrReturnStatus,
			Date returnDate,
			String note,
			Customer customer,
			BigDecimal totalPrice,
			Long noOfLines) {
		this(customer);
		setId(id);
		setCreationDate(creationDate);
		setNote(note);
		setSaleOrReturnStatus(saleOrReturnStatus);
		setReturnDate(returnDate);
		setTotalPrice(totalPrice);
		setTotalAmount(noOfLines);
	}	
	

	@OneToMany(mappedBy = "saleOrReturn", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//	@OneToMany(fetch = FetchType.EAGER)
	private Collection<SaleOrReturnOrderLine> saleOrReturnOrderLines;

	@NotNull
	@DateTimeFormat(pattern="dd/MM/yy")
	private Date returnDate;

	@Enumerated(EnumType.STRING)
	@NotNull
	private SaleOrReturnStatus saleOrReturnStatus;

	@ManyToOne
	@NotNull
	private Customer customer;
	
	private String customerReference;

	@Transient
	//sum(sor.sellPrice * sor.amount)
	private BigDecimal totalPrice;

	@Transient
	//sum(sor.amount)
	private Long totalAmount;

	public String getCustomerReference() {
		return customerReference;
	}

	public void setCustomerReference(String customerReference) {
		this.customerReference = customerReference;
	}

	public Long getTotalAmount() {
		if(totalAmount == null) { //This is a transient entity, calculate
			long noOfLines = 0;
			if(getSaleOrReturnOrderLines() != null) { //No lines added yet
				for(SaleOrReturnOrderLine saleOrReturnOrderLine : getSaleOrReturnOrderLines()){
					noOfLines += saleOrReturnOrderLine.getAmount();
				}
			}
			return noOfLines;
		}
		//Persisted entity
		return totalAmount;
	}

	public void setTotalAmount(Long totalAmount) {
		this.totalAmount = totalAmount;
	}


	public BigDecimal getTotalPrice() {
		if(totalPrice == null) { //This is a transient entity, calculate
			float totalPrice = 0;
			if(getSaleOrReturnOrderLines() != null) { //No lines added yet
				for(SaleOrReturnOrderLine saleOrReturnOrderLine : getSaleOrReturnOrderLines()){
					totalPrice += saleOrReturnOrderLine.getPrice().floatValue();
				}
			}
			return new BigDecimal(totalPrice);
		}
		//Persisted entity
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Collection<SaleOrReturnOrderLine> getSaleOrReturnOrderLines() {
		return saleOrReturnOrderLines;
	}

	public void setSaleOrReturnOrderLines(Collection<SaleOrReturnOrderLine> saleOrReturnOrderLines) {
		this.saleOrReturnOrderLines = saleOrReturnOrderLines;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Date getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}

	public SaleOrReturnStatus getSaleOrReturnStatus() {
		return saleOrReturnStatus;
	}

	public void setSaleOrReturnStatus(SaleOrReturnStatus saleOrReturnStatus) {
		this.saleOrReturnStatus = saleOrReturnStatus;
	}
}
