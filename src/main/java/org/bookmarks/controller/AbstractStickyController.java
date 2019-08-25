package org.bookmarks.controller;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.bookmarks.domain.AbstractEntity;
import org.bookmarks.domain.Category;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.StockItemType;
import org.bookmarks.service.StockItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public abstract class AbstractStickyController<AbstractEntity> extends AbstractBookmarksController {
	
	@Autowired
	private StockItemService stockItemService;	
	
	@RequestMapping(value="/addSticky", method=RequestMethod.POST)
	public String addSticky(StockItem stockItem, HttpSession session, ModelMap modelMap) {
		
		List<StockItem> stockItems = (List<StockItem> ) session.getAttribute("stickies");
		
		if(stockItems == null) {
			return "sessionExpired";
		}
		
		StockItem dbStockItem = null;
		//ISBN valid
		try {
			dbStockItem = stockItemService.getFullStockItemByISBNAsNumber(stockItem.getIsbn());
		} catch (Exception e) {
			addError("Invalid isbn", modelMap);
			modelMap.addAttribute(stockItem);
			return getManagedView();
		}
		
		//ISBN exists?
		if(dbStockItem == null) {
			addError("Cannot find isbn in database", modelMap);
			modelMap.addAttribute(stockItem);
			return getManagedView();
		}		
		

		
		//Deviations, can be readingListSticky, categorySticky, bouncies or typeSticky
		Category category = (Category) session.getAttribute("categoryToManage");
		if(category != null) {
			//Is it correct category??
			if(!dbStockItem.getCategory().getId().equals(category.getId())) {
				addError("This stock is not in category " + category.getName(), modelMap);
				modelMap.addAttribute(stockItem);
				return getManagedView();
			}	
		}
		
		String bouncyObject = (String) session.getAttribute("bouncyObject");
		if(bouncyObject != null) {
			//Has image?
			if(dbStockItem.getImageFilename() == null) {
				//addError("This stock hasn't got an image", modelMap);
				//modelMap.addAttribute(stockItem);
				//return getManagedView();
			}	
			if(dbStockItem.getTitle().length() > 100) {
				addError("This stock has too large a title : " + dbStockItem.getTitle(), modelMap);
				modelMap.addAttribute(stockItem);
				return getManagedView();
			}
		}		
		
		StockItemType type = (StockItemType) session.getAttribute("typeToManage");
		if(type != null) {
			//Is it correct category??
			if(!dbStockItem.getType().equals(type)) {
				addError("This stock is not of type " + type.getDisplayName(), modelMap);
				modelMap.addAttribute(stockItem);
				return getManagedView();
			}	
		}		
		
		//Duplication?
		for(StockItem si : stockItems) {
			if(si.getId().equals(dbStockItem.getId())) {
				addError("This stock has already been added", modelMap);
				modelMap.addAttribute(stockItem);
				return getManagedView();
			}
		}

		modelMap.addAttribute(new StockItem());

		stockItems.add(0, dbStockItem);
		
		//Can it be put on website??
		if(dbStockItem.getPutOnWebsite() == false) {
			addWarning("This stock is set to not be on the website", modelMap);
		} else addSuccess("Added but not saved", modelMap);
		
		return getManagedView();
	}		
	
	@RequestMapping(value="/moveUp", method=RequestMethod.GET)
	public String moveUp(Integer index, HttpSession session, ModelMap modelMap) {
		
		List<StockItem> stockItems = (List<StockItem> ) session.getAttribute("stickies");
		if(stockItems == null) {
			return "sessionExpired";
		}
		if(index != 0) {
			Collections.swap(stockItems, index, index -1);
		}
		modelMap.addAttribute(new StockItem());
		
		return getManagedView();
	}
	
	@RequestMapping(value="/moveDown", method=RequestMethod.GET)
	public String moveDown(Integer index, HttpSession session, ModelMap modelMap) {
		
		List<StockItem> stockItems = (List<StockItem> ) session.getAttribute("stickies");
			
		if(stockItems == null) {
			return "sessionExpired";
		}
		if(index != stockItems.size() - 1) {
			Collections.swap(stockItems, index, index + 1);
		}
		modelMap.addAttribute(new StockItem());
		
		return getManagedView();
	}		
	
	@RequestMapping(value="/remove", method=RequestMethod.GET)
	public String remove(Integer index, HttpSession session, ModelMap modelMap) {
		
		List<StockItem> stockItems = (List<StockItem> ) session.getAttribute("stickies");
		
		if(stockItems == null) {
			return "sessionExpired";
		}
		
		int i = index; //Dowhhy??
		stockItems.remove(i);

		modelMap.addAttribute(new StockItem());
		
		return getManagedView();
	}
	
	protected abstract String getManagedView();
	
	protected abstract List<StockItem> getStickies(Long id);
}
