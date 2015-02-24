package org.bookmarks.controller.validation;

import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.SupplierDelivery;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class SupplierInvoiceValidator implements Validator {
	    public boolean supports(Class<?> clazz) {
	    	return SupplierDelivery.class.isAssignableFrom(clazz);
	    }

	    public void validate(Object target, Errors errors) {
	    	final SupplierDelivery supplierInvoice = (SupplierDelivery) target;
	    	
	    	//Validate Category
//	    	if(stockItem.getCategory().getId() == null) {
//	    		errors.rejectValue("category", "invalid", "Must have a valid category");
//	    	}
//	    	
//	    	//Validate publisher
//	    	if(stockItem.getPublisher().getId() == null) {
//	    		errors.rejectValue("publisher", "invalid", "Must have a valid publisher");
//	    	}
//	    	
//	    	//Validate ISBN
//	    	String isbn = stockItem.getIsbn();
//			//If a category has been selected then it's always valid
//			for (int i = 0; i < isbn.length(); i++) {
//				char c = isbn.charAt(i);
//				//If we find a non-digit character which isn't x
//				if (!Character.isDigit(c)) {
//					if(c != 'x' && c != 'X') {
//						errors.rejectValue("isbn", "invalidCharacter", "Invalid ISBN Character, only digits or x are allowed");
//					break;
//				}
//				}
//			}
//			if(isbn.length() != 10 && isbn.length() != 13) {
//				errors.rejectValue("isbn", "invalidLength", "Invalid ISBN Length, must be 10 or 13 digits");
//			}
	    }
}
