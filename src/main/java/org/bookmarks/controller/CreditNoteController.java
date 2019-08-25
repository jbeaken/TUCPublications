package org.bookmarks.controller;

import java.math.BigDecimal;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.bookmarks.domain.CreditNote;
import org.bookmarks.domain.StockItem;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.TransactionType;

import org.bookmarks.service.AZLookupServiceImpl;
import org.bookmarks.service.CreditNoteService;
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
@RequestMapping("/creditNote")
public class CreditNoteController extends AbstractBookmarksController<CreditNote> {

	@Autowired
	private CreditNoteService creditNoteService;

	@Autowired
	private CustomerService customerService;

	private Logger logger = LoggerFactory.getLogger(CreditNoteController.class);


	@RequestMapping(value="/search")
	public String search(CreditNoteSearchBean creditNoteSearchBean, HttpServletRequest request, HttpSession session, ModelMap modelMap) {

		if(creditNoteSearchBean.isFromSession() == false) { //Pagination etc. already set
			setPaginationFromRequest(creditNoteSearchBean, request);
		} else {
			//
		}

		Collection<CreditNote> creditNoteList = creditNoteService.search(creditNoteSearchBean);

		//Now reconcile
	//	BigDecimal clubAccountOutgoings = creditNoteService.getOutgoings();
		//BigDecimal clubAccountIncomings = creditNoteService.getIncomings();

		//BigDecimal clubAccountBalance = clubAccountOutgoings.add(clubAccountIncomings);

		//Don't like, fix for shitty export
		setPageSize(creditNoteSearchBean, modelMap, creditNoteList.size());

		//Add to session for later search
		session.setAttribute("creditNoteSearchBean", creditNoteSearchBean);

		modelMap.addAttribute("creditNoteList", creditNoteList);
		modelMap.addAttribute(TransactionType.values());
		//modelMap.addAttribute("clubAccountOutgoings", clubAccountOutgoings);
		//modelMap.addAttribute("clubAccountIncomings", clubAccountIncomings);
		//modelMap.addAttribute("clubAccountBalance", clubAccountBalance);
		modelMap.addAttribute("searchResultCount", creditNoteSearchBean.getSearchResultCount());

		return "searchCreditNotes";
	}


	@RequestMapping(value="/displaySearch", method=RequestMethod.GET)
	public String displaySearch(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		CreditNoteSearchBean creditNoteSearchBean = new CreditNoteSearchBean();
		modelMap.addAttribute(creditNoteSearchBean);
		modelMap.addAttribute(TransactionType.values());

		return search(creditNoteSearchBean, request, session, modelMap);
	}

	@RequestMapping(value="/delete", method=RequestMethod.GET)
	public String delete(Long id, RedirectAttributes redirectAttributes, HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		CreditNote creditNote = creditNoteService.get(id);
		try {
			//Reverse account balance
			customerService.debitAccount( creditNote.getCustomer(), creditNote.getAmount().multiply(new BigDecimal( -1 )) );

			//Delete
			creditNoteService.delete(creditNote);

			redirectAttributes.addFlashAttribute("success", creditNote.getTransactionDescription() + " has been deleted");
		} catch (Exception e) {
			//Most likely due to this invoice being referenced from col
			addError("Cannot delete!", modelMap);
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

		redirectAttributes.addFlashAttribute("success", "Have added credit note " + creditNote.getTransactionDescription());

		return "redirect:searchFromSession";
	}

	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String displayAdd(Customer customer, ModelMap modelMap) {
		CreditNote creditNote = new CreditNote();

		Customer dbCustomer = customerService.get( customer.getId() );

		creditNote.setCustomer( dbCustomer );

		modelMap.addAttribute(creditNote);

		return "addCreditNote";
	}

	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public String edit(CreditNote creditNote, ModelMap modelMap) {

		CreditNote dbCreditNote = creditNoteService.get(creditNote.getId());

		dbCreditNote.setNote( creditNote.getNote() );

		if(dbCreditNote.getCustomer().getId() != creditNote.getCustomer().getId()) {
			//Customer has changed.
			//Debit old customer
			creditNoteService.creditAccount( dbCreditNote.getCustomer(), dbCreditNote.getAmount().negate() );

			//Credit new customer
			creditNoteService.creditAccount( creditNote.getCustomer(), dbCreditNote.getAmount() );

			//Deal with match, remove from old
			//update customer set tsbMatch = null where tsbMatch = creditNote.getTransactionDescription();
			//update customer set tsbMatchSecondary = null where tsbMatchSecondary = ?
			creditNoteService.removeMatch( dbCreditNote.getTransactionDescription() );

			//Skip add, do manually on next import

			//Move over new customer

			dbCreditNote.setCustomer( creditNote.getCustomer() );
		}

		creditNoteService.update(dbCreditNote);

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
