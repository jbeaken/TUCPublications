package org.bookmarks.domain;

public enum StockLevel {
	IN_STOCK("In Stock"),
	OUT_OF_STOCK("Out of Stock"),
	BELOW_KEEP_IN_STOCK_LEVEL("Below Keep in Stock Level");

	private final String displayName;

    private StockLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
