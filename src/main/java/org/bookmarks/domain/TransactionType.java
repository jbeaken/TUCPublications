package org.bookmarks.domain;

public enum TransactionType {
  TFR("TRF (From TSB)");
  
  String displayName;

  TransactionType(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
