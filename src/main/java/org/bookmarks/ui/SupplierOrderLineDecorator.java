package org.bookmarks.ui;

import java.util.Locale;

import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.SupplierDeliveryLine;
import org.bookmarks.domain.SupplierOrder;
import org.bookmarks.domain.SupplierOrderLine;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.SupplierOrderLineStatus;
import org.displaytag.decorator.TableDecorator;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.number.CurrencyStyleFormatter;
import org.springframework.format.number.PercentStyleFormatter;

public class SupplierOrderLineDecorator extends AbstractBookmarksTableDecorator {
//	protected CurrencyStyleFormatter CurrencyStyleFormatter = new CurrencyStyleFormatter();
//	protected PercentStyleFormatter PercentStyleFormatter = new PercentStyleFormatter();
	public String getLink()	{
        SupplierOrderLine supplierOrderLine = (SupplierOrderLine)getCurrentRowObject();
        Long id = supplierOrderLine.getId();
        String text = "Put on hold";
        if(supplierOrderLine.getSupplierOrderLineStatus() == SupplierOrderLineStatus.ON_HOLD) text = "Take off hold";
        return 	getImageAnchor("markForHold?id=" + id, 
        				"green-write.gif", 
        				text, false)
				+ getImageAnchor("editSupplierOrderLine?id=" + id, 
        				"green-write.gif", 
        				"Edit", false)
        		+ getImageAnchor("deleteSupplierOrderLine?id=" + id, 
        				"green-nuke.gif", 
        				"Delete", false);
	}	
	
	public String getStatus() {
		SupplierOrderLine supplierOrderLine = (SupplierOrderLine)getCurrentRowObject();
		if(supplierOrderLine.getSupplierOrderLineStatus() == SupplierOrderLineStatus.ON_HOLD) {
			return "<span style='color:red'>On Hold</span>";
		}
		return "<span style='color:blue'>Ready To Send</span>";
	}
	
//	public String getSupplierOrderLineStatus() {
//		SupplierOrderLine supplierOrderLine = (SupplierOrderLine)getCurrentRowObject();
//		return supplierOrderLine.getSupplierOrderLineStatus().getDisplayName();
//	}	
	
	public String getTitle() {
		SupplierOrderLine supplierOrderLine = (SupplierOrderLine)getCurrentRowObject();
		return getStockItemTitleLink(supplierOrderLine.getStockItem(), "editSupplierOrderLine");
	}
	
	public String getCustomerOrder() {
		SupplierOrderLine supplierOrderLine = (SupplierOrderLine)getCurrentRowObject();
		CustomerOrderLine customerOrderLine = supplierOrderLine.getCustomerOrderLine();
		if(customerOrderLine != null) {
			return getAnchor(contextPath + "/customerOrderLine/edit?id=" + customerOrderLine.getId() + "&flow=supplierOrderEdit", 
					customerOrderLine.getId().toString(), 
					"View Customer Order", 
					true, 
					false);
		}
		return getImage("blue-nuke.png", "No Customer Order");
	}	
}
