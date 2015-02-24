package org.bookmarks.domain;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.bookmarks.website.domain.DeliveryType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
/**
 *
 * @author Administrator
 *
 */
@Entity
@Table(name="invoice")
public class Invoice extends AbstractEntity {

	@OneToMany(fetch = FetchType.EAGER)
	@Cascade(value={CascadeType.ALL})
	@JoinTable(name="invoice_sale")
	private Set<Sale> sales = new HashSet<Sale>();

	@ManyToOne
	@NotNull
	private Customer customer;
	
	@OneToMany(fetch = FetchType.EAGER)
	private Set<CustomerOrderLine> customerOrderLines;

	@NotNull
	@Column(name="isProforma")
	private Boolean isProforma = Boolean.FALSE;

	@Min(value=0)
	@NotNull
	@Column(name="secondHandPrice")
	private BigDecimal secondHandPrice = new BigDecimal(0);

	@NotNull
	@Column(name="serviceCharge")
	private BigDecimal serviceCharge = new BigDecimal(0);

	@Transient
	private BigDecimal stockItemCharges = new BigDecimal(0);	

	@Enumerated
	@NotNull
	private DeliveryType deliveryType = DeliveryType.COLLECTION;
	
	@NotNull
	@Min(value=0)
	private BigDecimal totalPrice = new BigDecimal(0);

	@NotNull
	@Min(value=0)
	private BigDecimal vatPayable = new BigDecimal(0);	
	
	@NotNull
	private Boolean paid = Boolean.FALSE;
	
	//Constructors
	public Invoice() {
		super();
	}

	public Invoice(Customer customer) {
		this();
		this.customer = customer;
	}

	//From repository search
	public Invoice(Long id, Date creationDate, Boolean isProforma, Boolean paid, DeliveryType deliveryType, String note, BigDecimal totalPrice, BigDecimal  secondHandPrice, BigDecimal serviceCharge, Long customerId, String firstName, String lastName, String mobile, String work, String home) {
		this();
		Customer customer = new Customer(firstName, lastName, mobile, work, home);
		customer.setId(customerId);
		setCustomer(customer);
		setIsProforma(isProforma);
		setPaid(paid);
		setDeliveryType(deliveryType);
		setId(id);
		setCreationDate(creationDate);
		setNote(note);
		setSecondHandPrice(secondHandPrice);
		setServiceCharge(serviceCharge);
		setTotalPrice(totalPrice);
	}

	public Boolean getPaid() {
		return paid;
	}

	public void setPaid(Boolean paid) {
		this.paid = paid;
	}
	
	@Transient
	private Boolean updateStock = Boolean.TRUE;

	public Boolean getUpdateStock() {
		return updateStock;
	}

	public void setUpdateStock(Boolean updateStock) {
		this.updateStock = updateStock;
	}	

	public DeliveryType getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(DeliveryType deliveryType) {
		this.deliveryType = deliveryType;
	}


	public BigDecimal getVatPayable() {
		return vatPayable;
	}

	public void setVatPayable(BigDecimal vatPayable) {
		this.vatPayable = vatPayable;
	}

	public BigDecimal getStockItemCharges() {
		return stockItemCharges;
	}

	public void setStockItemCharges(BigDecimal stockItemCharges) {
		this.stockItemCharges = stockItemCharges;
	}

	public Set<Sale> getSales() {
		return sales;
	}

	public void setSales(Set<Sale> sales) {
		this.sales = sales;
	}


	public BigDecimal getServiceCharge() {
		return serviceCharge;
	}

	public void setServiceCharge(BigDecimal serviceCharge) {
		this.serviceCharge = serviceCharge;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public BigDecimal getSecondHandPrice() {
		return secondHandPrice;
	}

	public void setSecondHandPrice(BigDecimal secondHandPrice) {
		this.secondHandPrice = secondHandPrice;
	}

//	public Set<InvoiceOrderLine> getInvoiceOrderLines() {
//		return invoiceOrderLines;
//	}
//
//	public void setInvoiceOrderLines(Set<InvoiceOrderLine> invoiceOrderLines) {
//		this.invoiceOrderLines = invoiceOrderLines;
//	}

	public Set<CustomerOrderLine> getCustomerOrderLines() {
		return customerOrderLines;
	}

	public void setCustomerOrderLines(Set<CustomerOrderLine> customerOrderLines) {
		this.customerOrderLines = customerOrderLines;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Boolean getIsProforma() {
		return isProforma;
	}

	public void setIsProforma(Boolean isProforma) {
		this.isProforma = isProforma;
	}

	public void addCustomerOrderLine(CustomerOrderLine customerOrderLine) {
		if(customerOrderLines == null) {
			customerOrderLines = new HashSet<CustomerOrderLine>();
		}
		customerOrderLines.add(customerOrderLine);
	}

	public void setInvoiceOrderLines(Collection<InvoiceOrderLine> invoiceOrderLines) {
		Set<InvoiceOrderLine> set = new HashSet<InvoiceOrderLine>(invoiceOrderLines);
		setInvoiceOrderLines(set);
	}

	public void setCustomerOrderLines(
			Collection<CustomerOrderLine> customerOrderLines) {
		Set<CustomerOrderLine> set = new HashSet<CustomerOrderLine>(customerOrderLines);
		setCustomerOrderLines(set);
	}

	public void calculate(boolean calculateDiscount) {
		calculate(getSales(), calculateDiscount);
	}

	/**
	 * When creating new invoice, sale.isDiscountOverriden is in use, when it's an edit of an existing one
	 * can this isn't set, can manually override
	 * @param sales
	 * @param calculateDiscount
	 */
	public void calculate(Collection<Sale> sales, boolean calculateDiscount) {
		BigDecimal totalPrice = new BigDecimal(0);
		BigDecimal vatPayable = new BigDecimal(0);
		for(Sale sale : sales){
			sale.calculate(this, calculateDiscount);
//			price = price.setScale(2, BigDecimal.ROUND_HALF_DOWN);
			totalPrice = totalPrice.add(sale.getDiscountedPrice().multiply(new BigDecimal(sale.getQuantity())));
			vatPayable = vatPayable.add(sale.getVatAmount().multiply(new BigDecimal(sale.getQuantity())));
		}
//		setStockItemCharges(new BigDecimal(totalPrice.floatValue()));
		setStockItemCharges(totalPrice);
		//Add any 2nd hand
		if(getSecondHandPrice() != null) {
			totalPrice = totalPrice.add(getSecondHandPrice());
		}
		//Add service charge 2nd hand
		if(getServiceCharge() != null) {
			totalPrice = totalPrice.add(getServiceCharge());
		}
		setTotalPrice(totalPrice);
		setVatPayable(vatPayable);
	}
}
