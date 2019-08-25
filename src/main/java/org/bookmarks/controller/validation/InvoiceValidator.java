package org.bookmarks.controller.validation;

import org.bookmarks.domain.Invoice;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class InvoiceValidator implements Validator {
	    public boolean supports(Class<?> clazz) {
	    	return Invoice.class.isAssignableFrom(clazz);
	    }

	    
	    public void validateAdditionalCharges(Object target, Errors errors) {
	    	final Invoice invoice = (Invoice) target;
	    }


		@Override
		public void validate(Object target, Errors errors) {
			// TODO Auto-generated method stub
			
		}
}
