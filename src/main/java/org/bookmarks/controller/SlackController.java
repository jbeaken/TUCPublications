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
import org.bookmarks.service.SlackService;
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
@RequestMapping("/slack")
public class SlackController extends AbstractBookmarksController {


	@Autowired
	private SlackService slackService;

	private final Logger logger = LoggerFactory.getLogger(SlackController.class);

	@RequestMapping(value = "/post", method = RequestMethod.GET)
	public String post(HttpSession session, ModelMap modelMap) throws java.io.IOException {

		slackService.post();

		return "welcome";
	}

	@RequestMapping(value = "/todo", method = RequestMethod.GET)
	public String todo(HttpSession session, ModelMap modelMap) throws java.io.IOException {

		slackService.todo();

		return "welcome";
	}


	@Override
	public Service getService() {
		return null;
	}
}
