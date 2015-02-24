package org.bookmarks.ui;

import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.SupplierDeliveryLine;
import org.bookmarks.domain.StockItem;
import org.displaytag.decorator.TableDecorator;

/**
* Used by selectStockItemsForCustomerOrder.jsp, when creating customer order
* Not used by searchCustomerOrderLines.jsp, see SearchCustomerOrderLineDecorator.java
*/
public class CustomerOrderLineDecorator extends SearchStockItemsDecorator {
	public String getLink()	{
        CustomerOrderLine customerOrderLine = (CustomerOrderLine)getCurrentRowObject();
        Long id = customerOrderLine.getStockItem().getId();
        String researchText = "";
        if(customerOrderLine.getIsResearch() == Boolean.TRUE) {
        	researchText = "Remove Research";
		} else {
			researchText = "Set As Research";
		}
        return
        		getImageAnchor(contextPath + "/customerOrder/editCustomerOrderLine?stockItemId=" + id,
        				"write_medium.png",
        				"Edit Order", false)
        		+ getImageAnchor(contextPath + "/customerOrder/deleteCustomerOrderLine?stockItemId=" + id,
        				"blue-nuke.png",
        				"Delete Order", false)
	        + getImageAnchor(contextPath + "/customerOrderLine/setAsResearch?stockItemId=" + id,
	        		"research.png",
	        		researchText, false);
	}

	public String getId() {
		CustomerOrderLine customerOrderLine = (CustomerOrderLine)getCurrentRowObject();
		return getAnchor(contextPath + "/customerOrderLine/edit?customerOrderLineId=" + customerOrderLine.getId(),
		        				customerOrderLine.getId().toString(),
        				"Edit", false,false);
	}
	
	public String getPrices() {
		CustomerOrderLine customerOrderLine = (CustomerOrderLine)getCurrentRowObject();
		return getPrices(customerOrderLine.getStockItem());
	}
}
