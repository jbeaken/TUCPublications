package org.bookmarks.controller;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.servlet.http.HttpSession;

import org.bookmarks.domain.AbstractEntity;
import org.bookmarks.domain.CustomerType;
import org.bookmarks.domain.Publisher;
import org.bookmarks.domain.Supplier;
import org.bookmarks.service.SupplierService;
import org.bookmarks.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/supplier")
public class SupplierController extends AbstractBookmarksController<Supplier> {

	@Autowired
	private SupplierService supplierService;

	@ResponseBody
	@RequestMapping(value="/autoCompleteSupplierName", method=RequestMethod.GET)
	public String autoCompletePublisherName(String term, HttpServletRequest request, ModelMap modelMap) {
		Collection<Supplier> suppliers = supplierService.getForAutoComplete(term);
		StringBuffer buffer = new StringBuffer("[ ");
		for(Supplier s : suppliers) {
			buffer.append(" { \"label\": \"" + s.getName() + "\", \"value\": \"" + s.getId() + "\" }");
			buffer.append(", ");
		}
		String json = buffer.toString();
		json = json.substring(0, json.length() - 2) + "  ]";
		return json;
	}

	@RequestMapping(value="/search")
	public String search(SupplierSearchBean supplierSearchBean, BindingResult bindingResult, HttpSession session, HttpServletRequest request, ModelMap modelMap) {

		if(supplierSearchBean.isFromSession() == false) { //Pagination etc. already set
			setPaginationFromRequest(supplierSearchBean, request);
		} else {
			//
		}

		Collection<Supplier> suppliers = supplierService.search(supplierSearchBean);

		session.setAttribute("searchBean", supplierSearchBean);

		//Don't like, fix for shitty export
		setPageSize(supplierSearchBean, modelMap, suppliers.size());

		modelMap.addAttribute(suppliers);
		modelMap.addAttribute("searchResultCount", supplierSearchBean.getSearchResultCount());

		return "searchSuppliers";
	}

	@RequestMapping(value="/displaySearch", method=RequestMethod.GET)
	public String displaySearch(ModelMap modelMap) {
		modelMap.addAttribute(new SupplierSearchBean());
		modelMap.addAttribute("searchResultCount", 10);  //WHY? Not needed in displaySearch in CustomerController
		return "searchSuppliers";
	}


	@RequestMapping(value="/delete", method=RequestMethod.GET)
	public String delete(Long id, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		Supplier supplier = new Supplier();
		supplier.setId(id);
		supplierService.delete(supplier);
		modelMap.addAttribute(new SupplierSearchBean());
		modelMap.addAttribute("searchResultCount", 10);  //WHY? Not needed in displaySearch in CustomerController
		return searchFromSession(session, request, modelMap);
	}

	@RequestMapping(value="/searchFromSession")
	public String searchFromSession(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		SupplierSearchBean supplierSearchBean = (SupplierSearchBean) session.getAttribute("searchBean");

		supplierSearchBean.isFromSession(true);
		modelMap.addAttribute(supplierSearchBean);

		return search(supplierSearchBean, null, session, request, modelMap);
	}

	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String add(@Valid Supplier supplier, BindingResult bindingResult, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		//Check for errors
		if(bindingResult.hasErrors()){
			return "addSupplier";
		}
		Supplier duplicate = supplierService.getByName(supplier.getName());
		if(duplicate != null) {
			//Supplier with this name already exists
			bindingResult.reject("invalid", "Supplier with this name already exists");
			return "addSupplier";
		}

		//Need a redirect
		supplierService.save(supplier);
		staticDataService.resetSuppliers();

		SupplierSearchBean searchBean = new SupplierSearchBean();
		searchBean.setSupplier(supplier);
		session.setAttribute("searchBean", searchBean);

		return "redirect:searchFromSession";
	}

	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String displayAdd(HttpSession session, ModelMap modelMap) {
		modelMap.addAttribute(new Supplier());
		return "addSupplier";
	}

	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public String edit(@Valid Supplier supplier, BindingResult bindingResult, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		//Check for errors
		if(bindingResult.hasErrors()){
			return "editSupplier";
		}


		supplierService.update(supplier);
		staticDataService.resetSuppliers();

		return searchFromSession(session, request, modelMap);

	}

	@RequestMapping(value="/edit", method=RequestMethod.GET)
	public String displayEdit(Long id, String flow, HttpSession session, ModelMap modelMap) {
		Supplier supplier = supplierService.get(id);
		modelMap.addAttribute(supplier);
		modelMap.addAttribute("flow", flow);
		return "editSupplier";
	}

	@RequestMapping(value="/view", method=RequestMethod.GET)
	public String displayView(Long id, ModelMap modelMap) {
		Supplier supplier = supplierService.get(id);
		modelMap.addAttribute(supplier);
		return "viewSupplier";
	}

	@Override
	public Service getService() {
		return supplierService;
	}

}
