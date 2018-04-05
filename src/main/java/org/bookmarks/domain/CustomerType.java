package org.bookmarks.domain;

public enum CustomerType {
	CUSTOMER("Customer"),
	WEB("Web"),
	BOOKMARKS("Bookmarks"),
	CENTRE("Centre"),
	BRANCH_DISTRICT("Branch/District"),
	TRADE("Trade"),
	INSTITUTION("Institution"),
	SISTER("Sister");

	private final String displayName;

    private CustomerType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
