package org.bookmarks.controller.validation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.bookmarks.website.domain.CreditCard;
import org.bookmarks.domain.CustomerOrder;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.website.domain.PaymentType;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class CustomerOrderLineValidator implements Validator {
	    public boolean supports(Class<?> clazz) {
	    	return CustomerOrderLine.class.isAssignableFrom(clazz);
	    }

	    public void validate(Object target, Errors errors) {
	    	final CustomerOrderLine customerOrderLine = (CustomerOrderLine) target;
	    	
			if(!customerOrderLine.isComplete() && customerOrderLine.getCompletionDate() != null) {
				errors.rejectValue("completionDate", "typeMismatch", "invalid");
			}
	    	//If payment is by credit card, check details
	    	//TO-DO check only numbers are used and year and date are valid and in future
	    	if(customerOrderLine.getPaymentType() == PaymentType.CREDIT_CARD) {
	    		CreditCard creditCard = customerOrderLine.getCreditCard();
//	    		if(creditCard.getCreditCard1().length() > 4 ||
//	    				creditCard.getCreditCard1().length() < 4)
//	    			errors.rejectValue("creditCard1", "typeMismatch", "invalid");
//	    		if(creditCard.getCreditCard2().length() > 4 ||
//	    				creditCard.getCreditCard2().length() < 4)
//	    			errors.rejectValue("creditCard2", "typeMismatch", "invalid");
//	    		if(creditCard.getCreditCard3().length() > 4 ||
//	    				creditCard.getCreditCard3().length() < 4)
//	    			errors.rejectValue("creditCard3", "typeMismatch", "invalid");
//	    		if(creditCard.getCreditCard4().length() > 4 ||
//	    				creditCard.getCreditCard4().length() < 4)
//	    			errors.rejectValue("creditCard4", "typeMismatch", "invalid");
//	    		if(creditCard.getExpiryMonth().length() > 2 ||
//	    				creditCard.getExpiryMonth().length() < 2)
//	    			errors.rejectValue("expiryMonth", "typeMismatch", "invalid");
//	    		if(creditCard.getExpiryYear().length() > 2 ||
//	    				creditCard.getExpiryYear().length() < 2)
//	    			errors.rejectValue("expiryYear", "typeMismatch", "invalid");
	    		if(creditCard.getSecurityCode().length() > 3 ||
	    				creditCard.getSecurityCode().length() < 3)
	    			errors.rejectValue("creditCard.securityCode", "typeMismatch", "invalid");
	    		
	    		//check card number
	    		String cardNumber = creditCard.getCreditCard1() 
	    				+ creditCard.getCreditCard2()
	    				+ creditCard.getCreditCard3()
	    				+ creditCard.getCreditCard4();
//	    		if(!validateCardNumber(cardNumber)) {
//	    			errors.rejectValue("creditCard.creditCard1", "typeMismatch", "invalid card number");
//	    		}
	    		
	    		//check expiry
	    		try {
	    			Integer year = Integer.parseInt("20" + creditCard.getExpiryYear());
	    			Integer month = Integer.parseInt(creditCard.getExpiryMonth());
	    			Calendar calendar = new GregorianCalendar(year, month - 1, 1);
	    			if(month > 12 || month < 1) {
	    				errors.rejectValue("creditCard.expiryMonth", "typeMismatch", "Invalid month");
	    			}
	    			if(calendar.getTime().getTime() < new Date().getTime()) {
	    				errors.rejectValue("creditCard.expiryMonth", "typeMismatch", "Card has expired");
	    			}
	    		} catch(Exception e) {
	    			errors.rejectValue("creditCard.expiryMonth", "typeMismatch", "Invalid expiry date");
	    		}
	    		
	    		//check security
	    		try {
	    			Integer.parseInt(creditCard.getSecurityCode());
	    		} catch(Exception e) {
	    			errors.rejectValue("creditCard.securityCode", "typeMismatch", "Invalid security code");
	    		}
	    	}
	    			
	    }
	    
	    public boolean validateCardNumber(String cardNumber) {
	    	try {
	        int sum = 0;
	        int digit = 0; 
	        int addend = 0;
	        boolean doubled = false;
	        for (int i = cardNumber.length () - 1; i >= 0; i--) {
	            digit = Integer.parseInt (cardNumber.substring (i, i + 1));
	            if (doubled) {
	                addend = digit * 2;
	                if (addend > 9) {
	                    addend -= 9; 
	                }
	            } else {
	                addend = digit;
	            }
	            sum += addend;
	            doubled = !doubled;
	        }
	        return (sum % 10) == 0;
	    	} catch(Exception e) {
	    	}
	    	return false;
	    }	    
}
