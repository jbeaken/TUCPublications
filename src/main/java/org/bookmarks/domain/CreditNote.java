package org.bookmarks.domain;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
@Entity
public class CreditNote extends AbstractEntity {
	
	@NotNull
	private BigDecimal amount;
	
	@ManyToOne
	@NotNull	
	private Customer customer;

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public CreditNote() {
		super();
	}
	public CreditNote(Customer customer) {
		this();
		setCustomer(customer);
		setAmount(customer.getCreditAmount());
	}

	public CreditNote(Long customerId) {
		this();
		Customer customer = new Customer(customerId);
		setCustomer(customer);
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
