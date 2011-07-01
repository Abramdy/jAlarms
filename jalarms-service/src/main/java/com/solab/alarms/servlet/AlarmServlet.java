package com.solab.alarms.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import javax.annotation.*;
import com.solab.alarms.*;
import org.springframework.web.context.support.*;
import org.springframework.web.context.*;
import org.springframework.beans.factory.config.*;

/** This servlet can receive requests with "alarm" and "src" parameters, and sends an alarm message using
 * the configured AlarmSender.
 */
public class AlarmServlet extends HttpServlet {

	private AlarmSenderImpl sender;
	private java.util.Properties authbase;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		WebApplicationContext ctxt = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
		sender = ctxt.getBean(AlarmSenderImpl.class);
		authbase = (java.util.Properties)ctxt.getBean("servletPasswords");
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String msg = request.getParameter("alarm");
		String src = request.getParameter("src");
		String pass = request.getParameter("password");
		String resp = "ERROR Incomplete Data, you must send field 'alarm' and optionally 'src'";
		if (msg != null) {
			String vpass = authbase.getProperty(src == null ? "defaultPassword" : src);
			boolean ok = true;
			if (vpass != null && vpass.length() > 0) {
				ok = vpass.equals(pass);
			}
			if (ok) {
				sender.sendAlarm(msg, src);
				resp = "OK";
			} else {
				resp = "AUTH failed";
			}
		}
		response.setContentType("text/plain");
		response.setContentLength(resp.length());
		response.getWriter().print(resp);
	}

}
