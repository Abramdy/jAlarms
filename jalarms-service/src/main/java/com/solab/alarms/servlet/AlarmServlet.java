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

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		sender = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext()).getBean(AlarmSenderImpl.class);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String msg = request.getParameter("alarm");
		String src = request.getParameter("src");
		String resp = "ERROR Incomplete Data, you must send field 'alarm' and optionally 'src'";
		if (msg != null) {
			//TODO setup some kind of password for default messages and per-src passwords
			if (src != null) {
			}
			sender.sendAlarm(msg, src);
			resp = "OK";
		}
		response.setContentType("text/plain");
		response.setContentLength(resp.length());
		response.getWriter().print(resp);
	}

}
