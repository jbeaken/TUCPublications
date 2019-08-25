package org.bookmarks.controller;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.bookmarks.controller.StockItemSearchBean;
import org.bookmarks.domain.Event;
import org.bookmarks.domain.StockTakeLine;
import org.bookmarks.domain.StockItem;
import org.bookmarks.service.StockTakeLineService;
import org.bookmarks.service.StockItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping(value="/stockTakeLine")
public class StockTakeLineController extends AbstractBookmarksController {

	@Autowired
	private StockTakeLineService stockTakeLineService;

	@Autowired
	private StockItemService stockItemService;

	private Logger logger = LoggerFactory.getLogger(StockTakeLineController.class);


	@RequestMapping(value="/search")
	public String search(@ModelAttribute StockItemSearchBean stockItemSearchBean, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
	//TO-DO validate publisher as might not be an id from jquery chosen
		StockItem stockItem = stockItemSearchBean.getStockItem();

		Collection<StockTakeLine> stockTakeLines = new ArrayList<StockTakeLine>();
//		String errorMessage = stockItemSearchBean.checkValidity();  //Remove use stockItemValidator
		convertToISBN13(stockItem);
//		if(errorMessage != null) {
//			modelMap.addAttribute("message", errorMessage);
//			fillStockSearchModel(session, modelMap);
//			modelMap.addAttribute(stockTakeLines);
//			return "searchStockTakeLines";
//		}

		if(!stockItemSearchBean.isFromSession()) {
			setPaginationFromRequest(stockItemSearchBean, request);
		}
		stockTakeLines = stockTakeLineService.search(stockItemSearchBean);
		modelMap.addAttribute("searchResultCount", stockItemSearchBean.getSearchResultCount());
//		fillStockSearchModel(session, modelMap);
		modelMap.addAttribute(stockTakeLines);
		setPageSize(stockItemSearchBean, modelMap, stockTakeLines.size());
		stockItemSearchBean.isFromSession(false);

		session.setAttribute("stockItemSearchBean", stockItemSearchBean);

		return "searchStockTakeLines";
	}

	@RequestMapping(value="/init")
	public String init(HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		Map<Long, StockTakeLine> stockTakeMap = new HashMap<Long, StockTakeLine>();
		session.setAttribute("stockTakeMap", stockTakeMap);
		modelMap.addAttribute(new StockItemSearchBean());
		return "addStockTakeLine";
	}


	@RequestMapping(value="/displaySearch")
	public String displaySearch(HttpServletRequest request, ModelMap modelMap) {
		modelMap.addAttribute(new StockItemSearchBean());

		return "searchStockTakeItems";
	}
	/**
	 *
	 * From addStockTakeLine.jsp
	 *
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/addToStockTake", method=RequestMethod.POST)
	public String addToStockTake(StockItemSearchBean stockItemSearchBean, HttpServletRequest request, ModelMap modelMap, HttpSession session) {

		Map<Long, StockTakeLine> stockTakeMap = (Map<Long, StockTakeLine>) session.getAttribute("stockTakeMap");

		//This may be from search stockitem screen so above may be null
		if(stockTakeMap == null) {
			stockTakeMap = new HashMap<Long, StockTakeLine>();
			session.setAttribute("stockTakeMap", stockTakeMap);
		}

		String errorMessage = stockItemSearchBean.checkValidityForISBN();

		if(errorMessage != null) {
			modelMap.addAttribute("message", errorMessage);
			modelMap.addAttribute(stockTakeMap.values());
			return "addStockTakeLine";
		}

		Long isbnAsNumber = null;
		try {
			isbnAsNumber = Long.parseLong(stockItemSearchBean.getStockItem().getIsbn().trim());
		} catch(NumberFormatException e) {
			addError("Invalid ISBN", modelMap);
			modelMap.addAttribute(stockTakeMap.values());
			modelMap.addAttribute(stockItemSearchBean);
			return "addStockTakeLine";
		}

		StockItem stockItem = null;
		try {
			stockItem = stockItemService.getByISBNAsNumberForStockTake(isbnAsNumber);
		} catch(Exception e) {
			addError("ISBN not in database", modelMap);
			modelMap.addAttribute(stockTakeMap.values());
			modelMap.addAttribute(stockItemSearchBean);
			return "addStockTakeLine";
		}

		if(stockItem == null) {
			addError("Cannot find isbn in database", modelMap);
			modelMap.addAttribute(stockTakeMap.values());
			modelMap.addAttribute(stockItemSearchBean);
			return "addStockTakeLine";
		}

		//Is this a new stock record or an update?
		StockTakeLine stockTakeLine = stockTakeLineService.getByStockItemId(stockItem.getId());
		if(stockTakeLine == null) {
			//New
			stockTakeLine = new StockTakeLine(stockItem);
		} else {
			//Has previous record, sync creation date as this is used to sort list for display
			stockTakeLine.setQuantity(stockTakeLine.getQuantity() + 1);
			stockTakeLine.setCreationDate(new Date());
		}

		if(stockTakeMap.size() > 50) {
			List<StockTakeLine> list = new ArrayList<StockTakeLine>(stockTakeMap.values());

			Collections.sort(list, (s1, s2) -> s1.getCreationDate().compareTo(s2.getCreationDate()) );

			list = list.subList(0, 20);
			stockTakeMap = new HashMap<Long, StockTakeLine>();
			for(StockTakeLine line : list) {
				stockTakeMap.put(line.getId(), line);
			}
			session.setAttribute("stockTakeMap", stockTakeMap);
			//stockTakeMap.clear();
		}


		stockTakeLine.setDateOfUpdate(new Date());

		stockTakeLineService.saveOrUpdate(stockTakeLine);
		stockTakeMap.put(stockTakeLine.getId(), stockTakeLine);

		fillStockTakeLineModel(stockTakeMap, session, modelMap);

		modelMap.addAttribute("lastStockRecord", stockTakeLine);
		modelMap.addAttribute(new StockItemSearchBean());

		return "addStockTakeLine";
	}


	@RequestMapping(value = "/delete", method=RequestMethod.GET)
	public String delete(Long id, ModelMap modelMap, HttpSession session) {
		Map<Long, StockTakeLine> stockTakeMap = (Map<Long, StockTakeLine>) session.getAttribute("stockTakeMap");

		//Get stockRecord details
		StockTakeLine stockRecord = stockTakeLineService.get(id);

		//Delete item
		stockTakeLineService.delete(stockRecord);

		//Remove sold item from session list of stockRecords for display
		stockTakeMap.remove(stockRecord.getId());

		//Put into model
		fillStockTakeLineModel(stockTakeMap, session, modelMap);
		modelMap.addAttribute(new StockItemSearchBean());
		return "addStockTakeLine";
	}

	@RequestMapping(value = "/edit", method=RequestMethod.GET)
	public String edit(Long id, ModelMap modelMap, HttpSession session) {

		StockTakeLine stockTakeLine = stockTakeLineService.get(id);

		modelMap.addAttribute(stockTakeLine);
		return "editStockTakeLine";
	}

	@RequestMapping(value = "/increment", method=RequestMethod.GET)
	public String increment(Long id, ModelMap modelMap, HttpSession session) {

		StockTakeLine stockTakeLine = stockTakeLineService.get(id);

		stockTakeLine.setAmountToIncrement(null);
		modelMap.addAttribute(stockTakeLine);
		return "incrementStockTakeLine";
	}

	@RequestMapping(value = "/increment", method=RequestMethod.POST)
	public String increment(StockTakeLine stockTakeLine, ModelMap modelMap, HttpSession session) {
		Map<Long, StockTakeLine> stockTakeMap = (Map<Long, StockTakeLine>) session.getAttribute("stockTakeMap");

		StockTakeLine stockTakeLineToIncrement = stockTakeLineService.get(stockTakeLine.getId());

		stockTakeLineToIncrement.setQuantity(stockTakeLineToIncrement.getQuantity() + stockTakeLine.getAmountToIncrement());

		//Update
		stockTakeLineToIncrement.setDateOfUpdate(new Date());
		stockTakeLineService.update(stockTakeLineToIncrement);

		stockTakeMap.put(stockTakeLineToIncrement.getId(), stockTakeLineToIncrement);

		//Put into model
		fillStockRecordModel(stockTakeMap, session, modelMap, new StockItemSearchBean());

		modelMap.addAttribute("lastStockRecord", stockTakeLineToIncrement);
		return "addStockTakeLine";
	}

	@RequestMapping(value = "/edit", method=RequestMethod.POST)
	public String edit(StockTakeLine stockTakeLine, ModelMap modelMap, HttpSession session) {
		Map<Long, StockTakeLine> stockTakeMap = (Map<Long, StockTakeLine>) session.getAttribute("stockTakeMap");

		//Update
		stockTakeLineService.update(stockTakeLine);

		stockTakeMap.put(stockTakeLine.getId(), stockTakeLine);

		//Put into model
		fillStockRecordModel(stockTakeMap, session, modelMap, new StockItemSearchBean());

		modelMap.addAttribute("lastStockRecord", stockTakeLine);
		return "addStockTakeLine";
	}

	/**
	 * delete from StockTakeLine
	 * Can now start a new stocktake
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/reset", method=RequestMethod.GET)
	public String reset(ModelMap modelMap, HttpSession session) {

		stockTakeLineService.reset();

		Map<Long, StockTakeLine> stockTakeMap =  new HashMap<Long, StockTakeLine>();

		session.setAttribute("stockTakeMap", stockTakeMap);

		//Put into model
		fillStockRecordModel(stockTakeMap, session, modelMap, new StockItemSearchBean());

		addInfo("Stock take record is reset!", modelMap);

		return "addStockTakeLine";
	}

	@RequestMapping(value="/displaySearch", method=RequestMethod.GET)
	public String displaySearch(StockItemSearchBean stockItemSearchBean, HttpSession session, ModelMap modelMap) {
		fillStockSearchModel(session, modelMap);
		modelMap.addAttribute(stockItemSearchBean);
		return "searchStockTakeLines";
	}

	/**
	 * Stock take has been completed, update the stock record by
	 * reseting quantityInStock to 0 (commit = true) and then transfer from
	 * StockTakeLine.quantity to StockItem.quantityInStock
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/commit", method=RequestMethod.GET)
	public String commit(ModelMap modelMap, Boolean includeBookmarks, Boolean includeMerchandise, HttpSession session) {

		logger.info("About to commit stock take : includeBookmarks : {}, includeMerchandise : {}", includeBookmarks, includeMerchandise);

		stockTakeLineService.commit(true, includeBookmarks, includeMerchandise);

		logger.info("Stock update successful!!");

		session.removeAttribute("stockTakeMap");

		addInfo("Stock update successful!", modelMap);

		return "welcome";
	}

	private void fillStockRecordModel(Map<Long, StockTakeLine> stockTakeMap, HttpSession session, ModelMap modelMap, StockItemSearchBean stockItemSearchBean) {
		modelMap.addAttribute(stockItemSearchBean);
		fillStockTakeLineModel(stockTakeMap, session, modelMap);
	}

	private void fillStockTakeLineModel(Map<Long, StockTakeLine> stockTakeMap, HttpSession session, ModelMap modelMap) {

		List<StockTakeLine> list = new ArrayList<StockTakeLine>(stockTakeMap.values());

		Collections.sort(list, (s1, s2) -> {
			return s2.getDateOfUpdate().compareTo(s1.getDateOfUpdate());
		});

		modelMap.addAttribute(list);
	}

	@Override
	public org.bookmarks.service.Service getService() {
		return stockTakeLineService;
	}
}
