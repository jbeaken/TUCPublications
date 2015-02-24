package org.bookmarks.controller;

import org.bookmarks.domain.SalesReportType;
import org.bookmarks.domain.Supplier;

public class ReportSearchBean extends AbstractSearchBean {

	public ReportSearchBean() {
		super();
	}
	
	private SalesReportType reportType;
	
	public SalesReportType getReportType() {
		return reportType;
	}

	public void setReportType(SalesReportType reportType) {
		this.reportType = reportType;
	}

	@Override
	public void reset() {
	}
	
}
