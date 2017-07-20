package org.bookmarks.controller;

import org.bookmarks.domain.VTTransaction;

public class VTTransactionSearchBean extends AbstractSearchBean {
	
	private VTTransaction transaction;

	public VTTransactionSearchBean() {
		super();
		transaction = new VTTransaction();
//		setSortColumn("e.startDate");
//		setSortOrder("DESC");
		setPage("1");
	}
	

	@Override
	public void reset() {
		transaction = new VTTransaction();
	}


	public VTTransaction getTransaction() {
		return transaction;
	}


	public void setTransaction(VTTransaction transaction) {
		this.transaction = transaction;
	}
	
}
