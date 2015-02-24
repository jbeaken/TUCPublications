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
@RequestMapping("/admin")
public class AdminController extends AbstractBookmarksController {

	@Autowired
	private AdminService adminService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private GardnersAvailability gardnersAvailability;
	
	@Autowired
	private ChipsOrdersManager chipsOrdersManager; 

	@Autowired
	private DailyReport dailyReport;
	
	@RequestMapping(value="/sendDailyEmail")
	public String sendDailyEmail(HttpSession session, ModelMap modelMap) throws ClientProtocolException, IOException {
		dailyReport.process();
		return "welcome";
	}
	
	@RequestMapping(value="/checkGardnersAvailability")
	public String gardnersAvailability(HttpSession session, ModelMap modelMap) throws IOException {
		gardnersAvailability.checkAvailability();
		return "welcome";
	}	
	
	@Override
	public Service getService() {
		// TODO Auto-generated method stub
		return null;
	}

}
