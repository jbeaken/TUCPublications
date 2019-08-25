package org.bookmarks.controller.validation;

import org.bookmarks.domain.SaleOrReturnOrderLine;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class SaleOrReturnOrderLineValidator  implements Validator {
    public boolean supports(Class<?> clazz) {
    	return SaleOrReturnOrderLine.class.isAssignableFrom(clazz);
    }

    public void validate(Object target, Errors errors) {
    	final SaleOrReturnOrderLine saleOrReturnOrderLine = (SaleOrReturnOrderLine) target;
    	
    	if(saleOrReturnOrderLine.getAmount() < 1) {
    		errors.rejectValue("amount", "invalid", "Invalid quantity");
    	}
    }
}
