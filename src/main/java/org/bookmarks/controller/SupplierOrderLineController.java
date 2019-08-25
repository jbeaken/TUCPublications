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
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrderLineStatus;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.SupplierDeliveryLine;
import org.bookmarks.domain.SupplierOrderLine;
import org.bookmarks.domain.SupplierOrder;
import org.bookmarks.domain.SupplierOrderLineStatus;
import org.bookmarks.domain.Level;
import org.bookmarks.domain.SupplierOrderLineType;
import org.springframework.validation.ObjectError;
import org.bookmarks.service.CustomerOrderLineService;
import org.bookmarks.service.Service;
import org.bookmarks.service.StockItemService;
import org.bookmarks.service.SupplierOrderLineService;
import org.bookmarks.service.SupplierOrderService;
import org.bookmarks.service.CustomerService;
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
@RequestMapping("/supplierOrderLine")
public class SupplierOrderLineController extends AbstractBookmarksController {
	@Autowired
	private SupplierOrderLineValidator supplierOrderLineValidator;
	
	@Autowired
	private StaticDataService staticDataService;
	
	@Autowired
	private CustomerOrderLineService customerOrderLineService;	

	@Autowired
	private SupplierOrderLineService supplierOrderLineService;

	@Autowired
	private StockItemService stockItemService;	

	@Autowired
	private CustomerService customerService;

	@RequestMapping(value="/sendAllToSupplier", method=RequestMethod.GET)
	public String sendAllToSupplier(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		SupplierOrderLineSearchBean supplierOrderLineSearchBean = (SupplierOrderLineSearchBean) session.getAttribute("supplierOrderLineSearchBean");
		supplierOrderLineService.sendAllToSupplier(supplierOrderLineSearchBean);
		
		return searchFromSession(request, session, modelMap);
	}	

	@RequestMapping(value="/sendToSupplier", method=RequestMethod.GET)
	public String sendToSupplier(Long id, String flow, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		SupplierOrderLine supplierOrderLine = supplierOrderLineService.get(id);

		supplierOrderLineService.sendToSupplier(supplierOrderLine);
		
		return searchFromSession(request, session, modelMap);
	}	

	@RequestMapping(value="/copyISBNs", method=RequestMethod.GET)
	public String copyISBNs(HttpSession session, ModelMap modelMap) {
		SupplierOrderLineSearchBean supplierOrderLineSearchBean = (SupplierOrderLineSearchBean) session.getAttribute("supplierOrderLineSearchBean");
		Collection<SupplierOrderLine> supplierOrderLines = supplierOrderLineService.search(supplierOrderLineSearchBean);
		
		int rows = 0;
		StringBuffer b = new StringBuffer();
		for(SupplierOrderLine sol : supplierOrderLines) {
			StockItem stockItem = sol.getStockItem();
			b.append(stockItem.getIsbn() + "\n");
			rows++;
		}
		modelMap.addAttribute("someText", b.toString());
		modelMap.addAttribute("rows", rows);
		
		return "showText";
	}	

	@RequestMapping(value="/createTable", method=RequestMethod.GET)
	public String createTable(HttpSession session, ModelMap modelMap) {
		SupplierOrderLineSearchBean supplierOrderLineSearchBean = (SupplierOrderLineSearchBean) session.getAttribute("supplierOrderLineSearchBean");
		Collection<SupplierOrderLine> supplierOrderLines = supplierOrderLineService.search(supplierOrderLineSearchBean);
		
		int rows = 0;
		StringBuffer b = new StringBuffer();
		for(SupplierOrderLine sol : supplierOrderLines) {
			StockItem stockItem = sol.getStockItem();
			b.append(stockItem.getIsbn() + "\t");
			b.append(stockItem.getTitle() + "\t");
			b.append(sol.getAmount() + "\n");
			rows++;
		}
		modelMap.addAttribute("someText", b.toString());
		modelMap.addAttribute("rows", rows);
		
		return "showText";
	}		
	
	@RequestMapping(value="/editCustomerOrderLine") 
	public String editCustomerOrderLine(Long id, ModelMap modelMap){
		SupplierOrderLine sol = supplierOrderLineService.get(id);
		if(sol.getCustomerOrderLine().getId() == null) {
			//This customer order line has been deleted, doesn't exist
			
			
		}
		CustomerOrderLine col = customerOrderLineService.get(sol.getCustomerOrderLine().getId());
		Customer customer = customerService.get(col.getCustomer().getId());
		col.setCustomer(customer);
		modelMap.addAttribute(col);
		modelMap.addAttribute(customer);
		modelMap.addAttribute(sol.getStockItem());
		return "editCustomerOrderLine";
	}

	
	
	/**
	 * Coming from customerorderline search, create a new order line and get the supplier of the stock
	 * stock item publisher or the preferered supplier if it exists.
	 */
	@RequestMapping(value="/displayCreateForCustomerOrder", method=RequestMethod.GET)
	public String displayCreateForCustomerOrder(Long customerOrderLineId, Long stockItemId, Long supplierId, Long amount, String flow, HttpServletRequest request, HttpSession session, ModelMap modelMap){
		CustomerOrderLine customerOrderLine = customerOrderLineService.get(customerOrderLineId);
		
		//Does a supplierOrderLine exist for this customer order line already?
		SupplierOrderLine supplierOrderLine = supplierOrderLineService.getByCustomerOrderLineId(customerOrderLineId);
		if(supplierOrderLine == null) {
			//No, so create new one
			supplierOrderLine = supplierOrderLineService.createForCustomerOrder(customerOrderLine);
		}
		
		supplierOrderLine.setAmount(amount);
		
		modelMap.addAttribute(supplierOrderLine);
		modelMap.addAttribute(getSuppliers());
		modelMap.addAttribute(Level.values());
		modelMap.addAttribute("flow", "searchCustomerOrder");
		
		return "displayCreateSupplierOrderLine";
	}	
	
	/**
	 * Coming from searchStockItems.jsp, create a new order line and get the supplier of the stock
	 * item publisher if it doesn't have a preferred one
	 * @param id
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/displayCreate", method=RequestMethod.GET)
	public String displayCreate(Long id, ModelMap modelMap, HttpSession session) {
		StockItem stockItem = stockItemService.get(id);
		SupplierOrderLine supplierOrderLine = supplierOrderLineService.getByStockItemId(id);
		
		if(supplierOrderLine == null) {
			supplierOrderLine = new SupplierOrderLine();
			supplierOrderLine.setAmount(1l); //Default to 1
			supplierOrderLine.setStockItem(stockItem);
			supplierOrderLine.setType(SupplierOrderLineType.USER);
			//Get the supplier, use preferred if necessary
			stockItem = stockItemService.get(stockItem.getId()); //Get stockitem's details
			supplierOrderLineService.setSupplier(supplierOrderLine, stockItem);
		}

		modelMap.addAttribute(supplierOrderLine);
		modelMap.addAttribute(getSuppliers());
		modelMap.addAttribute(Level.values());
		modelMap.addAttribute("flow", "searchStockItems");

		return "displayCreateSupplierOrderLine";
	}	
	/**
	 * From
	 *
	 * @param supplierSupplierOrderLine
	 * @param bindingResult
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/create", method=RequestMethod.POST)
	public String create(@Valid SupplierOrderLine supplierOrderLine, BindingResult bindingResult, String flow, HttpSession session, ModelMap modelMap) {
		if(bindingResult.hasErrors()) {
			List<ObjectError> errors = bindingResult.getAllErrors();
			for(ObjectError error : errors) {
				System.out.println(error.toString());
			}
		    modelMap.addAttribute(supplierOrderLine);
			modelMap.addAttribute(getSuppliers());
			modelMap.addAttribute(Level.values());
			modelMap.addAttribute("flow", "searchStockItems");		    
		    return "displayCreateSupplierOrderLine";
		}
		
		supplierOrderLineService.create(supplierOrderLine);

		if(flow.equals("searchCustomerOrder")) {
			modelMap.addAttribute("closeWindowWithFormSubmit", "not null");
		} else {
			modelMap.addAttribute("closeWindowNoRefresh", "not null");
		}
		
		return "closeWindow";
	}

	private void fillModel(SupplierOrderLine supplierOrderLine, ModelMap modelMap, HttpSession session) {
		modelMap.addAttribute(supplierOrderLine);
		modelMap.addAttribute(staticDataService.getSuppliers());
	}

	@RequestMapping(value="/displaySearch", method=RequestMethod.GET)
	public String displaySearch(HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		SupplierOrderLineSearchBean supplierOrderLineSearchBean = new SupplierOrderLineSearchBean();
		supplierOrderLineSearchBean.getSupplierOrderLine().setSupplierOrderLineStatus(SupplierOrderLineStatus.READY_TO_SEND);
		return search(supplierOrderLineSearchBean, request, session, modelMap);
	}
	
	@RequestMapping(value="/search")
	public String search(SupplierOrderLineSearchBean supplierOrderLineSearchBean, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		
		if(supplierOrderLineSearchBean.isFromSession() == false) { //Pagination etc. already set
			setPaginationFromRequest(supplierOrderLineSearchBean, request);
		}		

		Collection<SupplierOrderLine> supplierOrders = supplierOrderLineService.search(supplierOrderLineSearchBean);

		modelMap.addAttribute(supplierOrders);
		modelMap.addAttribute(getSuppliers(session));
		modelMap.addAttribute(SupplierOrderLineStatus.values());
		modelMap.addAttribute(Level.values());
		modelMap.addAttribute(SupplierOrderLineType.values());
		modelMap.addAttribute(supplierOrderLineSearchBean);
		modelMap.addAttribute("searchResultCount", supplierOrderLineSearchBean.getSearchResultCount());

		//Save for return later
		session.setAttribute("supplierOrderLineSearchBean", supplierOrderLineSearchBean);
		return "searchSupplierOrderLines";
	}
	
	@RequestMapping(value="/viewSupplierOrderLine")
	public String viewSupplierOrderLine(CustomerOrderLine customerOrderLine, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		
		SupplierOrderLineSearchBean supplierOrderLineSearchBean = new SupplierOrderLineSearchBean(customerOrderLine);
		
		return search(supplierOrderLineSearchBean, request, session, modelMap);
	}	

	@RequestMapping(value="/searchFromSession", method=RequestMethod.GET)
	public String searchFromSession(HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		
		SupplierOrderLineSearchBean supplierOrderLineSearchBean = (SupplierOrderLineSearchBean) session.getAttribute("supplierOrderLineSearchBean");

		return search(supplierOrderLineSearchBean, request, session, modelMap);
	}

	@RequestMapping(value="/edit", method=RequestMethod.GET)
	public String edit(Long id, ModelMap modelMap, HttpSession session) {
		SupplierOrderLine supplierOrderLine = supplierOrderLineService.get(id);

		modelMap.addAttribute(supplierOrderLine);
		modelMap.addAttribute(getSuppliers());
		modelMap.addAttribute(Level.values());
		modelMap.addAttribute("flow", "searchSupplierOrderLines");

		return "editSupplierOrderLine";
	}
	
	/**
	 * From editSupplierOrderLine.jsp
	 *
	 * @param supplierSupplierOrderLine
	 * @param bindingResult
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public String edit(@Valid SupplierOrderLine supplierOrderLine, BindingResult bindingResult, String flow, HttpSession session, ModelMap modelMap) {
		if(bindingResult.hasErrors()) {
		    modelMap.addAttribute(supplierOrderLine);
			modelMap.addAttribute(getSuppliers());
			modelMap.addAttribute(Level.values());
			modelMap.addAttribute("flow", flow);		    
		    return "displayCreateSupplierOrderLine";
		}
		
		supplierOrderLineService.update(supplierOrderLine);

		if(flow.equals("searchSupplierOrderLines")) {
			modelMap.addAttribute("closeWindowWithFormSubmit", "not null");
		} else {
			modelMap.addAttribute("closeWindowNoRefresh", "not null");
		}
		
		return "closeWindow";
	}	
	

	@RequestMapping(value="/toggleOnHold", method=RequestMethod.GET)
	public String toggleOnHold(Long id, HttpServletRequest request, ModelMap modelMap, HttpSession session) {
		SupplierOrderLine supplierOrderLine = supplierOrderLineService.get(id);
		
		supplierOrderLineService.toggleOnHold(supplierOrderLine);

		return searchFromSession(request, session, modelMap);
	}	
	
	@RequestMapping(value="/putAllOnHold", method=RequestMethod.GET)
	public String putAllOnHold(ModelMap modelMap, HttpServletRequest request, HttpSession session) {
		SupplierOrderLineSearchBean supplierOrderLineSearchBean = (SupplierOrderLineSearchBean) session.getAttribute("supplierOrderLineSearchBean");

		supplierOrderLineService.putAllOnHold(supplierOrderLineSearchBean);
		
		return searchFromSession(request, session, modelMap);
	}	
	
	@RequestMapping(value="/takeAllOffHold", method=RequestMethod.GET)
	public String takeAllOffHold(ModelMap modelMap, HttpServletRequest request, HttpSession session) {
		SupplierOrderLineSearchBean supplierOrderLineSearchBean = (SupplierOrderLineSearchBean) session.getAttribute("supplierOrderLineSearchBean");
		supplierOrderLineService.takeAllOffHold(supplierOrderLineSearchBean);
		
		return searchFromSession(request, session, modelMap);
	}	
	
	@RequestMapping(value="/putCustomerOrdersOnHold", method=RequestMethod.GET)
	public String putCustomerOrdersOnHold(ModelMap modelMap, HttpServletRequest request, HttpSession session) {
		SupplierOrderLineSearchBean supplierOrderLineSearchBean = (SupplierOrderLineSearchBean) session.getAttribute("supplierOrderLineSearchBean");
		supplierOrderLineService.putCustomerOrdersOnHold(supplierOrderLineSearchBean);
		
		return searchFromSession(request, session, modelMap);
	}	

	@RequestMapping(value="/takeCustomerOrdersOffHold", method=RequestMethod.GET)
	public String takeCustomerOrdersOffHold(ModelMap modelMap, HttpServletRequest request, HttpSession session) {
		SupplierOrderLineSearchBean supplierOrderLineSearchBean = (SupplierOrderLineSearchBean) session.getAttribute("supplierOrderLineSearchBean");
		supplierOrderLineService.takeCustomerOrdersOffHold(supplierOrderLineSearchBean);
		
		return searchFromSession(request, session, modelMap);
	}		

	@RequestMapping(value="/delete", method=RequestMethod.GET)
	public String delete(Long id, ModelMap modelMap, HttpServletRequest request, HttpSession session) {
		SupplierOrderLine sol = supplierOrderLineService.get(id);
		if(sol.getType() == SupplierOrderLineType.CUSTOMER_ORDER) {
			//Special case, remove it from the customer order before deleting
			//TO-DO what about the col status, should it change back to pending??
			CustomerOrderLine col = sol.getCustomerOrderLine();
			if(col != null) { //Why is this null TO-DO
				if(col.getStatus() == CustomerOrderLineStatus.PENDING_ON_ORDER || col.getStatus() == CustomerOrderLineStatus.ON_ORDER) {
					col.setStatus(CustomerOrderLineStatus.OUT_OF_STOCK);
				}
				col.setSupplierOrderLine(null);
				customerOrderLineService.update(col);
			}
		}
		supplierOrderLineService.delete(sol);
		return searchFromSession(request, session, modelMap);
	}

	@Override
	public Service getService() {
		return supplierOrderLineService;
	}

}
