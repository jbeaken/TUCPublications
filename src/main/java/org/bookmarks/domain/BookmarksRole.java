package org.bookmarks.domain;

import java.io.Serializable;

public enum BookmarksRole implements Serializable{
	BUYER("Buyer"),
	MAILORDER("Mail Order"),
	PUBLISHER("Publisher"),
	EVENT_ORGANISER("Event Organiser");

	private final String displayName;

    private BookmarksRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
