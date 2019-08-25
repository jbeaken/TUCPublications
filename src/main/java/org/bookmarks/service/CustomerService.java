package org.bookmarks.service;

import java.math.BigDecimal;
import java.util.Collection;

import org.bookmarks.controller.CustomerSearchBean;
import org.bookmarks.website.domain.CreditCard;
import org.bookmarks.domain.CreditNote;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrder;

public interface CustomerService extends Service<Customer>{

	void debitAccount(Customer customer, BigDecimal totalPrice);

	void debitAccount(Customer customer);

	void debitAccount(CreditNote creditNote);

	Collection<Customer> getForAutoComplete(String term, Boolean accountHolders);

	void updateCreditCard(CreditCard creditCard, Customer customer);

	void updateBookmarksAccountInfo(Customer customer);

	void updateEmail(Customer customer);

	void merge(Customer customerToKeep, Customer customeroDiscard);

	Customer findMatchedCustomer(String match);

	Customer findSecondaryMatchedCustomer(String match);

}
