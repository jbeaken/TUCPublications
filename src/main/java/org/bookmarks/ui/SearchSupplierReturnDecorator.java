package org.bookmarks.ui;

import org.bookmarks.domain.SupplierReturn;


public class SearchSupplierReturnDecorator extends AbstractBookmarksTableDecorator {

	
	public String getLink()	{
        SupplierReturn supplierReturn = (SupplierReturn)getCurrentRowObject();
        return showView(supplierReturn)
        		+ showEdit(supplierReturn, false)
        		+ getImageAnchor("showText?id=" + supplierReturn.getId() + "&flow=search", 
        		"printer.png", 
				"Print", true, false)
        		+ showEditNote(supplierReturn);
	}
}
