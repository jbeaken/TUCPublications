package org.bookmarks.domain;

public enum EventType {
	SHOP("Shop"),
	TRADE_UNION("Trade Union"),
	EXTERNAL("External"); 
	
	private final String displayName;

    private EventType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
