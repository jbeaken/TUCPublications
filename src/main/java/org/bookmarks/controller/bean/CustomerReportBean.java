package org.bookmarks.controller.bean;

import java.util.Date;

import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerReportType;
import org.bookmarks.domain.SalesReportType;

public class CustomerReportBean extends ReportBean{
	
	private Customer customer;

	private String filter;
	
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}
	private CustomerReportType customerReportType;

	public CustomerReportType getCustomerReportType() {
		return customerReportType;
	}

	public void setCustomerReportType(CustomerReportType customerReportType) {
		this.customerReportType = customerReportType;
	}
}
