package org.bookmarks.controller.validation;

import org.bookmarks.domain.Event;
import org.bookmarks.domain.StockItem;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class EventValidator implements Validator {
	
	    public boolean supports(Class<?> clazz) {
	    	return Event.class.isAssignableFrom(clazz);
	    }

	    
	    public void validateAdditionalCharges(Object target, Errors errors) {
	    	final Event event = (Event) target;
	    }


		@Override
		public void validate(Object target, Errors errors) {
			final Event event = (Event) target;
			
			if(event.getOnWebsite() == true) {
				if(event.getStartTime() == null || event.getStartTime().trim().isEmpty()) {
					errors.rejectValue("stockItem", "required", "Event is on website, needs a start time");
					return;
				}				
			}
			
		}
}
