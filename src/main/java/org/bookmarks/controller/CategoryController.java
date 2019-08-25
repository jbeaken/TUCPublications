package org.bookmarks.controller;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.bookmarks.domain.CreditNote;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.ReadingList;
import org.bookmarks.domain.StockItem;
import org.bookmarks.service.CategoryService;
import org.bookmarks.service.Service;
import org.bookmarks.service.StockItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/category")
public class CategoryController extends AbstractStickyController<Category> {

	@Autowired
	private CategoryService categoryService;
	
	@RequestMapping(value="/manageStickies", method=RequestMethod.GET)
	public String manageStickies(Long id, HttpSession session, ModelMap modelMap) {
		
		Category category = categoryService.get(id);
		
		List<StockItem> stockItems = categoryService.getStickies(id);
		
		modelMap.addAttribute(new StockItem());
		
		
		session.setAttribute("stickies", stockItems);
		session.setAttribute("categoryToManage", category);
		session.removeAttribute("typeToManage");
		
		return getManagedView();
	}	
	
	@RequestMapping(value="/saveStickies", method=RequestMethod.GET)
	public String saveStickies(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		
		List<StockItem> stockItems = (List<StockItem>) session.getAttribute("stickies");
		Category category = (Category) session.getAttribute("categoryToManage");
		
		if(stockItems == null) {
			return "sessionExpired";
		}
		
		categoryService.saveStickies(stockItems, category);
		
		
		session.removeAttribute("stickies");
		session.removeAttribute("categoryToManage");
		
		addSuccess("Have saved stickies for " + category.getName(), modelMap);
		
		return searchFromSession(session, request, modelMap);
	}	
	
	
	
	

	@RequestMapping(value="/search")
	public String search(CategorySearchBean categorySearchBean, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		if(categorySearchBean.isFromSession() == false) { //Pagination etc. already set
			setPaginationFromRequest(categorySearchBean, request);
		} else {
			//
		}

		Collection<Category> categories = categoryService.search(categorySearchBean);

		//Don't like, fix for shitty export
		setPageSize(categorySearchBean, modelMap, categories.size());

		//Add to session for later search
		session.setAttribute("categorySearchBean", categorySearchBean);

		modelMap.addAttribute("categoriesList", categories);
		modelMap.addAttribute("searchResultCount", categorySearchBean.getSearchResultCount());
		
		//setFocus("categoryAutoComplete", modelMap);
		
		return "searchCategories";
	}

	
	@ResponseBody
	@RequestMapping(value="/autoCompleteName", method=RequestMethod.GET)
	public String autoCompleteName(String term, HttpServletRequest request, ModelMap modelMap) {
		/*Collection<Category> categories = categoryService.getForAutoComplete(term);
		StringBuffer buffer = new StringBuffer("[ ");
		for(Category c : categories) {
			String postcode = c.getAddress().getPostcode();
			buffer.append(" { \"label\": \"" + c.getLastName() + ", " + c.getFirstName() + " " + (postcode != null ? postcode : "") + "\", \"value\": \"" + c.getId() + "\" }");
			buffer.append(", ");
		}
		String json = buffer.toString();
		json = json.substring(0, json.length() - 2) + "  ]";
		//System.out.println(json);
		return json;*/
		return "";
	}



	@RequestMapping(value="/displaySearch", method=RequestMethod.GET)
	public String displaySearch(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		CategorySearchBean categorySearchBean = new CategorySearchBean();
		modelMap.addAttribute(categorySearchBean);
		return search(categorySearchBean, request, session, modelMap);
	}

	@RequestMapping(value="/delete", method=RequestMethod.GET)
	public String delete(Long id, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		Category category = categoryService.get(id);
		try {
			categoryService.delete(category);
			staticDataService.resetCategories();
			addSuccess(category.getName() + " has been deleted", modelMap);
		} catch (Exception e) {
			//Most likely due to this invoice being referenced from col
			addError("Cannot delete! Perhaps this category has stock items in it", modelMap);
		}		
		
		CategorySearchBean categorySearchBean = new CategorySearchBean();
		modelMap.addAttribute(categorySearchBean);
		
		return search(categorySearchBean, request, session, modelMap);
	}	

	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String add(@Valid Category category, BindingResult bindingResult, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		//Check for errors
		if(bindingResult.hasErrors()){
			modelMap.addAttribute(getCategories());
			return "addCategory";
		}
		
		if(category.getParent().getId() == null) {
			category.setParent(null); //Why do I have to do this?
			try {
				categoryService.save(category);
			} catch (org.hibernate.exception.ConstraintViolationException e) {
				modelMap.addAttribute(getCategories());
				addError("Cateogory with this name already exists!", modelMap);
				return "addCategory";
			}
		} else {
			Category parent = categoryService.get(category.getParent().getId());
			parent.add(category);
			categoryService.update(parent);
		}
		
		staticDataService.resetCategories();
		
		CategorySearchBean csb = new CategorySearchBean();
		csb.setCategory(category);
		csb.setPage("1");
		session.setAttribute("categorySearchBean", csb);
		
		return "redirect:searchFromSession";
	}

	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String displayAdd(ModelMap modelMap) {
		
		modelMap.addAttribute(new Category());
		modelMap.addAttribute(getCategories());
		
		return "addCategory";
	}

	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public String edit(@Valid Category category, BindingResult bindingResult, String flow, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		//Check for errors
		if(bindingResult.hasErrors()){
			modelMap.addAttribute("flow", flow);
			modelMap.addAttribute(getCategories());
			return "editCategory";
		}
		
		Category dbCategory = categoryService.get(category.getId());

		if(category.getParent().getId() == null) {
			dbCategory.setParent(null); 
		}
		
		dbCategory.setName(category.getName());
		dbCategory.setIsOnWebsite(category.getIsOnWebsite());
		dbCategory.setIsInSidebar(category.getIsInSidebar());

		categoryService.update(dbCategory);
		
		staticDataService.resetCategories();
		
		if(flow.equals("invoiceSearch") || flow.equals("categoryOrderSearch")) {
			modelMap.addAttribute("closeWindow", "not null");
			return "closeWindow";
		}

		return "redirect:searchFromSession";
	}

	@RequestMapping(value="/edit", method=RequestMethod.GET)
	public String displayEdit(Long id, String flow, ModelMap modelMap) {
		Category category = categoryService.get(id);
		modelMap.addAttribute(category);
		modelMap.addAttribute(getCategories());
		modelMap.addAttribute("flow", flow);
		return "editCategory";
	}


	@RequestMapping(value="/searchFromSession")
	public String searchFromSession(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		CategorySearchBean categorySearchBean = (CategorySearchBean) session.getAttribute("categorySearchBean");
		categorySearchBean.isFromSession(true);
		modelMap.addAttribute(categorySearchBean);
		return search(categorySearchBean, request, session, modelMap);
	}

	@Override
	public Service<Category> getService() {
		return categoryService;
	}

	@Override
	protected String getManagedView() {
		return "manageStickyCategories";
	}

	@Override
	protected List<StockItem> getStickies(Long id) {
		return categoryService.getStickies(id);
	}
}
