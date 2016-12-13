package org.bookmarks.controller;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.bookmarks.domain.AbstractEntity;
import org.bookmarks.domain.Publisher;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.Supplier;
import org.bookmarks.service.PublisherService;
import org.bookmarks.service.Service;
import org.bookmarks.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/publisher")
public class PublisherController extends AbstractBookmarksController {

	@Autowired
	private PublisherService publisherService;

	@Autowired
	private SupplierService supplierService;

	@ResponseBody
	@RequestMapping(value="/autoCompletePublisherName", method=RequestMethod.GET)
	public String autoCompletePublisherName(String term, HttpServletRequest request, ModelMap modelMap) {
		Collection<Publisher> publishers = publisherService.getForAutoComplete(term);

		StringBuffer buffer = new StringBuffer("[ ");

		for(Publisher p : publishers) {
			buffer.append(" { \"label\": \"" + p.getName() + "\", \"value\": \"" + p.getId() + "\" }");
			buffer.append(", ");
		}

		String json = buffer.toString();

		json = json.substring(0, json.length() - 2) + "  ]";

		//System.out.println(json);

		return json;
	}

	@RequestMapping(value="/search")
	public String search(PublisherSearchBean publisherSearchBean, BindingResult bindingResult, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		setPaginationFromRequest(publisherSearchBean, request);

		Collection<Publisher> publishers = publisherService.search(publisherSearchBean);

		session.setAttribute("searchBean", publisherSearchBean);

		//Don't like, fix for shitty export
		setPageSize(publisherSearchBean, modelMap, publishers.size());

		modelMap.addAttribute(publishers);
		modelMap.addAttribute("searchResultCount", publisherSearchBean.getSearchResultCount());

		return "searchPublishers";
	}

	@RequestMapping(value="/displaySearch", method=RequestMethod.GET)
	public String displaySearch(ModelMap modelMap) {
		modelMap.addAttribute(new PublisherSearchBean());
		return "searchPublishers";
	}

	@RequestMapping(value="/searchFromSession")
	public String searchFromSession(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		PublisherSearchBean publisherSearchBean = (PublisherSearchBean) session.getAttribute("searchBean");
		publisherSearchBean.isFromSession(true);
		modelMap.addAttribute(publisherSearchBean);
		return search(publisherSearchBean, null, session, request, modelMap);
	}

	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String add(String flow, HttpSession session, ModelMap modelMap) {
		modelMap.addAttribute(new Publisher());
		modelMap.addAttribute(getSuppliers());

		modelMap.addAttribute("flow", flow);

		return "addPublisher";
	}

	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String add(@Valid Publisher publisher, BindingResult bindingResult, String flow, HttpSession session, ModelMap modelMap) {

		Publisher exists = publisherService.getByName(publisher.getName());
		if(exists != null) {
			bindingResult.addError(new ObjectError("publisher", "This publisher already exists"));
		}

		//Check for errors
		if(bindingResult.hasErrors()){
			addError(bindingResult.getAllErrors(), modelMap);
			modelMap.addAttribute(getSuppliers());
			return "addPublisher";
		}

		Supplier supplier = supplierService.get(publisher.getSupplier().getId());
		publisher.setSupplier(supplier);

		publisherService.save(publisher);

		staticDataService.resetPublishers();

		addSuccess("Have successfully added publisher", modelMap);

		if(flow != null && (flow.equals("addStock") || flow.equals("editStock"))) {
			StockItem stockItem = (StockItem) session.getAttribute("sessionStockItem");
			if(stockItem == null) {
				return "sessionExpired";
			}
			stockItem.setPublisher(publisher);
			fillStockSearchModel(session, modelMap);
			modelMap.addAttribute(stockItem);
			return flow; //Either addStock or editStock
		}

		return "viewPublisher";
	}

	@RequestMapping(value="/addAndCreateSupplier", method=RequestMethod.POST)
	public String addAndCreateSupplier(@Valid Publisher publisher, String flow, BindingResult bindingResult, HttpSession session, ModelMap modelMap) {
		Supplier exists = supplierService.getByName(publisher.getName());
		if(exists != null) {
			bindingResult.addError(new ObjectError("supplier", "This supplier already exists"));
		}

		//Check for errors
		if(bindingResult.hasErrors()){
			addError(bindingResult.getAllErrors(), modelMap);
			modelMap.addAttribute(getSuppliers());
			return "addPublisher";
		}

		Supplier supplier = new Supplier(publisher);

		supplierService.save(supplier);

		publisher.setSupplier(supplier);

		String result = add(publisher, bindingResult, flow, session, modelMap);

		if(result.equals("addPublisher")) return result; //error has occured

		staticDataService.resetSuppliers();

		return result;
	}



	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public String edit(@Valid Publisher publisher, BindingResult bindingResult, String flow, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		//Check for errors
		if(bindingResult.hasErrors()){
			modelMap.addAttribute(getSuppliers());
			return "editPublisher";
		}

		Supplier supplier = supplierService.get(publisher.getSupplier().getId());
		publisher.setSupplier(supplier);

		publisherService.update(publisher);
		staticDataService.resetPublishers();

		if(flow !=null & flow.equals("editStock")) {
			modelMap.addAttribute("closeWindow", "not null");
			return "closeWindow";
		}
		return searchFromSession(session, request, modelMap);
	}

	@RequestMapping(value="/edit", method=RequestMethod.GET)
	public String displayEdit(Long id, String flow, ModelMap modelMap) {
		Publisher publisher = publisherService.get(id);

		modelMap.addAttribute(publisher);
		modelMap.addAttribute(getSuppliers());
		modelMap.addAttribute("flow", flow);

		return "editPublisher";
	}

	@RequestMapping(value="/view", method=RequestMethod.GET)
	public String displayView(Long id, ModelMap modelMap) {
		Publisher publisher = publisherService.get(id);
		modelMap.addAttribute(publisher);
		return "viewPublisher";
	}

	@Override
	public Service getService() {
		return publisherService;
	}

}
