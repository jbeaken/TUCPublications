package org.bookmarks.domain;

public enum SupplierReturnStatus {
	ON_SHELVES("On Shelves"),
	AWAITING_CREDIT("Awaiting Credit"),
	COMPLETE("Complete");

	private final String displayName;

		private SupplierReturnStatus(String displayName) {
				this.displayName = displayName;
		}

		public String getDisplayName() {
				return displayName;
		}
}
