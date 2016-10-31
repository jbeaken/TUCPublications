package org.bookmarks.controller.validation;

import org.bookmarks.website.domain.CreditCard;
import org.bookmarks.domain.CustomerOrder;
import org.bookmarks.website.domain.PaymentType;
import org.bookmarks.website.domain.DeliveryType;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class CustomerOrderValidator implements Validator {
	    public boolean supports(Class<?> clazz) {
	    	return CustomerOrder.class.isAssignableFrom(clazz);
	    }

	    public void validate(Object target, Errors errors) {
	    	final CustomerOrder customerOrder = (CustomerOrder) target;

	    	//If payment is by credit card, check details
	    	//TO-DO check only numbers are used and year and date are valid and in future
	    	CreditCard creditCard = customerOrder.getCreditCard();
	    	if(customerOrder.getPaymentType() == PaymentType.CREDIT_CARD) {

	    		if(creditCard.getCreditCard1() == null || creditCard.getCreditCard1().length() > 4 ||
	    				creditCard.getCreditCard1().length() < 4)
	    			errors.rejectValue("creditCard.creditCard1", "typeMismatch", "invalid");
	    		if(creditCard.getCreditCard2() == null || creditCard.getCreditCard2().length() > 4 ||
	    				creditCard.getCreditCard2().length() < 4)
	    			errors.rejectValue("creditCard.creditCard2", "typeMismatch", "invalid");
	    		if(creditCard.getCreditCard3() == null || creditCard.getCreditCard3().length() > 4 ||
	    				creditCard.getCreditCard3().length() < 4)
	    			errors.rejectValue("creditCard.creditCard3", "typeMismatch", "invalid");
	    		if(creditCard.getCreditCard4() == null || creditCard.getCreditCard4().length() > 4 ||
	    				creditCard.getCreditCard4().length() < 4)
	    			errors.rejectValue("creditCard.creditCard4", "typeMismatch", "invalid");
	    		if(creditCard.getExpiryMonth() == null || creditCard.getExpiryMonth().length() > 2 ||
	    				creditCard.getExpiryMonth().length() < 2)
	    			errors.rejectValue("creditCard.expiryMonth", "typeMismatch", "invalid");
	    		if(creditCard.getExpiryYear() == null || creditCard.getExpiryYear().length() > 2 ||
	    				creditCard.getExpiryYear().length() < 2)
	    			errors.rejectValue("creditCard.expiryYear", "typeMismatch", "invalid");
	    		if(creditCard.getSecurityCode() == null ||creditCard.getSecurityCode().length() > 3 ||
	    				creditCard.getSecurityCode().length() < 3)
	    			errors.rejectValue("creditCard.securityCode", "typeMismatch", "invalid");
	    	}
			//If mail order, cannot be cash payment, only paid, account or cheque
			if(customerOrder.getDeliveryType() == DeliveryType.MAIL && customerOrder.getPaymentType() == PaymentType.CASH) {
				errors.rejectValue("paymentType", "cashWithMailorder", "Cannot have cash payment with mail order delivery.");
			}
	    }
}
