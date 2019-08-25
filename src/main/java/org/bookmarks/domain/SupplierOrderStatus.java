package org.bookmarks.domain;

public enum SupplierOrderStatus {
	PENDING("Pending"),
	SENT_TO_SUPPLIER("Sent to supplier"),
	RECEIVED("Received");
	
	private final String displayName;

    private SupplierOrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
