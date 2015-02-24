package org.bookmarks.controller.validation;

import org.bookmarks.domain.SupplierDeliveryLine;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class SupplierDeliveryLineValidator implements Validator {
	    public boolean supports(Class<?> clazz) {
	    	return SupplierDeliveryLine.class.isAssignableFrom(clazz);
	    }

	    public void validate(Object target, Errors errors) {
	    	final SupplierDeliveryLine SupplierDeliveryLine = (SupplierDeliveryLine) target;
	    	
	    }
}
