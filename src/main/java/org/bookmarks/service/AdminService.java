package org.bookmarks.service;

import java.io.IOException;
import java.util.List;

public interface AdminService {
	List<String> uploadAccounts() throws IOException;
}
