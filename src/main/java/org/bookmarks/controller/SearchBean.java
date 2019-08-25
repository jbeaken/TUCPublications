package org.bookmarks.controller;

public interface SearchBean {
	String checkValidity();
	
	String getSortColumn();
	
	String getSortOrder();
	
	void reset();
	
	boolean isExport();

	void setExport(boolean isExport);
	
	void setSortColumn(String sortColumn);

	void setSortOrder(String sortOrder);
	
	void setPage(String page);
	
	Integer getPage();
	
	Integer getPageSize();
	
	void setPageSize(Integer pageSize);
	
	Integer getSearchResultCount();
	
	void setSearchResultCount(Integer searchResultCount);
	
	void isFromSession(boolean isFromSession);
	
	boolean isFromSession();
	
	Boolean getGroupBy();
	
	void setGroupBy(Boolean groupBy);
}
