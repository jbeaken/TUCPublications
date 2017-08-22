package org.bookmarks.service;

import java.math.BigDecimal;
import java.util.Collection;

import org.bookmarks.controller.CustomerSearchBean;
import org.bookmarks.domain.CreditNote;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrder;

public interface CreditNoteService extends Service<CreditNote>{

	BigDecimal getOutgoings();

	BigDecimal getIncomings();

	void creditAccount(CreditNote creditNote);

	void creditAccount(Customer customer, BigDecimal amount);

	void removeMatch(String transactionDescription);


}
