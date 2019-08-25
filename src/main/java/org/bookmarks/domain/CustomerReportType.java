package org.bookmarks.domain;

public enum CustomerReportType {
	INVOICE("Invoice");
	
	private final String displayName;

    private CustomerReportType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
