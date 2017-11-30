package org.bookmarks.controller;

import org.bookmarks.domain.Staff;

public class StaffSearchBean extends AbstractSearchBean {

	public StaffSearchBean() {
		super();
		staff = new Staff();
		staff.setName("");
		setPage("1");
	}

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	private Staff staff;

	@Override
	public void reset() {
		staff = new Staff();
	}

}
