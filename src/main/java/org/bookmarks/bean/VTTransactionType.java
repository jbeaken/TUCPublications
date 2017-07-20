package org.bookmarks.bean;

public enum VTTransactionType {
	
	REC("REC"), SIN("SIN");
	
	private final String displayName;

    private VTTransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
