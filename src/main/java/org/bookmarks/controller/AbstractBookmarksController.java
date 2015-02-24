package org.bookmarks.controller;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.bookmarks.controller.helper.ISBNConvertor;
import org.bookmarks.domain.AbstractEntity;
import org.bookmarks.domain.Availablity;
import org.bookmarks.domain.Binding;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.ConcreteEntity;
import org.bookmarks.domain.Level;
import org.bookmarks.domain.Publisher;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.StockItemType;
import org.bookmarks.domain.StockLevel;
import org.bookmarks.domain.Supplier;
import org.bookmarks.service.StaticDataService;
import org.displaytag.tags.TableTagParameters;
import org.displaytag.util.ParamEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public abstract class AbstractBookmarksController<E extends AbstractEntity> implements BookmarkController<AbstractEntity> {
 
	@Autowired  
	protected StaticDataService staticDataService;

	protected Collection<Category> getCategories(HttpSession session) {
		return staticDataService.getCategories();
	}
	 
	protected Collection<Category> getCategories() {
		return staticDataService.getCategories();
	}

	protected Collection<Supplier> getSuppliers(HttpSession session) {
		return getSuppliers();
	}
	
	protected Collection<Supplier> getSuppliers() {
		return staticDataService.getSuppliers();
	}
	
	protected String sessionExpired(ModelMap modelMap) {
		//session has expired
		addWarning("Your session has expired", modelMap);
		return "welcome";
	}
	
/*	protected Collection<Supplier> getSuppliersIncludingMarxismSuppliers() {
		return staticDataService.getSuppliersIncludingMarxismSuppliers();
	}	*/

	protected void clearSession(HttpSession session) {
		session.removeAttribute("supplierDeliveryLinesMap");
		session.removeAttribute("supplierDelivery");
		session.removeAttribute("filledCustomerOrderLines");
		session.removeAttribute("customerOrderLineMap");
	}

	protected void setPageSize(SearchBean searchBean, ModelMap modelMap, Integer size) {
		//Don't like, fix for shitty export
		if(searchBean.isExport()) {
			modelMap.addAttribute("pageSize", size);
		} else {
			modelMap.addAttribute("pageSize", 10);
		}
	}
	protected void setPageSize(ModelMap modelMap) {
		modelMap.addAttribute("pageSize", 10);
	}

	
	protected void addError(List<ObjectError> allErrors, ModelMap modelMap) {
		StringBuilder errorMessages = new StringBuilder();
		for(ObjectError error : allErrors) {
			errorMessages.append(error.getDefaultMessage());
		}
		addError(errorMessages.toString(), modelMap);
	}
	
	protected void addInfo(String info, ModelMap modelMap) {
		modelMap.addAttribute("info", info);
	}
	
	protected void addError(String error, ModelMap modelMap) {
		modelMap.addAttribute("error", error);
	}
	
	protected void addWarning(String warning, ModelMap modelMap) {
		modelMap.addAttribute("warning", warning);
	} 
	
	protected void addSuccess(String success, ModelMap modelMap) {
		modelMap.addAttribute("success", success);
	}

	protected Collection<Publisher> getPublishers(HttpSession session) {
		return staticDataService.getPublishers();
	}

	protected Collection<Publisher> getPublishers() {
		return staticDataService.getPublishers();
	}
	
	protected Sale getSecondHandSale() {
		Sale sale = new Sale();
		sale.setDiscount(new BigDecimal(0));
		sale.setQuantity(1l);
		sale.setVat(new BigDecimal(0));
		StockItem stockItem = staticDataService.getSecondHandStockItem();
		sale.setStockItem(stockItem);
		return sale;
	}

	
	@RequestMapping(value="/searchByAjax")
	public String searchByAjax(SearchBean searchBean, ModelMap modelMap) {
		Collection<AbstractEntity> searchResults = getService().searchByAjax(searchBean);
//		modelMap.p
		return "closeWindow";
	}

	@RequestMapping(value="/saveNote", method=RequestMethod.POST)
	public String saveNote(ConcreteEntity entity, ModelMap modelMap) {
		getService().saveNote(entity);
		modelMap.addAttribute("closeWindow", "not null");
		return "closeWindow";
	}

	@RequestMapping(value="/closeWindow", method=RequestMethod.GET)
	public String closeWindow(ModelMap modelMap) {
		modelMap.addAttribute("closeWindow", "not null");
		return "closeWindow";
	}


	@RequestMapping(value="/displayEditNote", method=RequestMethod.GET)
	public String editNote(Long id, ModelMap modelMap) {
		AbstractEntity entity = getService().get(id);
		modelMap.addAttribute("entityForNote", entity);
		return "editNote";
	}

	protected boolean convertToISBN13(StockItem stockItem) {
		String isbn = stockItem.getIsbn();
		if(isbn.length() != 10) return true;

		String isbn13 = ISBNConvertor.ISBN1013(isbn);
		if(isbn13.equals("ERROR")){
			return false;
		}
		stockItem.setIsbn(isbn13);
		return true;
	}

	
	protected void fillStockSearchModel(HttpSession session, ModelMap modelMap,
			boolean reset) {
		if(reset == true) {
			modelMap.addAttribute(new StockItemSearchBean());
		}
		fillStockSearchModel(session, modelMap);
	}

	protected void fillStockSearchModel(HttpSession session, ModelMap modelMap) {
		modelMap.addAttribute(getCategories(session));
		modelMap.addAttribute(getPublishers(session));
		modelMap.addAttribute(Binding.values());
		modelMap.addAttribute(StockItemType.values());
		modelMap.addAttribute(Availablity.values());
		modelMap.addAttribute(SearchEngine.values());
		modelMap.addAttribute(StockLevel.values());
		modelMap.addAttribute(Level.values());
	}

	protected void setPaginationFromRequest(String tableId, SearchBean searchBean, HttpServletRequest request) {
		 String sortBy = request.getParameter((new ParamEncoder(tableId)).encodeParameterName(TableTagParameters.PARAMETER_SORT));
//		 String exporting = request.getParameter((new ParamEncoder(tableId)).encodeParameterName(TableTagParameters.PARAMETER_EXPORTING));
		 String exportingType = request.getParameter((new ParamEncoder(tableId)).encodeParameterName(TableTagParameters.PARAMETER_EXPORTTYPE));
//		 String sortUsingName = request.getParameter((new ParamEncoder(tableId)).encodeParameterName(TableTagParameters.PARAMETER_SORTUSINGNAME));
		 String sortOrder = request.getParameter((new ParamEncoder(tableId)).encodeParameterName(TableTagParameters.PARAMETER_ORDER));
		 String page = request.getParameter((new ParamEncoder(tableId)).encodeParameterName(TableTagParameters.PARAMETER_PAGE));
		 if(sortBy != null) {
			 searchBean.setSortColumn(sortBy);
		 }
		 if(sortOrder != null) {
			 searchBean.setSortOrder(sortOrder);
		 }
		 searchBean.setPage(page);
		 if(exportingType != null) {
			 searchBean.setExport(true);
		 } else {
			 searchBean.setExport(false);
		 }
	}

	protected void setFocus(String elementId, ModelMap modelMap) {
		modelMap.addAttribute("focusId", elementId);
	}
	
	protected void setPaginationFromRequest(SearchBean searchBean, HttpServletRequest request) {
		setPaginationFromRequest("searchTable", searchBean, request);
	}
}
