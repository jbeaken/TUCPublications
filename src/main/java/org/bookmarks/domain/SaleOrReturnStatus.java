package org.bookmarks.domain;

public enum SaleOrReturnStatus {
	//READY_FOR_COLLECTION("Ready for collection"),
	RETURNED("Returned"),
	WITH_CUSTOMER("With Customer"); 
	//SOLD_OUT("Sold out");
	
	private final String displayName;

    private SaleOrReturnStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
