package org.bookmarks.controller.validation;

import org.bookmarks.controller.bean.CustomerReportBean;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.StockItemType;
import org.bookmarks.exceptions.BookmarksException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CustomerReportBeanValidator implements Validator {
	    public boolean supports(Class<?> clazz) {
	    	return CustomerReportBean.class.isAssignableFrom(clazz);
	    }

	    public void validate(Object target, Errors errors) {
	    	final CustomerReportBean customerReportBean = (CustomerReportBean) target;
	    	
			switch (customerReportBean.getCustomerReportType()) {
			case INVOICE:
				validateForInvoiceReport(customerReportBean, errors);
				return;
			default:
				throw new BookmarksException("Invalid report type");
			}	    	
	    }


		private void validateForInvoiceReport(CustomerReportBean customerReportBean,
				Errors errors) {
			if(customerReportBean.getCustomer().getBookmarksAccount().getAccountHolder() == false) {
				errors.reject("noAccountForCustomer", "Customer does not have an account!");
			}
		}
}
