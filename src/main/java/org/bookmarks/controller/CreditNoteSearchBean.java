package org.bookmarks.controller;

import org.bookmarks.domain.CreditNote;
import org.bookmarks.domain.Customer;

public class CreditNoteSearchBean extends AbstractSearchBean {

	public CreditNoteSearchBean() {
		super();
		creditNote = new CreditNote();
		creditNote.setCustomer(new Customer());
//		setSortColumn("e.startDate");
//		setSortOrder("DESC");
		setPage("1");
	}
	
	public CreditNote getCreditNote() {
		return creditNote;
	}

	public void setCreditNote(CreditNote creditNote) {
		this.creditNote = creditNote;
	}

	private CreditNote creditNote;

	@Override
	public void reset() {
		creditNote = new CreditNote();
	}
	
}
