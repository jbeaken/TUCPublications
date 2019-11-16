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
@RequestMapping("/.well-known")
public class ChallengeController extends AbstractBookmarksController {

	@RequestMapping("/acme-challenge/{filename}")
	public void certbot(@PathVariable("filename") String filename, HttpServletResponse response) throws java.io.IOException {

		Resource resource = appContext.getResource("file:/var/www/html/.well-known/acme-challenge/" + filename);

		InputStream is = resource.getInputStream();

		OutputStream os = response.getOutputStream();

		IOUtils.copy(is, os);
		os.flush();
		os.close();
	}

	@Override
	public Service getService() {
		return null;
	}
}
