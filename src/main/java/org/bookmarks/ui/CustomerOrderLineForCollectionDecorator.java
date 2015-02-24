package org.bookmarks.ui;

import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.CustomerOrderLineStatus;
import org.bookmarks.domain.SupplierDeliveryLine;
import org.bookmarks.domain.StockItem;
import org.displaytag.decorator.TableDecorator;

public class CustomerOrderLineForCollectionDecorator extends SearchCustomersDecorator {
	public String getLink()	{
        CustomerOrderLine customerOrderLine = (CustomerOrderLine)getCurrentRowObject();
        Long id = customerOrderLine.getId();

        return getImageAnchor("../customerOrderLine/informCustomer?id=" + id + "&customerOrderStatus=" + CustomerOrderLineStatus.EMAILED_CUSTOMER, 
				"blue-clipboard copy.png", 
				"Emailed customer", true)
		+ getImageAnchor("../customerOrderLine/informCustomer?id=" + id + "&customerOrderStatus=" + CustomerOrderLineStatus.LEFT_PHONE_MESSAGE, 
				"blue-database2.png", 
				"Left Telephone message", true)
		+ getImageAnchor("../customerOrderLine/informCustomer?id=" + id + "&customerOrderStatus=" + CustomerOrderLineStatus.SPOKE_TO_CUSTOMER, 
				"blue-database2.png", 
				"Spoke to customer", true);
	}
	
	public String getCustomerName() {
		CustomerOrderLine customerOrderLine = (CustomerOrderLine)getCurrentRowObject();
		return getName(customerOrderLine.getCustomer());
	}
	
	public String getCustomerContactDetails() {
		CustomerOrderLine customerOrderLine = (CustomerOrderLine)getCurrentRowObject();
		return getContactDetails(customerOrderLine.getCustomer());

	}


}
