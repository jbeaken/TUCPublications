package org.bookmarks.controller.validation;

import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.StockItemType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class StockItemValidator implements Validator {
	    public boolean supports(Class<?> clazz) {
	    	return StockItem.class.isAssignableFrom(clazz);
	    }

	    public void validate(Object target, Errors errors) {
	    	final StockItem stockItem = (StockItem) target;
	    	
	    	//Validate Category
	    	if(stockItem.getCategory().getId() == null) {
	    		errors.rejectValue("category.id", "invalid", "Must have a valid category");
	    	}
	    	
	    	//Validate Title
	    	if(stockItem.getTitle().trim().isEmpty()) {
	    		errors.rejectValue("title", "invalid", "Must have a valid title");
	    	}
	    	
	    	//Validate publisher
	    	if(stockItem.getPublisher().getId() == null) {
	    		errors.rejectValue("publisher.id", "invalid", "Must have a valid publisher");
	    	}
	    	
	    	//Validate binding
	    	if(stockItem.getBinding() == null) {
	    		errors.rejectValue("binding", "invalid", "Must have a valid binding");
	    	}
	    	
	    	//Validate authors
//	    	if(stockItem.getConcatenatedAuthors().trim().equals("")) {
//	    		errors.rejectValue("concatenatedAuthors", "invalid", "Must have a valid author");
//	    	}
	    	
	    	//Validate discount
	    	if(stockItem.getDiscount() == null || stockItem.getDiscount().intValue() < 0 || stockItem.getDiscount().intValue() > 100) {
	    		errors.rejectValue("discount", "invalid", "Must have a valid discount");
	    	}
	    	
	    	//Validate prices
	    	if(stockItem.getCostPrice() == null){
	    		errors.rejectValue("costPrice", "invalid", "Must have a valid cost price");
	    	}
	    	
	    	if(stockItem.getSellPrice() == null){
	    		errors.rejectValue("sellPrice", "invalid", "Must have a valid sell price");
	    	}
	    	
	    	if(stockItem.getPublisherPrice() == null){
	    		errors.rejectValue("publisherPrice", "invalid", "Must have a valid publisher price");
	    	}
	    	
	    	//Validate isbn if not generating one or if it's a dvd or cd
	    	if(stockItem.getGenerateISBN() == false 
	    			&& stockItem.isBook()) {
	    		validateISBN(stockItem.getIsbn(), errors, "isbn");
	    	}
	    }

	
		public boolean validateISBN(String isbn, Errors errors, String field) {
	    	//Validate ISBN
			String message = validateISBN(isbn);
			if(message != null) {
				if(field.equals("isbn")) {
					errors.rejectValue(field, "invalidCharacter", message);
				} else {
					errors.rejectValue(field + ".isbn", "invalidCharacter", message);
				}
				return false;
			}
			return true;
		}
		
		public String validateISBN(String isbn) {
	    	//Validate ISBN
			String message = null;
			for (int i = 0; i < isbn.length(); i++) {
				char c = isbn.charAt(i);
				//If we find a non-digit character which isn't x
				if (!Character.isDigit(c)) {
					if(c != 'x' && c != 'X') {
						message =  "Invalid ISBN Character, only digits or x are allowed";
					}
				}
			}
			return message;
		}		
		
		public boolean validateISBN(StockItem stockItem, Errors errors, String field) {
			//removeHyphen(stockItem);
			if(stockItem.isCDorDVD()){
				return true;
			}
			//Validate ISBN
			return validateISBN(stockItem.getIsbn(), errors, field);
		}

		public boolean isAZLookup(StockItem stockItem, BindingResult bindingResult) {
			if(stockItem.getTitle().isEmpty() 
//					&& stockItem.getConcatenatedAuthors().isEmpty() 
					&& !stockItem.getIsbn().isEmpty()
					&& validateISBN(stockItem.getIsbn(), bindingResult, "isbn")) {
				return true;
			}
			return false;
		}
}
