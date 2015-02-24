package org.bookmarks.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.bookmarks.domain.SupplierDelivery;


public class SearchSupplierDeliveryDecorator extends AbstractBookmarksTableDecorator {

	
	public String getLink()	{
        SupplierDelivery supplierDelivery = (SupplierDelivery)getCurrentRowObject();
        return showView(supplierDelivery)
        		+ showEdit(supplierDelivery, false)
        		+ getImageAnchor("showText?id=" + supplierDelivery.getId() + "&flow=search", 
        		"printer.png", 
				"Print", true, false)
        		+ showEditNote(supplierDelivery);
	}
}
