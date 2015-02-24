package org.bookmarks.controller.validation;

import org.bookmarks.controller.bean.SaleReportBean;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.StockItemType;
import org.bookmarks.exceptions.BookmarksException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class SaleReportBeanValidator implements Validator {
	    public boolean supports(Class<?> clazz) {
	    	return SaleReportBean.class.isAssignableFrom(clazz);
	    }

	    public void validate(Object target, Errors errors) {
	    	final SaleReportBean saleReportBean = (SaleReportBean) target;
	    	return;
//			switch (saleReportBean.getSalesReportType()) {
//			case SALE_LIST:
//				validateForSaleList(saleReportBean, errors);
//				return;
//			case BY_CATEGORY:
//				validateForCategoryReport(saleReportBean, errors);
//				return;
//			case CATEGORY_STOCK_TAKE:
//				validateForCategoryReport(saleReportBean, errors);
//				return;
//			case TIME_OF_DAY:
//				validateForTimeOfDay(saleReportBean, errors);
//				return;	
//			case PUBLISHER_STOCK_TAKE:
//				validateForPublisherReport(saleReportBean, errors);
//				return;				
//			default:
//				throw new BookmarksException("Invalid report type");
//			}	    	
	    }
	
		private void validateForTimeOfDay(SaleReportBean saleReportBean, Errors errors) {
			
		}

		private void validateForCategoryReport(SaleReportBean saleReportBean,Errors errors) {
		}
		
	
		private void validateForPublisherReport(SaleReportBean saleReportBean,
				Errors errors) {
		}		

		private void validateForSaleList(SaleReportBean saleReportBean,
				Errors errors) {
			if(saleReportBean.getIsDateAgnostic() == false) {
				if(saleReportBean.getEndHour() == null 
						|| saleReportBean.getStartHour() == null
						|| saleReportBean.getEndMinute() == null
						|| saleReportBean.getStartMinute() == null
						|| saleReportBean.getEndDate() == null
						|| saleReportBean.getStartDate() == null) {
					errors.rejectValue("startDate","typeMismatch");
				}			
			}
		}
}
