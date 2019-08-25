package org.bookmarks.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.bookmarks.controller.helper.ISBNConvertor;
import org.bookmarks.domain.AbstractEntity;
import org.bookmarks.domain.Availablity;
import org.bookmarks.domain.Binding;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.ConcreteEntity;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrder;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.Publisher;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.StockItemType;
import org.bookmarks.domain.Supplier;
import org.bookmarks.domain.SupplierDelivery;
import org.bookmarks.domain.SupplierDeliveryLine;
import org.bookmarks.service.CategoryService;
import org.bookmarks.service.PublisherService;
import org.bookmarks.service.SupplierService;
import org.displaytag.tags.TableTagParameters;
import org.displaytag.util.ParamEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.stereotype.Service;

@Service
public class StaticDataServiceImpl implements StaticDataService {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private PublisherService publisherService;

	@Autowired
	private SupplierService supplierService;
	
	private Collection<Category> categoryList;
	
	private Collection<Publisher> publisherList;
	
	private Collection<Supplier> supplierList;
	
	@Override
	public Collection<Category> getCategories() {
		if(categoryList == null) {
			categoryList = categoryService.getAllSorted("name", true);
		}
		return categoryList;
	}
	
	@Override
	public Collection<Supplier> getSuppliers() {
		if(supplierList == null) {
			supplierList = supplierService.getAllSortedExcludingMarxismSuppliers();
		}
		return supplierList;
	}
	
/*	@Override
	public Collection<Supplier> getSuppliersIncludingMarxismSuppliers() {
		if(supplierListIncludingMarxismSuppliers == null) {
			supplierListIncludingMarxismSuppliers = supplierService.getAllSorted("name", true);
		}
		return supplierListIncludingMarxismSuppliers;
	}	*/
	
	@Override
	public Collection<Publisher> getPublishers() {
		if(publisherList == null) {
			publisherList = publisherService.getAllSorted("name", true);
		}
		return publisherList;
	}
	
	@Override
	public void resetPublishers() {
		publisherList = null;
	}
	
	@Override
	public void resetCategories() {
		categoryList = null;
	}
	
	@Override
	public void resetSuppliers() {
		supplierList = null;
//		supplierListIncludingMarxismSuppliers = null;
	}

	@Override
	public StockItem getSecondHandStockItem() {
		StockItem stockItem = new StockItem();
		stockItem.setId(39625l);
		stockItem.setTitle("Second Hand");
		return stockItem;
	}
}
