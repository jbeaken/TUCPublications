package org.bookmarks.controller.validation;

import org.bookmarks.domain.InvoiceOrderLine;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.StockItemType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class InvoiceOrderLineValidator implements Validator {
	    public boolean supports(Class<?> clazz) {
	    	return InvoiceOrderLine.class.isAssignableFrom(clazz);
	    }

	    public void validate(Object target, Errors errors) {
	    	final InvoiceOrderLine invoiceOrderLine = (InvoiceOrderLine) target;
	    	
//	    	if(invoiceOrderLine.getAmount() < 1) {
//	    		errors.rejectValue("amount", "invalid", "Invalid quantity");
//	    	}
	    	
//	    	if(invoiceOrderLine.getDiscount() != null) {
//		    	float discount = invoiceOrderLine.getDiscount().floatValue();
//		    	if(discount < 0 || discount > 1) {
//		    		errors.rejectValue("discount", "invalid", "Invalid discount");
//		    	}
//		    }
	    }
}
