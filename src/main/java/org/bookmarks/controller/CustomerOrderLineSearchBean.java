package org.bookmarks.controller;

import org.bookmarks.domain.BookmarksRole;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.website.domain.PaymentType;
import java.util.Date;

public class CustomerOrderLineSearchBean extends AbstractSearchBean {
	private CustomerOrderLine customerOrderLine;

	private BookmarksRole bookmarksRole;

	private String researchText;

	// @DateTimeFormat(pattern="dd/MM/yy")
	private Date startDate;

	// @DateTimeFormat(pattern="dd/MM/yy")
	private Date endDate;

	public String getResearchText() {
		return researchText;
	}

	public void setResearchText(String researchText) {
		this.researchText = researchText;
	}

	public BookmarksRole getBookmarksRole() {
		return bookmarksRole;
	}

	public void setBookmarksRole(BookmarksRole bookmarksRole) {
		this.bookmarksRole = bookmarksRole;
	}

	public CustomerOrderLineSearchBean() {
		super();
		customerOrderLine = new CustomerOrderLine();
		setSortColumn("col.creationDate");
		setSortOrder("DESC");
	}

	public CustomerOrderLineSearchBean(Long customerID) {
		this();
		Customer customer = new Customer();
		customer.setId(customerID);
		getCustomerOrderLine().setCustomer(customer);
	}

	public CustomerOrderLineSearchBean(Customer customer) {
		this();
		getCustomerOrderLine().setCustomer(customer);
	}

	public CustomerOrderLine getCustomerOrderLine() {
		return customerOrderLine;
	}

	public void setCustomerOrderLine(CustomerOrderLine customerOrderLine) {
		this.customerOrderLine = customerOrderLine;
	}


	public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}

		public Date getEndDate() {
			return endDate;
		}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	@Override
	public void reset() {
		customerOrderLine = new CustomerOrderLine();
	}
}
