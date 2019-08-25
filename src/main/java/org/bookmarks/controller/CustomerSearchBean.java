package org.bookmarks.controller;

import org.bookmarks.domain.Customer;

public class CustomerSearchBean extends AbstractSearchBean {

	private Long customerId;

	public CustomerSearchBean() {
		super();
		customer = new Customer();
		customer.setCustomerType(null);
	}
	public Long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	private Customer customer;

	@Override
	public void reset() {
		customer = new Customer();
	}

}
