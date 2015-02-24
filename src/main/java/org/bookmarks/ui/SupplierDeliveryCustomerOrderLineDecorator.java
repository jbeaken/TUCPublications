package org.bookmarks.ui;

import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.SupplierDeliveryLine;
import org.bookmarks.domain.StockItem;
import org.displaytag.decorator.TableDecorator;

public class SupplierDeliveryCustomerOrderLineDecorator extends AbstractBookmarksTableDecorator {
	public String getLink()	{
        CustomerOrderLine customerOrderLine = (CustomerOrderLine)getCurrentRowObject();
        Long id = customerOrderLine.getId();

        return "<a href=\"fillStock?customerOrderLineID=" + id 
        		+ "&stockItemId=" + customerOrderLine.getStockItem().getId() 
        		+ "\" title=\"assign\">A</a>";
	}
	
	public String getId() {
		CustomerOrderLine customerOrderLine = (CustomerOrderLine)getCurrentRowObject();
		return customerOrderLine.getId().toString();
	}
	
	public String getCustomerName() {
		CustomerOrderLine customerOrderLine = (CustomerOrderLine)getCurrentRowObject();
		Customer customer = customerOrderLine.getCustomer();
		return getCustomerName(customer);	
	}
	
	public String getCustomerOrderStatus() {
		CustomerOrderLine customerOrderLine = (CustomerOrderLine)getCurrentRowObject();
		return customerOrderLine.getStatus().getDisplayName();
	}
	
	public String getCustomerContactDetails() {
		CustomerOrderLine customerOrderLine = (CustomerOrderLine)getCurrentRowObject();
		Customer customer = customerOrderLine.getCustomer();
		return getContactDetails(customer);
	}
}
