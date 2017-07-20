package org.bookmarks.controller;

import org.bookmarks.domain.VTTransaction;

public class VTTransactionSearchBean extends AbstractSearchBean {

	public VTTransactionSearchBean() {
		super();
		creditNote = new VTTransaction();
//		setSortColumn("e.startDate");
//		setSortOrder("DESC");
		setPage("1");
	}
	
	public VTTransaction getVTTransaction() {
		return creditNote;
	}

	public void setVTTransaction(VTTransaction creditNote) {
		this.creditNote = creditNote;
	}

	private VTTransaction creditNote;

	@Override
	public void reset() {
		creditNote = new VTTransaction();
	}
	
}
