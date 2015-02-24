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
import org.bookmarks.service.EventService;
import org.bookmarks.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/calendar")
public class CalendarController extends AbstractBookmarksController {

	@Autowired
	private EventService eventService;
	
//	@Autowired
//	private EmailService emailService;
//	
//	@Autowired
//	private GardnersAvailability gardnersAvailability;
//	
//	@Autowired
//	private ChipsOrdersManager chipsOrdersManager; 
//
//	@Autowired
//	private DailyReport dailyReport;
	
	@RequestMapping(value="/view")
	public String view(HttpSession session, ModelMap modelMap) throws ClientProtocolException, IOException {
		return "viewCalendar";
	}
	
	
	@Override
	public Service getService() {
		return null;
	}

}
