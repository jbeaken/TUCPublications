package org.bookmarks.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpSession;

import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.core.io.Resource;
import org.apache.commons.io.IOUtils;
import org.springframework.context.ApplicationContext;

import org.bookmarks.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/.well-known")
public class ChallengeController extends AbstractBookmarksController {

	@Autowired
	private ApplicationContext appContext;

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
