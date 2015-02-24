package org.bookmarks.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.bookmarks.service.EmailService;
import org.bookmarks.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController extends AbstractBookmarksController {

	@Autowired
	private EmailService emailService;

	@RequestMapping(value="/consume")
	public String uploadAccounts(HttpSession session, ModelMap modelMap) {
		List<String> messages =  null;
		modelMap.addAttribute("messages", messages);
		return "uploadAccounts";
	}
	
	  @ExceptionHandler(NullPointerException.class)
      public String handleException (NullPointerException ex) {
		emailService.emailErrorToJack(ex);
        return "500ErrorPage";
      }	

	@Override
	public Service getService() {
		return null;
	}

}
