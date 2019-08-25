package org.bookmarks.domain;

import java.io.Serializable;

public enum CustomerOrderLineStatus implements Serializable {
	OUT_OF_STOCK("Out of stock"),
	OUT_OF_STOCK_CUSTOMER("Out of stock - CUS"),
	OUT_OF_STOCK_NOT_YET_PUBLISHED("Out of stock - NYP"),
	IN_STOCK("In stock"),
	PENDING_ON_ORDER("Pending - On Order"),
	ON_ORDER("On Order"),
//	RECEIVED_INTO_STOCK("Received into stock"),
//	COMPLETE_POSTED("Posted"),
//	COMPLETE_COLLECTED("Collected"),
	CANCELLED("Cancelled"),
//	AWAITING_CUSTOMER_FEEDBACK("Awaiting Customer Feedback"),
	RESEARCH("Research"),
	READY_TO_POST("Ready to post"),
	INFORM_CUSTOMER_TO_COLLECT("Inform customer to collect"),
	EMAILED_CUSTOMER("Emailed"),
	LEFT_PHONE_MESSAGE("Left Message"),
	SPOKE_TO_CUSTOMER("Spoke To Customer"),
	AWAITING_COLLECTION("Awaiting collection"), 
	COMPLETE("Complete");

	private final String displayName;

    private CustomerOrderLineStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
