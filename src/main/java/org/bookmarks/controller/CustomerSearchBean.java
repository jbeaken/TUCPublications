package org.bookmarks.controller;

import org.bookmarks.domain.Customer;

public class CustomerSearchBean extends AbstractSearchBean {
	public CustomerSearchBean() {
		super();
		customer = new Customer();
		customer.setCustomerType(null);
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
