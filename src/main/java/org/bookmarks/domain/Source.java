package org.bookmarks.domain;

public enum Source {
	PHONE("Phone"),
	WEB("Web"),
    EMAIL("Email"),
    LETTER("Letter"),
	IN_PERSON("In Person"),
	UNKNOWN("Unknown");

	private final String displayName;

    private Source(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
