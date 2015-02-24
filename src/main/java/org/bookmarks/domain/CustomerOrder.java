package org.bookmarks.domain;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.bookmarks.website.domain.CreditCard;
import org.bookmarks.website.domain.DeliveryType;
import org.bookmarks.website.domain.PaymentType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

public class CustomerOrder extends AbstractEntity{
	
//	@Enumerated
	@NotNull
	private PaymentType paymentType;
	
//	@Enumerated
	@NotNull
	private DeliveryType deliveryType;	

//	@ManyToOne
	@NotNull
	private Customer customer;
	
	private CreditCard creditCard;
	
	@NotNull
	private Source source;
	
	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public CustomerOrder() {
		super();
		customerOrderline = new HashSet<CustomerOrderLine>();
	}
	
	public BigDecimal getTotalPrice() {
		BigDecimal total = new BigDecimal(0);
		for(CustomerOrderLine col : customerOrderline) {
			total = total.add(col.getTotalPrice());
		}
		return total;
	}
	
	public Collection<CustomerOrderLine> getCustomerOrderline() {
		return customerOrderline;
	}

	public void setCustomerOrderline(Collection<CustomerOrderLine> customerOrderline) {
		this.customerOrderline = customerOrderline;
	}
	
	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	@OneToMany
	@Cascade(value={CascadeType.ALL})
//	@Fetch(FetchMode.JOIN)
	private Collection<CustomerOrderLine> customerOrderline;
	

	
	//ACCESSORS
	
	public DeliveryType getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(DeliveryType deliveryType) {
		this.deliveryType = deliveryType;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public void addStockItem(StockItem stockItem) {
		customerOrderline.add(new CustomerOrderLine(stockItem));
		
	}
}
