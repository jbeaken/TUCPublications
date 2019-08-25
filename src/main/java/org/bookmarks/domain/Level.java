package org.bookmarks.domain;

public enum Level {
	LOW("Low"),
	MEDIUM("Medium"),
	HIGH("High");

	private final String displayName;

    private Level(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
