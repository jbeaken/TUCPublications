package org.bookmarks.domain;

public enum SupplierOrderLineStatus {
	READY_TO_SEND("Ready to send"),
	SENT_TO_SUPPLIER("Sent to supplier"),
	ON_HOLD("On Hold");
	
	private final String displayName;

    private SupplierOrderLineStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
