package org.bookmarks.domain;

import java.io.Serializable;
import java.util.Date;

public interface Entity extends Serializable {
	Long getId();

	void setId(Long id);

	Date getCreationDate();

	void setCreationDate(Date creationDate);
}
