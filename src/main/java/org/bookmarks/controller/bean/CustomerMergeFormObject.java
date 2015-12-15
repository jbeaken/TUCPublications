package org.bookmarks.controller.bean;

import org.bookmarks.domain.Customer;

public class CustomerMergeFormObject {

	public CustomerMergeFormObject() {
		super();
		customerToKeep = new Customer();
		customerToDiscard = new Customer();
	}

	private Customer customerToKeep;

	private Customer customerToDiscard;

	public Customer getCustomerToKeep() {
		return customerToKeep;
	}

	public void setCustomerToKeep(Customer customerToKeep) {
		this.customerToKeep = customerToKeep;
	}

	public Customer getCustomerToDiscard() {
		return customerToDiscard;
	}

	public void setCustomerToDiscard(Customer customerToDiscard) {
		this.customerToDiscard = customerToDiscard;
	}
}
