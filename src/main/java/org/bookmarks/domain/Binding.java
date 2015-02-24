package org.bookmarks.domain;

public enum Binding {
	PAPERBACK("PB"), 
	HARDBACK("HB"),
    KINDLE("Kindle"),
	DVD("DVD"),
	CD("CD"),
	OTHER("Other");
	
	private final String displayName;

    private Binding(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
