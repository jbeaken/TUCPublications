package org.bookmarks.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.bookmarks.controller.validation.SupplierOrderLineValidator;
import org.bookmarks.controller.bean.ReorderReviewBean;
import org.bookmarks.controller.bean.ReorderReviewStockItemBean;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.SupplierDeliveryLine;
import org.bookmarks.domain.SupplierOrder;
import org.bookmarks.domain.SupplierOrderLine;
import org.bookmarks.domain.SupplierOrderStatus;
import org.bookmarks.service.ReorderReviewService;
import org.bookmarks.service.SaleService;
import org.bookmarks.service.Service;
import org.bookmarks.service.StockItemService;
import org.bookmarks.service.SupplierOrderLineService;
import org.bookmarks.service.SupplierOrderService;
import org.bookmarks.ui.comparator.GardnersDeliveryComparator;
import org.bookmarks.ui.comparator.SupplierOrderComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/reorderReview")
public class ReorderReviewController extends AbstractBookmarksController {

	@Autowired
	private ReorderReviewService reorderReviewService;

	@Autowired
	private SupplierOrderLineValidator supplierOrderLineValidator;

	private Logger logger = LoggerFactory.getLogger(ReorderReviewController.class);

	/*
		Ends up at
		reorderReviewRepositoryImpl.getReorderReview()
	*/
	@RequestMapping(value="/init", method=RequestMethod.GET)
	public String init(HttpSession session, ModelMap modelMap) {
		List<ReorderReviewStockItemBean> beans = reorderReviewService.getReorderReview();

		if(beans.isEmpty()) {
			logger.info("Reorder review initialized but no stock");
			addInfo("No stock for reorder review, try again after some sales!", modelMap);
			return "welcome";
		}

		//Place in session and then go to page one
		session.setAttribute("reorderReviewBeans", beans);

		logger.info("Reorder review initialized with " + beans.size() + " stock items");

		return getItem(0, session, modelMap);
	}

	@RequestMapping(value="/start", method=RequestMethod.GET)
	public String start(HttpSession session, ModelMap modelMap) {
		return getItem(0, session, modelMap);
	}

	//Go to the selected ReorderReviewStockItemBean
	private String getItem(int index, HttpSession session, ModelMap modelMap) {
		List<ReorderReviewStockItemBean> beans = (List<ReorderReviewStockItemBean>) session.getAttribute("reorderReviewBeans");

		ReorderReviewStockItemBean reorderReviewStockItemBean = beans.get(index);

		reorderReviewService.populate(reorderReviewStockItemBean);

		fillModel(reorderReviewStockItemBean, beans, modelMap, session, index);

		logger.info("Reorder Review item : " + reorderReviewStockItemBean.getStockItem());

		return "reorderReviewItem";
	}


	@RequestMapping(value="/cancel", method=RequestMethod.GET)
	private String cancel(HttpSession session, ModelMap modelMap) {
		session.removeAttribute("reorderReviewBeans");
		session.removeAttribute("supplierOrderMap");
		addSuccess("Have canceled the reorder review", modelMap);

		logger.info("Reorder review has been cancelled");

		return "welcome";
	}


	@RequestMapping(value="/save", method=RequestMethod.GET)
	private String save(HttpSession session, ModelMap modelMap) {
		List<ReorderReviewStockItemBean> beans = (List<ReorderReviewStockItemBean>) session.getAttribute("reorderReviewBeans");

		reorderReviewService.save(beans);

		session.removeAttribute("reorderReviewBeans");

		addSuccess("Have saved the reorder review", modelMap);

		logger.info("Successfully saved reorder review");

		return "welcome";
	}

	@RequestMapping(value="/showSummary", method=RequestMethod.GET)
	private String showSummary(HttpSession session, ModelMap modelMap) {
		List<ReorderReviewStockItemBean> beans = (List<ReorderReviewStockItemBean>) session.getAttribute("reorderReviewBeans");

		Collection<ReorderReviewStockItemBean> beansForSummary = new HashSet<ReorderReviewStockItemBean>();

		//Get all supplier order lines
		for(ReorderReviewStockItemBean bean : beans) {
			if(bean.isProcessed()) {
				beansForSummary.add(bean);
			}
		}

		modelMap.addAttribute(beansForSummary);

		return "reorderReviewSummary";
	}

	@RequestMapping(value="/process", method=RequestMethod.POST)
	private String process(@Valid ReorderReviewStockItemBean reorderReviewStockItemBean, BindingResult bindingResult, int index, String flow, HttpSession session, ModelMap modelMap) {

		supplierOrderLineValidator.validate(reorderReviewStockItemBean.getSupplierOrderLine(), bindingResult);

		if(bindingResult.hasErrors()) {
			return getItem(index, session, modelMap);
		}
		List<ReorderReviewStockItemBean> beans = (List<ReorderReviewStockItemBean>) session.getAttribute("reorderReviewBeans");

		//Get original bean and populate
		ReorderReviewStockItemBean originalBean = beans.get(index);
		if(flow.equals("removeFromMarxism")) {
			reorderReviewStockItemBean.getStockItem().setQuantityForMarxism(-1l);
		}
		reorderReviewService.process(originalBean, reorderReviewStockItemBean);

		if(flow.equals("next") || flow.equals("removeFromMarxism")) {
			index++;
		} else if(flow.equals("next20")) {
			index = index + 20;
		} else if(flow.equals("next100")) {
			index = index + 100;
		} else if(flow.equals("previous")) {
			index--;
		} else if(flow.equals("previous20")) {
			index = index - 20;
		}

		if(flow.equals("summary")) {
			return showSummary(session, modelMap);
		}

		return getItem(index, session, modelMap);
	}


	private void fillModel(ReorderReviewStockItemBean reorderReviewStockItemBean,
		List<ReorderReviewStockItemBean> beans, ModelMap modelMap, HttpSession session, int index) {
		modelMap.addAttribute(reorderReviewStockItemBean);
		modelMap.addAttribute(getSuppliers(session));
		modelMap.addAttribute("size", beans.size());
		modelMap.addAttribute("index", index);
		modelMap.addAttribute(getCategories());
		modelMap.addAttribute(reorderReviewStockItemBean.getMonthlySaleReportBean());
		modelMap.addAttribute(reorderReviewStockItemBean.getStockItem());
	}

	@Override
	public Service getService() {
		return reorderReviewService;
	}

}
