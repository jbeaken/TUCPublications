package org.bookmarks.bean;

public enum VTTransactionStatus {
	PROCESSED("Processed"), UNPROCESSED("Unprocessed");
	
	private final String displayName;

    private VTTransactionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
