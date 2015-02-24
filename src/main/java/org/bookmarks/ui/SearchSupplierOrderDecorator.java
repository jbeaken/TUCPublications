package org.bookmarks.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.bookmarks.domain.SupplierOrder;
import org.bookmarks.domain.SupplierOrderStatus;

public class SearchSupplierOrderDecorator extends AbstractBookmarksTableDecorator {
	DateFormat dateFormat = new SimpleDateFormat("dd/MMM/yy hh:mm");
	
	public String getLink()	{
        SupplierOrder supplierOrder = (SupplierOrder)getCurrentRowObject();
        StringBuffer images = new StringBuffer(showView(supplierOrder));
        boolean isDisabled = false;
        if(supplierOrder.getSupplierOrderStatus() == SupplierOrderStatus.SENT_TO_SUPPLIER) {
        	isDisabled = true;
        }
        return showView(supplierOrder)
        		+ showEdit(supplierOrder, isDisabled)
        		+ getImageAnchor("showText?id=" + supplierOrder.getId() + "&flow=search", 
        		"printer.png", 
				"Print", true, isDisabled)
        		//+ showDelete(supplierOrder, isDisabled)
        		+ showEditNote(supplierOrder);
	}
	
//	public String getNumberOfLines() {
//		SupplierOrder supplierOrder = (SupplierOrder)getCurrentRowObject();
//		return supplierOrder.getNoOfLines().toString();
//		
//	}
	
	//public String getOrderNumber() {
	//	SupplierOrder supplierOrder = (SupplierOrder)getCurrentRowObject();
	//	return supplierOrder.getOrderNumber() == null ? "PENDING" : supplierOrder.getOrderNumber(); 
	//}
	
	public String getSendDate() {
		SupplierOrder supplierOrder = (SupplierOrder)getCurrentRowObject();
		return supplierOrder.getSendDate() == null ? "Not Sent" : dateFormat.format(supplierOrder.getSendDate()); 
	}
	
	

	
	public String getSupplierOrderStatus() {
		SupplierOrder supplierOrder = (SupplierOrder)getCurrentRowObject();
		return supplierOrder.getSupplierOrderStatus().getDisplayName();
	}

}
