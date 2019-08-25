package org.bookmarks.controller;

import java.math.BigDecimal;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.bookmarks.domain.VTTransaction;
import org.bookmarks.domain.StockItem;
import org.bookmarks.bean.VTTransactionStatus;
import org.bookmarks.bean.VTTransactionType;
import org.bookmarks.domain.Customer;
import org.bookmarks.service.AZLookupServiceImpl;
import org.bookmarks.service.VTTransactionService;
import org.bookmarks.service.CustomerService;
import org.bookmarks.service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vtTransaction")
public class VTTransactionController extends AbstractBookmarksController<VTTransaction> {

	@Autowired
	private VTTransactionService vtTransactionService;

	@Autowired
	private CustomerService customerService;

	private Logger logger = LoggerFactory.getLogger(VTTransactionController.class);


	@RequestMapping(value="/search")
	public String search(VTTransactionSearchBean vtTransactionSearchBean, HttpServletRequest request, HttpSession session, ModelMap modelMap) {

		if(vtTransactionSearchBean.isFromSession() == false) { //Pagination etc. already set
			setPaginationFromRequest(vtTransactionSearchBean, request);
		} else {
			//
		}

		Collection<VTTransaction> vtTransactionList = vtTransactionService.search(vtTransactionSearchBean);

		
		//Don't like, fix for shitty export
		setPageSize(vtTransactionSearchBean, modelMap, vtTransactionList.size());

		//Add to session for later search
		session.setAttribute("vtTransactionSearchBean", vtTransactionSearchBean);

		modelMap.addAttribute("vtTransactionSearchBean", vtTransactionSearchBean);
		modelMap.addAttribute("vtTransactionList", vtTransactionList);
		modelMap.addAttribute("searchResultCount", vtTransactionSearchBean.getSearchResultCount());

		return "searchVTTransactions";
	}


	@RequestMapping(value="/displaySearch", method=RequestMethod.GET)
	public String displaySearch(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		VTTransactionSearchBean vtTransactionSearchBean = new VTTransactionSearchBean();
		
		modelMap.addAttribute("vtTransactionSearchBean", vtTransactionSearchBean);
		modelMap.addAttribute(VTTransactionStatus.values());
		modelMap.addAttribute(VTTransactionType.values());
		
		
		return search(vtTransactionSearchBean, request, session, modelMap);
	}

	@RequestMapping(value="/delete", method=RequestMethod.GET)
	public String delete(Long id, RedirectAttributes redirectAttributes, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		VTTransaction vtTransaction = vtTransactionService.get(id);
		try {
			vtTransactionService.delete(vtTransaction);
			redirectAttributes.addFlashAttribute("success", " has been deleted");
		} catch (Exception e) {
			//Most likely due to this invoice being referenced from col
			addError("Cannot delete! Perhaps this vtTransaction has vtTransactioned", modelMap);
			return searchFromSession(session, request, modelMap);
		}


		return "redirect:searchFromSession";
	}

	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String add(@Valid VTTransaction vtTransaction, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpSession session, HttpServletRequest request, ModelMap modelMap) {

		if(bindingResult.hasErrors()){
			addError("Cannot save vtTransaction", modelMap);
			return "addVTTransaction";
		}

		vtTransactionService.save(vtTransaction);

		VTTransactionSearchBean vtTransactionSearchBean = new VTTransactionSearchBean();
		vtTransactionSearchBean.setTransaction(vtTransaction);

		session.setAttribute("vtTransactionSearchBean", vtTransactionSearchBean);

		redirectAttributes.addFlashAttribute("success", "Have added vtTransaction ");

		return "redirect:searchFromSession";
	}


	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public String edit(@Valid VTTransaction vtTransaction, BindingResult bindingResult, String flow, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		//Check for errors
		if(bindingResult.hasErrors()){
			modelMap.addAttribute("flow", flow);
			return "editVTTransaction";
		}

		VTTransaction dbVTTransaction = vtTransactionService.get(vtTransaction.getId());

		vtTransactionService.update(dbVTTransaction);

		VTTransactionSearchBean vtTransactionSearchBean = new VTTransactionSearchBean();
		vtTransactionSearchBean.setTransaction(vtTransaction);
		session.setAttribute("vtTransactionSearchBean", vtTransactionSearchBean);

		return "redirect:searchFromSession";
	}

	@RequestMapping(value="/edit", method=RequestMethod.GET)
	public String displayEdit(Long id, String flow, ModelMap modelMap) {
		VTTransaction vtTransaction = vtTransactionService.get(id);
		modelMap.addAttribute(vtTransaction);
		modelMap.addAttribute("flow", flow);
		return "editVTTransaction";
	}


	@RequestMapping(value="/searchFromSession")
	public String searchFromSession(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		VTTransactionSearchBean vtTransactionSearchBean = (VTTransactionSearchBean) session.getAttribute("vtTransactionSearchBean");

		if(vtTransactionSearchBean == null) {
			vtTransactionSearchBean = new VTTransactionSearchBean();
		}

		vtTransactionSearchBean.isFromSession(true);

		modelMap.addAttribute(vtTransactionSearchBean);

		return search(vtTransactionSearchBean, request, session, modelMap);
	}

	@Override
	public Service getService() {
		return vtTransactionService;
	}
}
