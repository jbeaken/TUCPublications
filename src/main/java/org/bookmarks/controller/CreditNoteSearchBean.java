package org.bookmarks.controller;

import org.bookmarks.domain.CreditNote;

public class CreditNoteSearchBean extends AbstractSearchBean {

	public CreditNoteSearchBean() {
		super();
		creditNote = new CreditNote();
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
