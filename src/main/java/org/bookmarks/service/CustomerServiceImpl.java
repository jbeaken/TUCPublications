package org.bookmarks.service;


import java.math.BigDecimal;
import java.util.Collection;

import org.bookmarks.controller.CustomerSearchBean;
import org.bookmarks.website.domain.CreditCard;
import org.bookmarks.domain.CreditNote;
import org.bookmarks.domain.Customer;
import org.bookmarks.repository.CustomerRepository;
import org.bookmarks.repository.Repository;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl extends AbstractService<Customer> implements CustomerService {


	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CreditNoteService creditNoteService;


	@Override
	public Repository<Customer> getRepository() {
		return customerRepository;
	}

	@Override
	public Customer findMatchedCustomer(String match) {
		return 	customerRepository.findMatchedCustomer(match);
	}

	@Override
	public Customer findSecondaryMatchedCustomer(String match) {
		return 	customerRepository.findSecondaryMatchedCustomer(match);
	}

	@Override
	public void merge(Customer customerToKeep, Customer customerToDiscard) {
		customerRepository.merge(customerToKeep, customerToDiscard);
	}


	@Override
	//Doesn't create a credit note, used by invoice to debit account.
	public void debitAccount(Customer customer, BigDecimal amountChange) {
		customerRepository.debitAccount(customer, amountChange);
	}


	@Override
	public void debitAccount(Customer customer) {
		CreditNote creditNote = new CreditNote(customer);
		debitAccount(creditNote);
	}


	@Override
	public void debitAccount(CreditNote creditNote) {
		debitAccount(creditNote.getCustomer(), creditNote.getAmount());
	}

	@Override
	public void save(Customer customer) {
		if(customer.getBookmarksAccount().getAccountHolder() == true) {
			//Default to 0
			customer.getBookmarksAccount().setCurrentBalance(new BigDecimal(0));
		}
		super.save(customer);
	}
	@Override
	public void update(Customer customer) {
		if(customer.getBookmarksAccount().getAccountHolder() == true && customer.getBookmarksAccount().getCurrentBalance() == null) {
			//Default to 0
			customer.getBookmarksAccount().setCurrentBalance(new BigDecimal(0));
		}
		super.update(customer);
	}


	@Override
	public Collection<Customer> getForAutoComplete(String startOfSurname, Boolean accountHolders) {
		return customerRepository.getForAutoComplete(startOfSurname, accountHolders);
	}


	@Override
	public void updateCreditCard(CreditCard creditCard, Customer customer) {
		customerRepository.updateCreditCard(creditCard, customer);
	}


	@Override
	public void updateBookmarksAccountInfo(Customer customer) {
		customerRepository.updateBookmarksAccountInfo(customer);

	}


	@Override
	public void updateEmail(Customer customer) {
		customerRepository.updateEmail(customer);

	}
}
