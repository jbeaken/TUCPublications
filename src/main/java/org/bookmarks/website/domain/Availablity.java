package org.bookmarks.website.domain;

public enum Availablity {
	OUT_OF_PRINT("Out of print"),
	PUBLISHED("Published"),
	NOT_YET_PUBLISHED("Not yet published");
	
	private final String displayName;

    private Availablity(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }	
}
