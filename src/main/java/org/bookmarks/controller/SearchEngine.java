package org.bookmarks.controller;

public enum SearchEngine {
	LOCAL_DATABASE("Local database"), 
	ABE("ABE");
//	AZ("AZ");
	
	private final String displayName;
	
    private SearchEngine(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }	
}
