package org.bookmarks.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;


import org.bookmarks.controller.validation.SupplierOrderLineValidator;
import org.bookmarks.controller.bean.ReorderReviewBean;
import org.bookmarks.domain.CustomerOrderLine;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.SupplierDeliveryLine;
import org.bookmarks.domain.SupplierOrder;
import org.bookmarks.domain.SupplierOrderLine;
import org.bookmarks.domain.SupplierOrderLineStatus;
import org.bookmarks.domain.SupplierOrderStatus;
import org.bookmarks.domain.Level;
import org.bookmarks.service.CustomerOrderLineService;
import org.bookmarks.service.Service;
import org.bookmarks.service.StockItemService;
import org.bookmarks.service.SupplierOrderLineService;
import org.bookmarks.service.SupplierOrderService;
import org.bookmarks.service.StaticDataService;
import org.bookmarks.ui.comparator.GardnersDeliveryComparator;
import org.bookmarks.ui.comparator.SupplierOrderComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.apache.commons.lang3.StringUtils;

@Controller
@RequestMapping("/supplierOrder")
public class SupplierOrderController extends AbstractBookmarksController {
	@Autowired
	private SupplierOrderLineValidator supplierOrderLineValidator;
	
	@Autowired
	private StaticDataService staticDataService;
	
	@Autowired
	private CustomerOrderLineService customerOrderLineService;	

	@Autowired
	private StockItemService stockItemService;

	@Autowired
	private SupplierOrderService supplierOrderService;
	
	@Autowired
	private SupplierOrderLineService supplierOrderLineService;

	/**
	 * Create a map out of supplierorderlines so they can be selected and edited,
	 * add to session
	 * @param id
	 * @param session
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/edit", method=RequestMethod.GET)
	public String edit(Long id, HttpSession session, ModelMap modelMap) {
		SupplierOrder supplierOrder = supplierOrderService.get(id);

		Map<Long, SupplierOrderLine> map = new HashMap<Long, SupplierOrderLine>();
		for(SupplierOrderLine supplierOrderLine : supplierOrder.getSupplierOrderLines()) {
			map.put(supplierOrderLine.getId(), supplierOrderLine);
		}

		session.setAttribute("supplierOrderLineMap", map);
		session.setAttribute("supplierOrder", supplierOrder);

		fillModel(supplierOrder, modelMap, session);

		return "editSupplierOrder";
	}

	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public String edit(@Valid SupplierOrder supplierOrder, BindingResult bindingResult, HttpSession session, ModelMap modelMap) {
		if(bindingResult.hasErrors()) {
//			modelMap.addAttribute(SupplierOrderStatus.values());
			modelMap.addAttribute(supplierOrder);
			modelMap.addAttribute(supplierOrder.getSupplierOrderLines());
			return "editCustomerOrderLine";
		}

		supplierOrderService.update(supplierOrder);

		fillModel(supplierOrder, modelMap, session);

		return "viewSupplierOrder";
	}
	
	/**
	 * From searchCustomerOrderLines.jsp when out of stock is status, opened in new window, allows copy and paste of isbns
	 * @param id
	 * @param flow
	 * @param session
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/showText", method=RequestMethod.GET)
	public String showText(Long id, String flow, HttpSession session, ModelMap modelMap) {
		SupplierOrder supplierOrder = supplierOrderService.get(id);
		StringBuffer b = new StringBuffer();
		int rows = 2;
		b.append("ISBN\t\tTITLE\t\t\t\t\t\tQuantity\n");
		for(SupplierOrderLine sol : supplierOrder.getSupplierOrderLines()) {
			StockItem stockItem = sol.getStockItem();
			if(sol.getSupplierOrderLineStatus() == SupplierOrderLineStatus.ON_HOLD) continue;
			
			//Pad or strip title to fit 40 length
			String title = stockItem.getTitle().trim();
			System.out.println("1" + title.length());
			if(title.length() > 40) {
				title = title.substring(0, 40);
			} else {
				title = StringUtils.rightPad(title, 40 - title.length());
			}
			System.out.println("2" + title.length());
			b.append(stockItem.getIsbn() + "\t" + title + "\t\t" + sol.getAmount() + "\n");
			rows++;
		}
		
		modelMap.addAttribute("someText", b.toString());
		modelMap.addAttribute("rows", rows);
		
		return "showText";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value="/sendToSupplier", method=RequestMethod.POST)
	public String sendToSupplier(@Valid SupplierOrder supplierOrder, BindingResult bindingResult, HttpSession session, ModelMap modelMap) {
		Map<Long, SupplierOrderLine> map = (Map<Long, SupplierOrderLine>) session.getAttribute("supplierOrderLineMap");
		supplierOrder.setSupplierOrderLines(map.values());

		//supplierOrderLineValidator.validate(supplierOrder, bindingResult);
		if(bindingResult.hasErrors()) {
			modelMap.addAttribute(SupplierOrderStatus.values());
			modelMap.addAttribute(supplierOrder);
			return "editSupplierOrder";
		}

		supplierOrderService.sendToSupplier(supplierOrder);

		session.removeAttribute("supplierOrder");
		modelMap.addAttribute(supplierOrder);

		return "viewSupplierOrder";
	}

	@RequestMapping(value="/view", method=RequestMethod.GET)
	public String view(Long id, ModelMap modelMap) {
		SupplierOrder supplierOrder = supplierOrderService.get(id);

		modelMap.addAttribute(supplierOrder);

		return "viewSupplierOrder";
	}

	@RequestMapping(value="/editSupplierOrderLine", method=RequestMethod.GET)
	public String editSupplierOrderLine(Long id, ModelMap modelMap, HttpSession session) {
		SupplierOrder supplierOrder = (SupplierOrder) session.getAttribute("supplierOrder");

		SupplierOrderLine supplierOrderLine = supplierOrder.getSupplierOrderLine(id);
		
		supplierOrderLine.setSupplier(supplierOrder.getSupplier());

		modelMap.addAttribute(supplierOrderLine);
		modelMap.addAttribute(supplierOrder);
		//modelMap.addAttribute(getSuppliersIncludingMarxismSuppliers());
//		modelMap.addAttribute("checkBox", true);
//		modelMap.addAttribute(SupplierOrderStatus.values());

		return "editSupplierOrder";
	}
	

	@RequestMapping(value="/markForHold", method=RequestMethod.GET)
	public String markForHold(Long id, ModelMap modelMap, HttpSession session) {
		SupplierOrder supplierOrder = (SupplierOrder) session.getAttribute("supplierOrder");

		SupplierOrderLine supplierOrderLine = supplierOrder.getSupplierOrderLine(id);
		
//		supplierOrderService.toggleOnHold(supplierOrderLine);
		supplierOrderLineService.toggleOnHold(supplierOrderLine);
		
		fillModel(supplierOrder, modelMap, session);
		

		return "editSupplierOrder";
	}	
	
	@RequestMapping(value="/markAllForHold", method=RequestMethod.GET)
	public String markAllForHold(ModelMap modelMap, HttpSession session) {
		SupplierOrder supplierOrder = (SupplierOrder) session.getAttribute("supplierOrder");
		
		supplierOrderService.markAllForHold(supplierOrder);
		
		fillModel(supplierOrder, modelMap, session);
		
		return "editSupplierOrder";
	}	
	
	@RequestMapping(value="/markAllForReadyToSend", method=RequestMethod.GET)
	public String markAllForReadyToSend(ModelMap modelMap, HttpSession session) {
		SupplierOrder supplierOrder = (SupplierOrder) session.getAttribute("supplierOrder");
		
		supplierOrderService.markAllForReadyToSend(supplierOrder);
		
		fillModel(supplierOrder, modelMap, session);
		
		return "editSupplierOrder";
	}	
	
	
	@RequestMapping(value="/markCustomerOrdersReadyToSend", method=RequestMethod.GET)
	public String markCustomerOrdersReadyToSend(ModelMap modelMap, HttpSession session) {
		SupplierOrder supplierOrder = (SupplierOrder) session.getAttribute("supplierOrder");
		
		supplierOrderService.markCustomerOrdersReadyToSend(supplierOrder);
		
		fillModel(supplierOrder, modelMap, session);
		
		return "editSupplierOrder";
	}		

	@SuppressWarnings("unchecked")
	//Remove session attribute from calling methods (refactor)
	//TO-DO why is getsuppliers here?
	private void fillModel(SupplierOrder supplierOrder, ModelMap modelMap, HttpSession session) {
		modelMap.addAttribute(supplierOrder);
		//Sort
		List<SupplierOrderLine> list = new ArrayList<SupplierOrderLine>(supplierOrder.getSupplierOrderLines());
		Collections.sort(list, new SupplierOrderComparator());
		supplierOrder.setSupplierOrderLines(list);
//		modelMap.addAttribute(list);
		
		modelMap.addAttribute(staticDataService.getSuppliers());
	}

	@RequestMapping(value="/deleteSupplierOrderLine", method=RequestMethod.GET)
	public String deleteSupplierOrderLine(Long id, ModelMap modelMap, HttpSession session) {
		SupplierOrder supplierOrder = (SupplierOrder) session.getAttribute("supplierOrder");

		supplierOrderService.removeSupplierOrderLine(id, supplierOrder);

		modelMap.addAttribute(supplierOrder);
		modelMap.addAttribute(supplierOrder.getSupplierOrderLines());

		return "editSupplierOrder";
	}

	@RequestMapping(value="/delete", method=RequestMethod.GET)
	public String delete(Long id, ModelMap modelMap, HttpServletRequest request, HttpSession session) {

		supplierOrderService.delete(new SupplierOrder(id));

		SupplierOrderSearchBean supplierOrderSearchBean = (SupplierOrderSearchBean) session.getAttribute("savedSearchBean");
		return search(supplierOrderSearchBean, request, session, modelMap);
	}

	/**
	 * From editSupplierOrderLine.jsp - from editSupplierOrder.jsp - from searchSupplierOrders.jsp
	 * @param id
	 * @param modelMap
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/editSupplierOrderLine", method=RequestMethod.POST)
	public String editSupplierOrderLine(SupplierOrderLine supplierOrderLine, BindingResult bindingResult, ModelMap modelMap, HttpSession session) {
		SupplierOrder supplierOrder = (SupplierOrder) session.getAttribute("supplierOrder");

		//Validate
		supplierOrderLineValidator.validate(supplierOrderLine, bindingResult);
		if(bindingResult.hasErrors()) {
			modelMap.addAttribute(supplierOrder);
			modelMap.addAttribute(supplierOrderLine);
			//modelMap.addAttribute(getSuppliersIncludingMarxismSuppliers());
		    return "editSupplierOrder";
		}
		supplierOrderService.editSupplierOrderLine(supplierOrder, supplierOrderLine);

//		modelMap.addAttribute(supplierOrder.getSupplierOrderLines());
		modelMap.addAttribute(supplierOrder);
		//modelMap.addAttribute(getSuppliersIncludingMarxismSuppliers());
		//Why?
		modelMap.remove("supplierOrderLine");
		fillStockSearchModel(session, modelMap);

		return "editSupplierOrder";
	}


	@RequestMapping(value="/displaySearch", method=RequestMethod.GET)
	public String displaySearch(HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		SupplierOrderSearchBean supplierOrderSearchBean = new SupplierOrderSearchBean();
		supplierOrderSearchBean.getSupplierOrder().setSupplierOrderStatus(SupplierOrderStatus.PENDING);
		return search(supplierOrderSearchBean, request, session, modelMap);
	}
	




	@RequestMapping(value="/search")
	public String search(SupplierOrderSearchBean supplierOrderSearchBean, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		setPaginationFromRequest(supplierOrderSearchBean, request);

		Collection<SupplierOrder> supplierOrders = supplierOrderService.search(supplierOrderSearchBean);

		modelMap.addAttribute(supplierOrders);
		//modelMap.addAttribute(getSuppliersIncludingMarxismSuppliers());
		modelMap.addAttribute(SupplierOrderStatus.values());
		modelMap.addAttribute(supplierOrderSearchBean);
		modelMap.addAttribute("searchResultCount", supplierOrderSearchBean.getSearchResultCount());

		//Save for return later
		session.setAttribute("savedSearchBean", supplierOrderSearchBean);
		return "searchSupplierOrders";
	}

	@RequestMapping(value="/searchFromSession", method=RequestMethod.GET)
	public String searchFromSession(HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		SupplierOrderSearchBean supplierOrderSearchBean = (SupplierOrderSearchBean) session.getAttribute("savedSearchBean");

		return search(supplierOrderSearchBean, request, session, modelMap);
	}

	/**
	 * Find if a supplier order is already waiting (not sent), if not create and persist one
	 *
	 * @param supplierSupplierOrderLine
	 * @param bindingResult
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/send", method=RequestMethod.POST)
	public String send(@Valid SupplierOrderLine supplierOrderLine, BindingResult bindingResult, String flow, HttpSession session, ModelMap modelMap) {
		//Validate
		Long marxismAmount = supplierOrderLine.getMarxismAmount();
//		Long amount = supplierOrderLine.getAmount();
//		if(marxismAmount != null) {
//			if(marxismAmount < 0){
//				bindingResult.rejectValue("marxismAmount", "invalid", "Invalid marxism amount");
//			} else if(supplierOrderLine.getSupplier().getMarxismSupplier().getId() == null){
//				bindingResult.rejectValue("marxismAmount", "invalid", "No marxism account set up");
//			}
//		}
		if(bindingResult.hasErrors()) {
		    modelMap.addAttribute(supplierOrderLine);
		    return "displayCreateSupplierOrderLine";
		}
		
		supplierOrderService.sendToSupplier(supplierOrderLine);
		
		if(marxismAmount != null && marxismAmount > 0) {
			session.removeAttribute("supplierList"); //Just in case new marxism supplier has been created, better to have sendTo#
			//supplier return true or false depending of if Marxism supplier has been created
		}

		if(flow.equals("searchCustomerOrder")) {
			modelMap.addAttribute("closeWindowWithFormSubmit", "not null");
		} else {
			modelMap.addAttribute("closeWindowNoRefresh", "not null");
		}
		
		return "closeWindow";
	}
	/**
	 * Coming from stock search, create a new order line and get the supplier of the stock
	 * item publisher if it doesn't have a preferred one
	 * @param id
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/displayCreateSupplierOrderLine", method=RequestMethod.GET)
	public String displayCreateSupplierOrderLine(Long id, ModelMap modelMap, HttpSession session) {
		StockItem stockItem = stockItemService.get(id);
		SupplierOrderLine supplierOrderLine = null;
		//Get working at some point
//		SupplierOrder supplierOrder = getPendingFromRepository(marxismSupplier.getId()); 
		
//		supplierOrderLine = supplierOrderLineService.getPendingSupplierOrderLineFromStockItem(stockItem);
		if(supplierOrderLine == null) {
			supplierOrderLine = new SupplierOrderLine();
			supplierOrderLine.setAmount(1l); //Default to 1
			supplierOrderLine.setPriority(Level.LOW);
			supplierOrderLine.setStockItem(stockItem);
		}

		if(stockItem.getPreferredSupplier() == null) {
			supplierOrderLine.setSupplier(stockItem.getPublisher().getSupplier());
		} else {
			supplierOrderLine.setSupplier(stockItem.getPreferredSupplier());
		}

		modelMap.addAttribute(supplierOrderLine);
		modelMap.addAttribute(getSuppliers());
		modelMap.addAttribute(Level.values());
		modelMap.addAttribute("flow", "searchStockItems");

		return "displayCreateSupplierOrderLine";
	}
	/**
	 * Coming from customerorderline search, create a new order line and get the supplier of the stock
	 * item publisher if it doesn't have a preferred one
	 * @param id
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/displayCreateSupplierOrderLineForcustomerOrder", method=RequestMethod.GET)
	public String displayCreateSupplierOrderLineForCustomerOrder(Long customerOrderLineId, Long stockItemId, Long supplierId, Long amount, String flow, HttpServletRequest request, HttpSession session, ModelMap modelMap){
		CustomerOrderLine customerOrderLine = customerOrderLineService.get(customerOrderLineId); //A shit load of sql
		StockItem stockItem = customerOrderLine.getStockItem();
		 
		//To-do, get pending from repository
		SupplierOrderLine supplierOrderLine = new SupplierOrderLine();
		supplierOrderLine.setAmount(amount); 
		supplierOrderLine.setPriority(Level.HIGH);
		supplierOrderLine.setStockItem(stockItem);
		supplierOrderLine.setCustomerOrderLine(customerOrderLine);
		
		if(stockItem.getPreferredSupplier() == null) {
			supplierOrderLine.setSupplier(stockItem.getPublisher().getSupplier());
		} else {
			supplierOrderLine.setSupplier(stockItem.getPreferredSupplier());
		}
		
		modelMap.addAttribute(supplierOrderLine);
		modelMap.addAttribute(getSuppliers());
		modelMap.addAttribute(Level.values());
		modelMap.addAttribute("flow", "searchCustomerOrder");
		
		return "displayCreateSupplierOrderLine";
	}
	/**
	 * Coming from customerorderline search
	 */
	@RequestMapping(value="/showSupplierOrderForCustomerOrder", method=RequestMethod.GET)
	public String showSupplierOrderForCustomerOrder(Long customerOrderLineId, String flow, HttpServletRequest request, HttpSession session, ModelMap modelMap){
		SupplierOrderSearchBean supplierOrderSearchBean = new SupplierOrderSearchBean(customerOrderLineId);
		return search(supplierOrderSearchBean, request, session, modelMap);
	}
	
	@Override
	public Service getService() {
		return supplierOrderService;
	}

}
