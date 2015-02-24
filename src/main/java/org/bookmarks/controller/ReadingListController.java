package org.bookmarks.controller;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.servlet.http.HttpSession;

import org.bookmarks.domain.AbstractEntity;
import org.bookmarks.domain.CustomerType;
import org.bookmarks.domain.ReadingList;
import org.bookmarks.domain.StockItem;
import org.bookmarks.service.ReadingListService;
import org.bookmarks.service.Service;
import org.bookmarks.service.StockItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/readingList")
public class ReadingListController extends AbstractStickyController {


	@Autowired
	private ReadingListService readingListService;
	
	@Autowired
	private StockItemService stockItemService;	
	
	@RequestMapping(value="/search")
	public String search(ReadingListSearchBean readingListSearchBean, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		
		if(readingListSearchBean == null) readingListSearchBean = new ReadingListSearchBean();
		
		if(readingListSearchBean.isFromSession() == false) { //Pagination etc. already set
			setPaginationFromRequest(readingListSearchBean, request);
		} else {
			//
		}

		Collection<ReadingList> readingLists = readingListService.search(readingListSearchBean);

		session.setAttribute("searchBean", readingListSearchBean);

		//Don't like, fix for shitty export
		setPageSize(readingListSearchBean, modelMap, readingLists.size());

		modelMap.addAttribute(readingLists);
		modelMap.addAttribute("searchResultCount", readingListSearchBean.getSearchResultCount());

		return "searchReadingLists";
	}

	@RequestMapping(value="/displaySearch", method=RequestMethod.GET)
	public String displaySearch(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		modelMap.addAttribute(new ReadingListSearchBean());
		return search(new ReadingListSearchBean(), session, request, modelMap);
	}
	
	@RequestMapping(value="/edit", method=RequestMethod.GET)
	public String edit(Long id, ModelMap modelMap) {
		ReadingList readingList = readingListService.get(id);
		modelMap.addAttribute(readingList);
		return "editReadingList";
	}	
	
	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public String edit(@Valid ReadingList readingList, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		ReadingList db = readingListService.get(readingList.getId());
		
		db.setName(readingList.getName());
		db.setIsOnWebsite(readingList.getIsOnWebsite());
		db.setIsOnSidebar(readingList.getIsOnSidebar());
		
		readingListService.update(db);
		
		addSuccess("Have saved reading list " + db.getName(), modelMap);
		return searchFromSession(session, request, modelMap);
	}		
	

//	@RequestMapping(value="/delete", method=RequestMethod.GET)
//	public String delete(Long id, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
//		ReadingList readingList = new ReadingList();
//		readingList.setId(id);
//		readingListService.delete(readingList.getId());
//		modelMap.addAttribute(new ReadingListSearchBean());
//		modelMap.addAttribute("searchResultCount", 10);  //WHY? Not needed in displaySearch in CustomerController
//		return searchFromSession(session, request, modelMap);
//	}	

	@RequestMapping(value="/searchFromSession")
	public String searchFromSession(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		ReadingListSearchBean readingListSearchBean = (ReadingListSearchBean) session.getAttribute("searchBean");
		
		if(readingListSearchBean == null) readingListSearchBean = new ReadingListSearchBean(); else readingListSearchBean.isFromSession(true);
		
		modelMap.addAttribute(readingListSearchBean);
		
		return search(readingListSearchBean, session, request, modelMap);
	}
	
	@RequestMapping(value="/delete")
	public String delete(ReadingList readingList, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		
		readingListService.delete(readingList);
		
		addSuccess("Have successfully deleted " + readingList.getName(), modelMap);
		
		return searchFromSession(session, request, modelMap);
	}	

	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String add(@Valid ReadingList readingList, BindingResult bindingResult, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		//Check for errors
		if(bindingResult.hasErrors()){
			return "addReadingList";
		}
		
		try {
			readingListService.save(readingList);
		} catch (Exception e) {
			addError("A reading list with this name already exists!", modelMap);
			modelMap.addAttribute(readingList);
			return "addReadingList";
		}
		
		ReadingListSearchBean searchBean = new ReadingListSearchBean();
		searchBean.setReadingList(readingList);
		session.setAttribute("searchBean", searchBean);
		
		return "redirect:searchFromSession";
	}

	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String displayAdd(HttpSession session, ModelMap modelMap) {
		modelMap.addAttribute(new ReadingList());
		return "addReadingList";
	}


	/*	
	@RequestMapping(value="/selectReadingList", method=RequestMethod.GET)
	public String selectReadingList(String isbn, HttpSession session, ModelMap modelMap) {
		Collection<ReadingList> readingLists = readingListService.getAllSorted("name", true);
		
		ReadingList readingList = new ReadingList();
		
		modelMap.addAttribute(readingLists);
		modelMap.addAttribute(readingList);
		
		addSuccess("Select Reading List ", modelMap);
		
		return "selectReadingList";
	}	
	
	@RequestMapping(value="/selectReadingList", method=RequestMethod.POST)
	public String selectReadingList(ReadingList readingList, HttpSession session, ModelMap modelMap) {
		StockItem stockItem = stockItemService.getByISBNAsNumber(readingList.getIsbn());
		
		readingList = readingListService.get(readingList.getId());
		readingList.add(stockItem);
		
		session.setAttribute("readingList", readingList);
		addSuccess("Added but not saved", modelMap);
		
		return "manageReadingList";
	}*/	
	
	@RequestMapping(value="/manage", method=RequestMethod.GET)
	public String manage(Long id, HttpSession session, ModelMap modelMap) {
		
		ReadingList readingList = readingListService.get(id);
		
		modelMap.addAttribute(new StockItem());
		
		session.setAttribute("stickies", readingList.getStockItems());
		session.setAttribute("readingList", readingList);
		
		return "manageReadingList";
	}		
	
	
	@RequestMapping(value="/save", method=RequestMethod.GET)
	public String save(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		
		ReadingList readingList = (ReadingList) session.getAttribute("readingList");
		List<StockItem> stickies = (List<StockItem>) session.getAttribute("stickies");
		if(readingList == null) {
			return "sessionExpired";
		}
		
		readingList.setStockItems(stickies);
		
		readingListService.update(readingList);
		
		addSuccess("Saved " + readingList.getName(), modelMap);
		
		session.removeAttribute("readingList");
		session.removeAttribute("stickies");
		
		return searchFromSession(session, request, modelMap);
	}	
	
	@Override
	public Service getService() {
		return readingListService;
	}

	@Override
	protected String getManagedView() {
		return "manageReadingList";
	}

	@Override
	protected List<StockItem> getStickies(Long id) {
		ReadingList readingList = readingListService.get(id);
		return readingList.getStockItems();
	}	
}
