package org.bookmarks.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.http.client.ClientProtocolException;
import org.bookmarks.scheduler.ChipsOrdersManager;
import org.bookmarks.scheduler.GardnersAvailability;
import org.bookmarks.scheduler.DailyReport;
import org.bookmarks.service.AdminService;
import org.bookmarks.service.EmailService;
import org.bookmarks.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/documentation")
public class DocumentationController {


	@RequestMapping(value="/")
	public String index(ModelMap modelMap) throws ClientProtocolException, IOException {
		return "documentation";
	}

	@RequestMapping(value="/technical")
	public String technical(ModelMap modelMap) throws ClientProtocolException, IOException {
		return "technical";
	}

}
