package org.bookmarks.controller;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.math.BigDecimal;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVPrinter;

import java.io.Reader;
import java.io.InputStreamReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.bookmarks.bean.CSVResponse;
import org.bookmarks.controller.bean.SaleReportBean;
import org.bookmarks.domain.AbstractEntity;
import org.bookmarks.domain.Staff;
import org.bookmarks.exceptions.BookmarksException;

import org.bookmarks.service.StaffService;
import org.bookmarks.service.Service;
import org.bookmarks.service.SaleService;
import org.bookmarks.service.StockItemService;
import org.bookmarks.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.multipart.MultipartFile;

import org.springframework.format.number.CurrencyStyleFormatter;

@Controller
@RequestMapping("/staff")
public class StaffController extends AbstractBookmarksController {

	@Autowired
	private StaffService staffService;

	private Logger logger = LoggerFactory.getLogger(StaffController.class);

	@RequestMapping(value = "/search")
	public String search(StaffSearchBean staffSearchBean, HttpServletRequest request, ModelMap modelMap) {

		if (staffSearchBean == null) {
			staffSearchBean = new StaffSearchBean();
		}

		setPaginationFromRequest(staffSearchBean, request);

		Collection<Staff> staffList = staffService.search(staffSearchBean);

		// Don't like, fix for shitty export
		setPageSize(staffSearchBean, modelMap, staffList.size());

		modelMap.addAttribute("staffList", staffList);
		modelMap.addAttribute("searchResultCount", staffSearchBean.getSearchResultCount());

		logger.debug("Request for search staff, found {} staff members", staffList.size());

		return "searchStaff";
	}

	@RequestMapping(value = "/delete/{id}")
	public String delete(@PathVariable("id") Long id, HttpServletRequest request, ModelMap modelMap) {

		Staff staff = staffService.get(id);

		try {
			staffService.delete(staff);
			addSuccess("Staff " + staff.getName() + " has been deleted", modelMap);
		} catch (Exception e) {
			e.printStackTrace();
			addError("Cannot delete staff, most probably because sales have been attached.", modelMap);
		}

		StaffSearchBean staffSearchBean = new StaffSearchBean();
		modelMap.addAttribute("staffSearchBean", staffSearchBean);

		logger.info("Deleted staff : {}", staff.getName());

		return search(staffSearchBean, request, modelMap);
	}

	@RequestMapping(value = "/reset", method = RequestMethod.POST)
	public String reset(HttpServletRequest request, ModelMap modelMap) {
		StaffSearchBean staffSearchBean = new StaffSearchBean();
		modelMap.addAttribute(staffSearchBean);
		return search(staffSearchBean, request, modelMap);
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(@Valid Staff staff, BindingResult bindingResult, HttpServletRequest request, HttpSession session, ModelMap modelMap) {

		// Check for errors
		if (bindingResult.hasErrors()) {
			return "addStaff";
		}

		staffService.save(staff);

		// Redirect
		StaffSearchBean staffSearchBean = new StaffSearchBean();
		staffSearchBean.getStaff().setName(staff.getName());
		session.setAttribute("searchBean", staffSearchBean);

		logger.info("Added staff : {}", ReflectionToStringBuilder.toString(staff));

		return "redirect:searchFromSession";
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String displayAdd(ModelMap modelMap) {
		modelMap.addAttribute(new Staff());
		return "addStaff";
	}

	@RequestMapping(value = "/searchFromSession")
	public String searchFromSession(HttpSession session, HttpServletRequest request, ModelMap modelMap) {
		StaffSearchBean staffSearchBean = (StaffSearchBean) session.getAttribute("searchBean");
		staffSearchBean.isFromSession(true);
		modelMap.addAttribute(staffSearchBean);
		return search(staffSearchBean, request, modelMap);
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String edit(@Valid Staff staff, BindingResult bindingResult, HttpServletRequest request, HttpSession session, ModelMap modelMap) {

		// Check for errors
		if (bindingResult.hasErrors()) {
			return "editStaff";
		}

		staffService.update(staff);

		// Redirect
		StaffSearchBean staffSearchBean = new StaffSearchBean();
		staffSearchBean.getStaff().setName(staff.getName());
		session.setAttribute("searchBean", staffSearchBean);

		logger.info("Edited staff : " + staff.getName());

		return "redirect:searchFromSession";
	}

	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
	public String edit(@PathVariable("id") Long id, ModelMap modelMap) {
		Staff staff = staffService.get(id);

		modelMap.addAttribute(staff);

		return "editStaff";
	}

	@RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
	public String view(@PathVariable("id") Long id, ModelMap modelMap) {

		Staff staff = staffService.get(id);
		modelMap.addAttribute(staff);

		return "viewStaff";
	}

	@Override
	public Service getService() {
		return staffService;
	}
}
