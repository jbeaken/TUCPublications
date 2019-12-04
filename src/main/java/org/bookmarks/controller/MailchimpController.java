package org.bookmarks.controller;

import com.ecwid.maleorang.method.v3_0.lists.members.MemberInfo;
import org.bookmarks.service.MailchimpService;
import org.bookmarks.service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


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
	public String subscribe(String email, String firstname, String lastname, ModelMap modelMap) {

		logger.info("Attempting to subscribe email : {}  name : {} {}", email, firstname, lastname);

		try {
			MemberInfo memberInfo = mailchimpService.subscribe(email, firstname, lastname);
			addSuccess("Successfully subscribed " +  email, modelMap);
			logger.info("Successfully subscribed {}", email);
		} catch(Exception e) {
				logger.error("Cannot subscribe to mailchimp", e);
				addError("There is a problem subscribing to mailchimp " + e.getMessage(), modelMap);
				return "subscribeMailchimp";
		}

		return "welcome";
	}

	@Override
	public Service getService() {
		return null;
	}
}
