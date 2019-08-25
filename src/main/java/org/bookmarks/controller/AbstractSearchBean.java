package org.bookmarks.controller;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public abstract class AbstractSearchBean implements SearchBean {

	private String sortColumn;
	
	private boolean isExport;  
	 
	private Boolean groupBy = false;
	 
	public Boolean getGroupBy(){
		return groupBy; 
	} 
	
	public void setGroupBy(Boolean groupBy) {
		this.groupBy = groupBy;
	}
	
	public boolean isExport() {
		return isExport;
	} 

	public void setExport(boolean isExport) {
		this.isExport = isExport;
	}

	private String sortOrder;
	
	private Integer searchResultCount;
	
	private boolean isFromSession;
	
	public void isFromSession(boolean isFromSession) {
		this.isFromSession = isFromSession;
	}
	
	public boolean isFromSession() {
		return isFromSession;
	}
	
	public Integer getSearchResultCount() {
		return searchResultCount;
	}

	public void setSearchResultCount(Integer searchResultCount) {
		this.searchResultCount = searchResultCount;
	}

	private Integer pageSize;
	
	private Integer page;
	
	public Integer getPage() {
		return page;
	}

	public void setPage(String page) {
		if(page == null) page = "1";
		 setPage(Integer.parseInt(page));
	}

	private void setPage(int page) {
		this.page = page;
	}

	public String checkValidity() {
		return null;
	}
	
	public void setSortColumn(String sortColumn) {
		this.sortColumn = sortColumn;
	}
	
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getSortColumn() {
		return sortColumn;
	}

	public Integer getPageSize() {
		if(pageSize == null) return 10; //default
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getSortOrder() {
		if(sortOrder != null && (sortOrder.equals("1") || sortOrder.equals("DESC"))) {
			return "DESC";
		}
		return "ASC";
	}	
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
