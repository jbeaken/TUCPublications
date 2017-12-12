package org.bookmarks.controller;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.bookmarks.domain.StockItem;
import org.bookmarks.repository.CustomerRepository;
import org.bookmarks.repository.StockItemRepository;
import org.bookmarks.service.ChipsService;
import org.bookmarks.service.CustomerOrderService;
import org.bookmarks.service.Service;
import org.bookmarks.service.MailchimpService;
import org.bookmarks.website.domain.WebsiteCustomer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/mailchimp")
public class MailchimpController extends AbstractBookmarksController {


	@Autowired
	private MailchimpService mailchimpService;

	private final Logger logger = LoggerFactory.getLogger(MailchimpController.class);

	@RequestMapping(value = "/subscribe", method = RequestMethod.GET)
	public String subscribe(ModelMap modelMap) {

		addInfo("Enter email to subscribe to mailchimp mailing list", modelMap);

		return "subscribeMailchimp";
	}

	@RequestMapping(value = "/subscribe", method = RequestMethod.POST)
	public String subscribe(String email, ModelMap modelMap) {

		try {
			mailchimpService.subscribe(email);
			addSuccess("Successfully subscribed " + email, modelMap);
		} catch(Exception e) {
				logger.error("Cannot subscribe to mailchimp", e);
				addError("There is a problem subscribing to mailchimp", modelMap);
				return "subscribeMailchimp";
		}

		return "welcome";
	}

	@Override
	public Service getService() {
		return null;
	}
}
