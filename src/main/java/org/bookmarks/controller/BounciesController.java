package org.bookmarks.controller;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
@RequestMapping("/bouncies")
public class BounciesController extends AbstractStickyController {

	@Autowired
	private StockItemService stockItemService;

	private final Logger logger = LoggerFactory.getLogger(BounciesController.class);
	
	@RequestMapping(value="/manage", method=RequestMethod.GET)
	public String manage(HttpSession session, ModelMap modelMap) {
		
		Collection<StockItem> stockItems = stockItemService.getBouncies();

		logger.info("Have got {} stickies to manage", stockItems.size());
		
		modelMap.addAttribute(new StockItem());
		
		session.setAttribute("stickies", stockItems);
		session.setAttribute("bouncyObject", "bouncy!");
		
		return getManagedView();
	}		
		
	@RequestMapping(value="/save", method=RequestMethod.GET)
	public String saveStickies(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		
		List<StockItem> stockItems = (List<StockItem>) session.getAttribute("stickies");
		
		if(stockItems == null) {
			return "sessionExpired";
		}

		logger.info("Have got {} stickies to save", stockItems.size());
		
		if(stockItems.size() < 18) {
			addError("18 bouncies are needed, you only have " + stockItems.size(), modelMap);
			modelMap.addAttribute(new StockItem());
			return getManagedView();
		}

		stockItems =  new ArrayList<StockItem>(stockItems.subList(0, 18));

		for(StockItem si : stockItems)  {
			if(si.getPutImageOnWebsite() == false) {
				addError(si.getTitle() + " has put image on website set to off, please correct ", modelMap);
				modelMap.addAttribute(new StockItem());
				return getManagedView();
			}
			if(si.getPutOnWebsite() == false) {
				addError(si.getTitle() + " has put stock on website set to off, please correct ", modelMap);
				modelMap.addAttribute(new StockItem());
				return getManagedView();
			}			
		}

		stockItemService.saveBouncies(stockItems);
		
		session.removeAttribute("stickies");
		session.removeAttribute("bouncyObject");
		
		addSuccess("Have saved bouncies!", modelMap);
		
		return "welcome";
	}	

	@Override
	public Service getService() {
		return null;
	}

	@Override
	protected String getManagedView() {
		return "manageBouncies";
	}

	@Override
	protected List<StockItem> getStickies(Long id) {
		return null;
	}
}
