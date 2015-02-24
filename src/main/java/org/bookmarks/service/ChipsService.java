package org.bookmarks.service;

import java.util.Collection;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.StockItem;
import org.bookmarks.website.domain.Customer;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface ChipsService {

	void removeFromWebsite(Long id);

	void putOnWebsite(Long id);

	List<Customer> getOrders() throws ClientProtocolException, IOException;

	void removeConsumedCustomers() throws ClientProtocolException, IOException;

	void evictAll() throws UnsupportedEncodingException, ClientProtocolException, IOException;

	void syncStockItemWithChips(StockItem stockItem) throws ClientProtocolException, IOException, SftpException, JSchException;

	void buildIndex() throws ClientProtocolException, IOException;

	void updateChips() throws UnsupportedEncodingException, ClientProtocolException, IOException;

	void updateReadingLists() throws ClientProtocolException, IOException;

	void updateEvents() throws UnsupportedEncodingException, ClientProtocolException, IOException;;
}
