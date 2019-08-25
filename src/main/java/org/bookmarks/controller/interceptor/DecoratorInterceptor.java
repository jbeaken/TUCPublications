package org.bookmarks.controller.interceptor;

import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.bookmarks.domain.Event;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Interceptor that allows for changing the current locale on every request,
 * via a configurable request parameter.
 *
 * @author Juergen Hoeller
 * @since 20.06.2003
 * @see org.springframework.web.servlet.LocaleResolver
 */
public class DecoratorInterceptor extends HandlerInterceptorAdapter {




	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws ServletException {

		HttpSession session = request.getSession(true);
		String contextPath = request.getRequestURI();
		if(contextPath.contains("resources")) {
			return true;
		}
		if(contextPath.contains("startSelling")) {
			contextPath = "sale";
		}
		if(contextPath.contains("customerOrderLine/complete")) {
			contextPath = "invoice";
		}
		if(contextPath.contains("showSales")) {
			contextPath = "sale";
		}
		if(contextPath.contains("sellAndStayByISBN")) {
			contextPath = "stock";
		}
		
		if(session != null) {
	 		Event event = (Event) session.getAttribute("event");
			if(event != null) contextPath = "invoice";
		}
		
		if(contextPath.contains("invoice")){
		    request.setAttribute("buttonImage", "green");
			request.setAttribute("menuColour", "#3b6e22");
			request.setAttribute("menuHoverColour", "#99ff25");
			request.setAttribute("tableRowEven", "#b4f797");
		} else if(contextPath.contains("supplier")){
			request.setAttribute("buttonImage", "red");
			request.setAttribute("menuColour", "#ff5c0b");
			request.setAttribute("menuHoverColour", "red");
			request.setAttribute("tableRowEven", "#ffa455");
		} else if(contextPath.contains("sale")){
			request.setAttribute("buttonImage", "purple");
			request.setAttribute("menuColour", "#0909c1");
			request.setAttribute("menuHoverColour", "#520ae2");
			request.setAttribute("tableRowEven", "#83a8ff");
		} else {
			request.setAttribute("buttonImage", "blue");
			request.setAttribute("menuColour", "#16a4e1");
			request.setAttribute("menuHoverColour", "#a2ffff");
		}
		return true;
	}

}
