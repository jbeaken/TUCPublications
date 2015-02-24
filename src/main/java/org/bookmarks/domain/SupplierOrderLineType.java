package org.bookmarks.domain;

public enum SupplierOrderLineType {
	CUSTOMER_ORDER("Customer Order"),
	KEEP_IN_STOCK("Keep In Stock"),
	USER("User");
	
	private final String displayName;

    private SupplierOrderLineType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
