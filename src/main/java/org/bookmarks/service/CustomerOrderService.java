package org.bookmarks.service;

import java.util.Collection;
import java.util.List;

import org.bookmarks.controller.CustomerOrderLineSearchBean;
import org.bookmarks.domain.CustomerOrder;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.website.domain.Customer;

public interface CustomerOrderService {

	CustomerOrder selectCustomer(Long id);

	CustomerOrder selectStockItemForCustomerOrder(Long id);

	void addStockItem(CustomerOrder customerOrder, Long id);
	
	void save(CustomerOrder customerOrder, Collection<CustomerOrderLine> collection);

	void saveChipsOrders(List<Customer> chipsCustomers);
}
