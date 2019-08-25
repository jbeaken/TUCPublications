package org.bookmarks.controller.validation;

import org.bookmarks.domain.SupplierOrder;
import org.bookmarks.domain.SupplierOrderLine;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class SupplierOrderLineValidator implements Validator {
	    public boolean supports(Class<?> clazz) {
	    	return SupplierOrderLine.class.isAssignableFrom(clazz);
	    }

	    public void validate(Object target, Errors errors) {
	    	final SupplierOrderLine supplierOrderLine = (SupplierOrderLine) target;
	    	
	  /*  	if(supplierOrderLine.getAmount() < 1) {
	    		errors.rejectValue("amount", "invalid", "Invalid quantity");
	    	}*/
	    }
}
