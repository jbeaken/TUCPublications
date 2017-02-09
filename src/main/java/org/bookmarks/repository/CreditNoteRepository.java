package org.bookmarks.repository;

import java.math.BigDecimal;
import java.util.Collection;

import org.bookmarks.controller.CustomerSearchBean;
import org.bookmarks.controller.SearchBean;
import org.bookmarks.controller.bean.CustomerReportBean;
import org.bookmarks.domain.CreditNote;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrder;

public interface CreditNoteRepository extends Repository<CreditNote>{


	Collection<CreditNote> getCreditNotes(CustomerReportBean customerReportBean);

	BigDecimal getOutgoings();

	BigDecimal getIncomings();

	
}
