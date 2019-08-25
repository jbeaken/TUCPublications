package org.bookmarks.controller.az;

import java.util.Collection;

import org.bookmarks.controller.CustomerSearchBean;
import org.bookmarks.domain.Customer;
import org.bookmarks.service.AZLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/isbn")
public class ISBNLookup {
	
	@Autowired
	private AZLookupService azLookupService;
	
	@RequestMapping(value="/lookup", method=RequestMethod.POST)
	public String search(CustomerSearchBean customerSearchBean, ModelMap modelMap) {
		return "";
	}
}
