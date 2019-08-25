package org.bookmarks.repository;

import java.math.BigDecimal;
import java.util.Collection;

import org.bookmarks.controller.CustomerSearchBean;
import org.bookmarks.controller.SearchBean;
import org.bookmarks.website.domain.CreditCard;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrder;

public interface CustomerRepository extends Repository<Customer>{

	void debitAccount(Customer customer, BigDecimal amountChange);

	Collection<Customer> getForAutoComplete(String startOfSurname, Boolean accountHolders);

	void updateCreditCard(CreditCard creditCard, Customer customer);

	void updateBookmarksAccountInfo(Customer customer);

	Customer getByEmail(String email);

	void updateEmail(Customer customer);

	void merge(Customer customerToKeep, Customer customerToDiscard);

	Customer findMatchedCustomer(String match);

	Customer findSecondaryMatchedCustomer(String match);
}
