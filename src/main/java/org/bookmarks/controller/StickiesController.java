package org.bookmarks.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.StockItemType;
import org.bookmarks.service.Service;
import org.bookmarks.service.StockItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/stickies")
public class StickiesController extends AbstractStickyController {

	@Autowired
	private StockItemService stockItemService;
	
	@RequestMapping(value="/manageStickyStockItemType", method=RequestMethod.POST)
	public String manageStickyStockItemType(StockItem stockItem, HttpSession session, ModelMap modelMap) {
		
		StockItemType type = stockItem.getType();
		List<StockItem> stockItems = stockItemService.getStickies(type);
		
		modelMap.addAttribute(new StockItem());
		
		session.setAttribute("stickies", stockItems);
		session.setAttribute("typeToManage", type);
		session.removeAttribute("categoryToManage");
		
		return getManagedView();
	}	
	
	@RequestMapping(value="/save", method=RequestMethod.GET)
	public String saveStickies(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		
		List<StockItem> stockItems = (List<StockItem>) session.getAttribute("stickies");
		StockItemType type = (StockItemType) session.getAttribute("typeToManage");
		
		if(stockItems == null) {
			return "sessionExpired";
		}
		
		stockItemService.saveStickies(stockItems, type);
		
		
		session.removeAttribute("stickies");
		session.removeAttribute("typeToManage");
		
		addSuccess("Have saved stickies for " + type.getDisplayName(), modelMap);
		
		return "welcome";
	}	
	
	@RequestMapping(value="/displayStockItemTypes", method=RequestMethod.GET)
	public String displayStockItemTypes(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		
		modelMap.addAttribute(StockItemType.values());
		modelMap.addAttribute(new StockItem());
		
		addInfo("Select the type to edit stickies for", modelMap);
		
		return "displayStockItemTypes";
	}	
	

	@Override
	public Service getService() {
		return null;
	}

	@Override
	protected String getManagedView() {
		return "manageStickyStockItemTypes";
	}

	@Override
	protected List<StockItem> getStickies(Long id) {
		return null;
	}
}
