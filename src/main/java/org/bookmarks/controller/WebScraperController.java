package org.bookmarks.controller;

import java.io.ObjectInputStream.GetField;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.bookmarks.domain.CreditNote;
import org.bookmarks.domain.Customer;
import org.bookmarks.domain.CustomerOrder;
import org.bookmarks.domain.CustomerType;
import org.bookmarks.scheduler.HaymarketWebScraper;
import org.bookmarks.scheduler.LRBWebScraper;
import org.bookmarks.scheduler.PlutoWebScraper;
import org.bookmarks.scheduler.VersoWebScraper;
import org.bookmarks.scheduler.WebScraper;
import org.bookmarks.scheduler.ZedWebScraper;
import org.bookmarks.service.AdminService;
import org.bookmarks.service.CustomerService;
import org.bookmarks.service.EmailService;
import org.bookmarks.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
@RequestMapping("/webScraper")
public class WebScraperController extends AbstractBookmarksController {

	@Autowired private HaymarketWebScraper haymarketWebScraper;
	
	@Autowired
	private ZedWebScraper zedWebScraper;	
	
	@Autowired
	private VersoWebScraper versoWebScraper;	
	
	@Autowired
	private LRBWebScraper lrbWebScraper;
	
	@Autowired
	private PlutoWebScraper plutoWebScraper;		

//	@RequestMapping(value="/getVerso")
//	public String getVerso(HttpSession session, ModelMap modelMap) throws Exception {
//		
//		webScraper.getVerso();
//		
//		addSuccess("Have sucessfully scraped Verso", modelMap);
//		return "welcome";
//	}
	
	@RequestMapping(value="/getZed")
	public String getZed(HttpSession session, ModelMap modelMap) throws Exception {
		
		zedWebScraper.scrape();
		
		addSuccess("Have sucessfully scraped Zed", modelMap);
		return "welcome";
	}
	
	
	@RequestMapping(value="/getHaymarket")
	public String getHaymarket(HttpSession session, ModelMap modelMap) throws Exception {
		
		haymarketWebScraper.scrape();
		
		addSuccess("Have sucessfully scraped Haymarket", modelMap);
		return "welcome";
	}	
	
	@RequestMapping(value="/getPluto")
	public String getPluto(HttpSession session, ModelMap modelMap) throws Exception {
		
		plutoWebScraper.scrape();
		
		addSuccess("Have sucessfully scraped Pluto", modelMap);
		return "welcome";
	}	
	
	@RequestMapping(value="/getVerso")
	public String getVerso(HttpSession session, ModelMap modelMap) throws Exception {
		
		versoWebScraper.scrape();
		
		addSuccess("Have sucessfully scraped Verso", modelMap);
		return "welcome";
	}
	
	@RequestMapping(value="/getLRB")
	public String getLRB(HttpSession session, ModelMap modelMap) throws Exception {
		
		lrbWebScraper.scrape();
		
		addSuccess("Have sucessfully scraped LRB", modelMap);
		return "welcome";
	}		
	
	@RequestMapping(value="/getPolity")
	public String getPolity(HttpSession session, ModelMap modelMap) throws Exception {
		
		//polityWebScraper.scrape();
		
		addSuccess("Have sucessfully scraped Polity", modelMap);
		return "welcome";
	}		


	@Override
	public Service getService() {
		// TODO Auto-generated method stub
		return null;
	}
}
