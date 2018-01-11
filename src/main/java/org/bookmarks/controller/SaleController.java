package org.bookmarks.controller;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.bookmarks.controller.bean.SaleReportBean;
import org.bookmarks.domain.Event;
import org.bookmarks.domain.Sale;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.StockItemType;
import org.bookmarks.service.SaleService;
import org.bookmarks.service.StockItemService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping(value="/sale")
public class SaleController extends AbstractBookmarksController {

	@Autowired
	private StockItemService stockItemService;

	@Autowired
	private StockItemController stockItemController;

	@Autowired
	private SaleService saleService;

	private Logger logger = LoggerFactory.getLogger(SaleController.class);

	public void setStockItemService(StockItemService stockItemService) {
		this.stockItemService = stockItemService;
	}

	@RequestMapping(value="/search")
	public String search(@Valid SaleReportBean saleSearchBean, BindingResult bindingResult, HttpServletRequest request, ModelMap modelMap) {
		if(bindingResult.hasErrors()) {
			modelMap.addAttribute(saleSearchBean);
			return "searchSales";
		}

		setPaginationFromRequest(saleSearchBean, request);

		Collection<Sale> sales = saleService.search(saleSearchBean);

		modelMap.addAttribute(sales);
		modelMap.addAttribute("searchResultCount", saleSearchBean.getSearchResultCount());

		return "searchSales";
	}



//	@RequestMapping(value = "/sell", method=RequestMethod.POST)
//	public String sell(StockItemSearchBean stockItemSearchBean, HttpServletRequest request, ModelMap modelMap, HttpSession session) {
//		String errorMessage = stockItemSearchBean.checkValidity();
//		if(errorMessage != null) {
//			modelMap.addAttribute("message", errorMessage);
//			fillStockSearchModel(session, modelMap);
//			return "searchStockItems";
//		}
//
//		setPaginationFromRequest(stockItemSearchBean, request);
//
//		convertToISBN13(stockItemSearchBean.getStockItem());
//
//		Collection<StockItem> stockItems = stockItemService.search(stockItemSearchBean);
//		modelMap.addAttribute("searchResultCount", stockItemSearchBean.getSearchResultCount());
//
//		//Sell item
//		Map<Long, Sale> saleMap = (Map<Long, Sale>) session.getAttribute("saleMap");
//
//		if(stockItems.size() == 1) {
//			//Sell
//			StockItem stockItem = stockItems.iterator().next();
//			Sale sale = sellSingleStockItem(stockItem, null, modelMap, saleMap);
//
//			//Reset search
//			stockItemSearchBean.reset();
//
//			fillSaleModel(saleMap, session, modelMap);
//			return "sellStockItem";
//		} else if(stockItems.size() > 1) {
//			//Too many stock items, display to user for selection
//			modelMap.addAttribute(stockItems);
//		} else {
//			//ISBN cannot be found in database
//			//Don't reset isbn
//			//In future offer to create new stock record if ISBN is 13 or 10??
//			modelMap.addAttribute("message", "ISBN " + stockItemSearchBean.getStockItem().getIsbn() + " cannot be found in database");
//		}
//
//		fillSaleModel(saleMap, session, modelMap);
//		return "sellStockItem";
//	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/sellAndGoByISBN", method=RequestMethod.GET)
	public String sellAndGoByISBN(String isbn, boolean stayInSearch, HttpServletRequest request, ModelMap modelMap, HttpSession session) {
		StockItemSearchBean stockItemSearchBean = new StockItemSearchBean(isbn);
		return sellByISBN(stockItemSearchBean, request, modelMap, session);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/sellAndStayByISBN", method=RequestMethod.GET)
	public String sellAndStayByISBN(String isbn, boolean stayInSearch, HttpServletRequest request, ModelMap modelMap, HttpSession session) {
		sellAndGoByISBN(isbn, stayInSearch, request, modelMap, session);
		return stockItemController.searchFromSession(session, request, modelMap);
	}

	/**
	 * Used from sellStockItem.jsp
	 *
	 * @param stockItemSearchBean
	 * @param request
	 * @param modelMap
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/sellByISBN", method=RequestMethod.POST)
	public String sellByISBN(StockItemSearchBean stockItemSearchBean, HttpServletRequest request, ModelMap modelMap, HttpSession session) {
		//Sell item
		Map<Long, Sale> saleMap = (Map<Long, Sale>) session.getAttribute("saleMap");
		BigDecimal totalPrice = (BigDecimal) session.getAttribute("totalPrice");

		//This may be from search stockitem screen so above may be null
		if(saleMap == null) {
			saleMap = new HashMap<Long, Sale>();
			totalPrice = new BigDecimal(0);
			session.setAttribute("saleMap", saleMap);
			session.setAttribute("totalPrice", totalPrice);
		}


		String errorMessage = stockItemSearchBean.checkValidityForISBN();

		if(errorMessage != null) {
			modelMap.addAttribute("message", errorMessage);
			session.setAttribute("totalPrice", totalPrice);
			modelMap.addAttribute(saleMap.values());
			return "sellStockItem";
		}
		Long isbnAsNumber = null;
		try {
			isbnAsNumber = Long.parseLong(stockItemSearchBean.getStockItem().getIsbn().trim());
		} catch(NumberFormatException e) {
			modelMap.addAttribute("message", "Invalid isbn!");
			session.setAttribute("totalPrice", totalPrice);
			modelMap.addAttribute(saleMap.values());
			modelMap.addAttribute(stockItemSearchBean);
			return "sellStockItem";
		}
		StockItem stockItem = stockItemService.getByISBNAsNumber(isbnAsNumber);
		if(stockItem == null) {
			modelMap.addAttribute("message", "Cannot find isbn in database!");
			session.setAttribute("totalPrice", totalPrice);
			session.removeAttribute("lastSale");
			modelMap.addAttribute(saleMap.values());
			modelMap.addAttribute(stockItemSearchBean);
			return "sellStockItem";
		}
		Event event = (Event) session.getAttribute("event");
		Sale sale = sellSingleStockItem(stockItem, event, modelMap, saleMap);

		fillSaleModel(saleMap, session, modelMap);

		session.setAttribute("lastSale", sale);
		modelMap.addAttribute(new StockItemSearchBean());

		return "redirect:sell";
	}

	/**
	 * Called from searchStockItems.jsp
	 */
	@RequestMapping(value = "/sellSingleStockItem", method=RequestMethod.GET)
	public String sellSingleStockItem(Long id, ModelMap modelMap, HttpSession session) {
		Map<Long, Sale> saleMap = (Map<Long, Sale>) session.getAttribute("saleMap");

		Event event = (Event) session.getAttribute("event");
		StockItem stockItem = stockItemService.get(id);
		Sale sale = sellSingleStockItem(stockItem, event, modelMap, saleMap);

		fillSaleModel(saleMap, session, modelMap);

		return "sellStockItem";
	}

	/**
	 * Called from sellStockItem.jsp
	 */
	@RequestMapping(value = "/sellExtra/{id}", method=RequestMethod.GET)
	public String sellExtra(@PathVariable("id") Long id, ModelMap modelMap, HttpSession session) {
		Map<Long, Sale> saleMap = (Map<Long, Sale>) session.getAttribute("saleMap");

		Event event = (Event) session.getAttribute("event");
		StockItem stockItem = stockItemService.get(id);
		Sale sale = sellSingleStockItem(stockItem, event, modelMap, saleMap);

		fillSaleModel(saleMap, session, modelMap);

		modelMap.addAttribute(new StockItemSearchBean());
		session.setAttribute("lastSale", sale);
		modelMap.addAttribute(new StockItemSearchBean());

		return "redirect:/sale/sell";
	}

	private Sale sellSingleStockItem(StockItem stockItem, Event event, ModelMap modelMap, Map<Long, Sale> saleMap) {

		//Sell
		Sale sale = saleService.sell(stockItem, event);

		//Put into sale map
		saleMap.put(sale.getId(), sale);

		logger.info( "Sold - " + sale.getStockItem().getSellPrice() + " : " + sale.getStockItem().getId() + " : " + sale.getStockItem().getIsbn() + " : " + sale.getStockItem().getTitle() );

		return sale;
	}

	@RequestMapping(value = "/delete", method=RequestMethod.GET)
	public String delete(Long id, ModelMap modelMap, HttpSession session) {
		Map<Long, Sale> saleMap = (Map<Long, Sale>) session.getAttribute("saleMap");

		//Get sale details
		Sale sale = saleService.get(id);

		//Delete item
		saleService.delete(sale);

		//Remove sold item from session list of sales for display
		saleMap.remove(sale.getId());

		//Put into model
		fillSaleModel(saleMap, session, modelMap);
		modelMap.addAttribute(new StockItemSearchBean());
		return "sellStockItem";
	}

	@RequestMapping(value = "/edit", method=RequestMethod.GET)
	public String edit(Long id, ModelMap modelMap, HttpSession session) {

		//May be not from sellStockItem.jsp, so can't rely on presence of salemap so get sale details from repository
		Sale sale = saleService.get(id);
		sale.setOriginalQuantity(sale.getQuantity());

		modelMap.addAttribute(sale);
		return "editSale";
	}

	@RequestMapping(value = "/sellSecondHand", method=RequestMethod.GET)
	public String sellSecondHand(ModelMap modelMap, HttpSession session) {
		modelMap.addAttribute(getSecondHandSale());
		return "sellSecondHand";
	}

	@RequestMapping(value = "/sellSecondHand", method=RequestMethod.POST)
	public String sellSecondHand(@Valid Sale sale, BindingResult bindingResult, ModelMap modelMap, HttpSession session) {
		if(bindingResult.hasErrors()) {
			modelMap.addAttribute(sale);
			return "sellSecondHand";
		}
		Map<Long, Sale> saleMap = (Map<Long, Sale>) session.getAttribute("saleMap");

		if(sale.getEvent().getId() == null) {
			sale.setEvent(null); //Why do I have to do this
		}

		//Delete item
		saleService.save(sale);

		//In case session has become invalid
		if(saleMap == null) {
			saleMap = new HashMap<Long, Sale>();
		}

		saleMap.put(sale.getId(), sale);

		//Put into model
		fillSaleModel(saleMap, session, modelMap, new StockItemSearchBean());

		return "sellStockItem";
	}



	@RequestMapping(value = "/edit", method=RequestMethod.POST)
	public String edit(Sale sale, ModelMap modelMap, HttpSession session) {
		Map<Long, Sale> saleMap = (Map<Long, Sale>) session.getAttribute("saleMap");

		//Update
		saleService.update(sale);

		saleMap.put(sale.getId(), sale);

		//Put into model
		fillSaleModel(saleMap, session, modelMap, new StockItemSearchBean());

		modelMap.addAttribute("lastSale", sale);
		return "sellStockItem";
	}

	/**
	 * From header.jsp, don't reset the saleMap
	 * @param modelMap
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/sell", method=RequestMethod.GET)
	public String displaySellStockItem(ModelMap modelMap, HttpSession session) {

		Map<Long, Sale> saleMap = (Map<Long, Sale>) session.getAttribute("saleMap");

		if(saleMap == null) {
			saleMap = new HashMap<Long, Sale>();
			session.setAttribute("saleMap", saleMap);
		}

		//Put into model
		fillSaleModel(saleMap, session, modelMap, new StockItemSearchBean());
		return "sellStockItem";
	}

	/**
	 * From header.jsp, don't reset the saleMap
	 * @param modelMap
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/displayExtras", method=RequestMethod.GET)
	public String displayExtras(ModelMap modelMap, HttpSession session) {
//		Map<String, List<StockItem>> extrasMap = stockItemService.getExtras();
		Collection<StockItem> extras = stockItemService.getExtras();
		modelMap.addAttribute("extras", extras);
		return "displayExtras";
	}

	/**
	 * From header.jsp, reset the saleMap
	 * @param modelMap
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/reset", method=RequestMethod.GET)
	public String reset(ModelMap modelMap, HttpSession session) {
		Map<Long, Sale> saleMap =  new HashMap<Long, Sale>();

		session.setAttribute("saleMap", saleMap);
		session.removeAttribute("lastSale");

		//Put into model
		fillSaleModel(saleMap, session, modelMap, new StockItemSearchBean());
		return "sellStockItem";
	}

	private void fillSaleModel(Map<Long, Sale> saleMap, HttpSession session, ModelMap modelMap, StockItemSearchBean stockItemSearchBean) {
		modelMap.addAttribute(stockItemSearchBean);
		fillSaleModel(saleMap, session, modelMap);
	}

	private void fillSaleModel(Map<Long, Sale> saleMap, HttpSession session, ModelMap modelMap) {
//		fillStockSearchModel(session, modelMap);
		List<Sale> list = new ArrayList<Sale>(saleMap.values());
		Collections.sort(list, (s1, s2) ->  s1.getCreationDate().compareTo(s2.getCreationDate()) );
		modelMap.addAttribute(list);
		BigDecimal total = new BigDecimal(0);
		for(Sale s : saleMap.values()) {
			total = total.add(s.getTotalPrice());
		}
		if (total.compareTo(BigDecimal.ZERO) != 0) modelMap.addAttribute("totalPrice", total);

		Collection<StockItem> extras = stockItemService.getExtras();
		modelMap.put("extras", extras);
	}

	@Override
	public org.bookmarks.service.Service getService() {
		return null;
	}
}
