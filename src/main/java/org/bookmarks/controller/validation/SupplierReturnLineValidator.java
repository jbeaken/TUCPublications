package org.bookmarks.controller.validation;

import org.bookmarks.domain.SupplierReturnLine;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class SupplierReturnLineValidator implements Validator {
	    public boolean supports(Class<?> clazz) {
	    	return SupplierReturnLine.class.isAssignableFrom(clazz);
	    }

	    public void validate(Object target, Errors errors) {
	    	final SupplierReturnLine SupplierReturnLine = (SupplierReturnLine) target;
	    	
	    }
}
