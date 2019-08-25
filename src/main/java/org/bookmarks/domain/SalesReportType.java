package org.bookmarks.domain;

public enum SalesReportType {
//	DAILY_REPORT("Daily Report"), 
	SALE_LIST("Sale List"), 
	CATEGORY_STOCK_TAKE("Category Stock Take"), 
	PUBLISHER_STOCK_TAKE("Publisher Stock Take"),
	TIME_OF_DAY("Time of Day"), 
	BY_SOURCE("By Source"),
	INVOICE("Invoice List"),
	BY_CATEGORY("By Category"),
	VAT("VAT"), 
	UNSOLD("Unsold");
	
	private final String displayName;

    private SalesReportType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
