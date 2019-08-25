package org.bookmarks.ui;

import org.bookmarks.domain.Supplier;
import org.bookmarks.domain.Supplier;
import org.bookmarks.domain.TelephoneDirectory;

public class SearchSuppliersDecorator extends AbstractBookmarksTableDecorator{
	public String getLink()
	{
	        Supplier supplier = (Supplier)getCurrentRowObject();
	        return showView(supplier)        				
	        		+ showEdit(supplier)     
//					+ showSupplierOrder(supplier)					
//	        		+ showCreateSupplierReturn(supplier)    
	        		+ showEditNote(supplier);
	}
	
	private String showCreateSupplierReturn(Supplier supplier) {
		return getImageAnchor("/supplierReturn/init?supplierId=" + supplier.getId() + "&flow=search",
				"supplierReturn.png", 
				"Create Supplier Return", false, false);
	}
	
	public String showSupplierOrder(Supplier supplier) {
		return getImageAnchor("/supplierOrder/init?supplierId=" + supplier.getId(),
	        				"blue-clipboard copy.png", 
	        				"Reorder Review", false);
	}	

	public String getAddress() {
		Supplier supplier = (Supplier)getCurrentRowObject();
		return getAddress(supplier.getAddress());
	}
	
	public String getTelephone() {
		Supplier supplier = (Supplier)getCurrentRowObject();
		if(supplier.getTelephone1() == null || supplier.getTelephone1().isEmpty()) {
			return "No telephone number";
		}
		return supplier.getTelephone1();
	}
	
	public String getAccountNumber() {
		Supplier supplier = (Supplier)getCurrentRowObject();
		if(supplier.getSupplierAccount() == null || supplier.getSupplierAccount().getAccountNumber() == null || supplier.getSupplierAccount().getAccountNumber().isEmpty()) {
			return "No account number";
		}
		return supplier.getSupplierAccount().getAccountNumber();
	}
	
	public String getContactName() {
		Supplier supplier = (Supplier)getCurrentRowObject();
		return getContactName(supplier.getContactName());
	}
	
	public String getEmail() {
		Supplier supplier = (Supplier)getCurrentRowObject();
		return getEmail(supplier.getEmail());
	}
}
