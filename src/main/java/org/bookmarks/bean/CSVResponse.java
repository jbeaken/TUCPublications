package org.bookmarks.bean;

import java.util.List;


public class CSVResponse {
	private final String filename;
	private final List<String[]> records;

	public CSVResponse(List<String[]> records, String filename) {
		this.records = records;
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public List<String[]> getRecords() {
		return records;
	}
}