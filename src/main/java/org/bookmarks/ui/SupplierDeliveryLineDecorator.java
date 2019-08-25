package org.bookmarks.ui;

import java.util.Locale;

import org.bookmarks.domain.SupplierDeliveryLine;
import org.bookmarks.domain.SupplierOrderLine;

public class SupplierDeliveryLineDecorator extends SearchStockItemsDecorator {


	public String getLink()	{
        SupplierDeliveryLine supplierDeliveryLine = (SupplierDeliveryLine)getCurrentRowObject();

        if(supplierDeliveryLine.getHasCustomerOrderLines()) {
        	return showFillCustomerOrders(supplierDeliveryLine)
        			+ showEdit(supplierDeliveryLine)
        			+ showDelete(supplierDeliveryLine) ;
        }
        return  showEdit(supplierDeliveryLine)
        	+ showDelete(supplierDeliveryLine) ;
	}

	public String getDiscount() {
		SupplierDeliveryLine supplierDeliveryLine = (SupplierDeliveryLine)getCurrentRowObject();
		return supplierDeliveryLine.getDiscount().toString() + "%";
	}
	
	public String getTitle() {
		SupplierDeliveryLine supplierDeliveryLine = (SupplierDeliveryLine)getCurrentRowObject();
		return getStockTitleLink(supplierDeliveryLine.getStockItem(), "editSupplierOrderLine");
	}	

	private String showEdit(SupplierDeliveryLine supplierDeliveryLine) {
		Long stockItemid = supplierDeliveryLine.getStockItem().getId();
		return getImageAnchor(contextPath + "/supplierDelivery/editSupplierDeliveryOrderLine?id=" + stockItemid,
				"write_medium.png",
				"Edit", false);
	}

	private  String showDelete(SupplierDeliveryLine supplierDeliveryLine) {
		Long stockItemid = supplierDeliveryLine.getStockItem().getId();
		return getImageAnchor(contextPath + "/supplierDelivery/deleteSupplierDeliveryOrderLine?id=" + stockItemid,
				"delete_medium.png",
				"Delete", false);
	}

	public String getPublisherPrice() {
		SupplierDeliveryLine supplierDeliveryLine = (SupplierDeliveryLine)getCurrentRowObject();
		return CurrencyStyleFormatter.print(supplierDeliveryLine.getPublisherPrice(), Locale.UK);
	}
	

	public String getSellPrice() {
		SupplierDeliveryLine supplierDeliveryLine = (SupplierDeliveryLine)getCurrentRowObject();
		return CurrencyStyleFormatter.print(supplierDeliveryLine.getStockItem().getSellPrice(), Locale.UK);
	}	
	
	public String getTotalPrice() {
		SupplierDeliveryLine supplierDeliveryLine = (SupplierDeliveryLine)getCurrentRowObject();
		return CurrencyStyleFormatter.print(supplierDeliveryLine.getPrice(), Locale.UK);
	}	

	private  String showFillCustomerOrders(SupplierDeliveryLine supplierDeliveryLine) {
        Long stockItemid = supplierDeliveryLine.getStockItem().getId();
		return getImageAnchor(contextPath + "/supplierDelivery/displayCustomerOrderLinesToFill?stockItemId=" + stockItemid,
    			"blue-database2.png",
    			"Fill customer orders", false);
	}
}
