package org.bookmarks.controller.validation;

import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.StockItemType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class SaleValidator implements Validator {
	    public boolean supports(Class<?> clazz) {
	    	return Sale.class.isAssignableFrom(clazz);
	    }

	    public void validate(Object target, Errors errors) {
	    	final Sale sale = (Sale) target;
	    	
	    	if(sale.getQuantity() < 1) {
	    		errors.rejectValue("quantity", "invalid", "Invalid quantity");
	    	}
	    	
	    	if(sale.getDiscount() != null) {
		    	float discount = sale.getDiscount().floatValue();
		    	if(discount < 0 || discount > 1) {
		    		errors.rejectValue("discount", "invalid", "Invalid discount");
		    	}
		    }
	    }
}
