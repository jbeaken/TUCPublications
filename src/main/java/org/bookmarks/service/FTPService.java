package org.bookmarks.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Collection;

import org.apache.commons.net.ftp.FTPClient;
import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;


public interface FTPService {
	
	void downloadFile(String removeFileName, String localFileName) throws SocketException, IOException;

	void uploadFile(String fileName, String location);

	boolean doesFileExist(String string) throws IOException;

	boolean doesFileExist(String string, FTPClient ftpClient) throws IOException;

	void login(FTPClient ftp) throws SocketException, IOException;
}
