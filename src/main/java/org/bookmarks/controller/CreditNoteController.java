package org.bookmarks.controller;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.bookmarks.domain.CreditNote;
import org.bookmarks.domain.StockItem;
import org.bookmarks.service.AZLookupServiceImpl;
import org.bookmarks.service.CreditNoteService;
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
@RequestMapping("/creditNote")
public class CreditNoteController extends AbstractBookmarksController<CreditNote> {

	@Autowired
	private CreditNoteService creditNoteService;
	
	private Logger logger = LoggerFactory.getLogger(CreditNoteController.class);
	
//	@ResponseBody
//	@RequestMapping(value="/addCreditNote", method=RequestMethod.GET)
//	public String addCreditNote(String name, HttpSession session) {
//		logger.debug("Adding " + name + " to stock item as new creditNote");
//		
//		StockItem stockItem = (StockItem) session.getAttribute("sessionStockItem");
//		
//		if(stockItem == null) {
//			//Session timeout
//			return "failure"; 
//		}
//		 
//		CreditNote exists = creditNoteService.findByName(name);
//		if(exists != null) {
//			logger.debug("CreditNote already exists");
//			return "CreditNote already exists";
//		}
//		
//		CreditNote creditNote = new CreditNote();
//		creditNote.setName(name);
////		creditNoteService.save(creditNote);
//		
//		stockItem.getCreditNotes().add(creditNote);
//		logger.debug("Have added creditNote " + creditNote.getName() + " to " + stockItem.getTitle());
//		
//		return getCreditNoteTable(stockItem);
//	}		
	
//	@ResponseBody
//	@RequestMapping(value="/removeCreditNote", method=RequestMethod.GET)
//	public String removeCreditNote(String name, HttpSession session, ModelMap modelMap) {
//
//		StockItem stockItem = (StockItem) session.getAttribute("sessionStockItem");
//		
//		if(stockItem == null) {
//			//Session timeout
//			return "failure";  
//		}
//		
//		CreditNote toRemove = null;
//		for(CreditNote a : stockItem.getCreditNotes()) {
//			if(a.getName().equals(name)) {
//				toRemove = a;
//			}
//		}
//		stockItem.getCreditNotes().remove(toRemove);
//		
//		return getCreditNoteTable(stockItem);
//	}	
	
//	@ResponseBody
//    @RequestMapping(value="/addCreditNoteToStock", method=RequestMethod.GET)
//	public String addCreditNoteToStock(Long creditNoteId, HttpSession session) {
//		StockItem stockItem = (StockItem) session.getAttribute("sessionStockItem");
//		if(stockItem == null) {
//			//Session timeout
//			return "failure";
//		}
//		
//		CreditNote creditNote = creditNoteService.get(creditNoteId);
//		stockItem.getCreditNotes().add(creditNote);
//
//		return getCreditNoteTable(stockItem);
//	}
	
//	@ResponseBody
//    @RequestMapping(value="/loadCreditNoteTable", method=RequestMethod.GET)
//	public String loadCreditNoteTable(HttpSession session) {
//		StockItem stockItem = (StockItem) session.getAttribute("sessionStockItem");
//		if(stockItem == null) {
//			//Session timeout
//			return "failure";
//		}
//		
//		return getCreditNoteTable(stockItem);
//	}	
	
//	private String getCreditNoteTable(StockItem stockItem) {
//		String table = "<table><th>CreditNote</th><th>Actions</th>";
//		for(CreditNote a : stockItem.getCreditNotes()) {
//			table = table + "<tr><td>" + a.getTransactionDescription() + "</td><td><a onclick=\"javascript:removeCreditNote('" + a.getTransactionDescription() + "')\"> Delete</a></td></tr>";
//		}
//		table = table + "</table>";  
//		
//		logger.debug(table);
//		
//		return table;
//	}

	@RequestMapping(value="/search")
	public String search(CreditNoteSearchBean creditNoteSearchBean, HttpServletRequest request, HttpSession session, ModelMap modelMap) {
		
		if(creditNoteSearchBean.isFromSession() == false) { //Pagination etc. already set
			setPaginationFromRequest(creditNoteSearchBean, request);
		} else {
			//
		}

		Collection<CreditNote> creditNoteList = creditNoteService.search(creditNoteSearchBean);

		//Don't like, fix for shitty export
		setPageSize(creditNoteSearchBean, modelMap, creditNoteList.size());

		//Add to session for later search
		session.setAttribute("creditNoteSearchBean", creditNoteSearchBean);

		modelMap.addAttribute("creditNoteList", creditNoteList);
		modelMap.addAttribute("searchResultCount", creditNoteSearchBean.getSearchResultCount());
		
		return "searchCreditNotes";
	}

	

	@RequestMapping(value="/displaySearch", method=RequestMethod.GET)
	public String displaySearch(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		CreditNoteSearchBean creditNoteSearchBean = new CreditNoteSearchBean();
		modelMap.addAttribute(creditNoteSearchBean);
		return search(creditNoteSearchBean, request, session, modelMap);
	}

	@RequestMapping(value="/delete", method=RequestMethod.GET)
	public String delete(Long id, RedirectAttributes redirectAttributes, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		CreditNote creditNote = creditNoteService.get(id);
		try {
			creditNoteService.delete(creditNote);
			redirectAttributes.addFlashAttribute("success", creditNote.getTransactionDescription() + " has been deleted");
		} catch (Exception e) {
			//Most likely due to this invoice being referenced from col
			addError("Cannot delete! Perhaps this creditNote has creditNoteed", modelMap);
			return searchFromSession(session, request, modelMap);
		}		
		
		
		return "redirect:searchFromSession";
	}	

	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String add(@Valid CreditNote creditNote, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		
		
//		CreditNote exists = creditNoteService.findByName(creditNote.getName());
//		
//		if(exists != null) {
//			addError(creditNote.getTransactionDescription() + " already exists", modelMap);
//			return "addCreditNote";
//		}
		if(bindingResult.hasErrors()){
			addError("Cannot save creditNote", modelMap);
			return "addCreditNote";
		}
		
		creditNoteService.save(creditNote);
		
		CreditNoteSearchBean creditNoteSearchBean = new CreditNoteSearchBean();
		creditNoteSearchBean.setCreditNote(creditNote);
		
		session.setAttribute("creditNoteSearchBean", creditNoteSearchBean);		
		
		redirectAttributes.addFlashAttribute("success", "Have added creditNote " + creditNote.getTransactionDescription());
		
		return "redirect:searchFromSession";
	}
	
	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String displayAdd(ModelMap modelMap) {
		modelMap.addAttribute(new CreditNote());
		return "addCreditNote";
	}

	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public String edit(@Valid CreditNote creditNote, BindingResult bindingResult, String flow, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		//Check for errors
		if(bindingResult.hasErrors()){
			modelMap.addAttribute("flow", flow);
			return "editCreditNote";
		}
		
		CreditNote dbCreditNote = creditNoteService.get(creditNote.getId());

		creditNoteService.update(dbCreditNote);

		CreditNoteSearchBean creditNoteSearchBean = new CreditNoteSearchBean();
		creditNoteSearchBean.setCreditNote(creditNote);
		session.setAttribute("creditNoteSearchBean", creditNoteSearchBean);				

		return "redirect:searchFromSession";
	}

	@RequestMapping(value="/edit", method=RequestMethod.GET)
	public String displayEdit(Long id, String flow, ModelMap modelMap) {
		CreditNote creditNote = creditNoteService.get(id);
		modelMap.addAttribute(creditNote);
		modelMap.addAttribute("flow", flow);
		return "editCreditNote";
	}


	@RequestMapping(value="/searchFromSession")
	public String searchFromSession(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		CreditNoteSearchBean creditNoteSearchBean = (CreditNoteSearchBean) session.getAttribute("creditNoteSearchBean");
		
		if(creditNoteSearchBean == null) {
			creditNoteSearchBean = new CreditNoteSearchBean();
		}
		
		creditNoteSearchBean.isFromSession(true);
		
		modelMap.addAttribute(creditNoteSearchBean);
		
		return search(creditNoteSearchBean, request, session, modelMap);
	}

	@Override
	public Service getService() {
		return creditNoteService;
	}
}

