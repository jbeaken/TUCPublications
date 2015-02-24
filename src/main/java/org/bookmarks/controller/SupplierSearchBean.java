package org.bookmarks.controller;

import org.bookmarks.domain.Supplier;

public class SupplierSearchBean extends AbstractSearchBean {

	public SupplierSearchBean() {
		super();
		supplier = new Supplier();
		setPage("1");
	}
	
	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	private Supplier supplier;

	@Override
	public void reset() {
		supplier = new Supplier();
	}
	
}
