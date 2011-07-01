/*
 * jAlarms A simple Java library to enable server apps to send alarms to sysadmins.
 * Copyright (C) 2011 Enrique Zamudio Lopez
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */
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
	private PasswordVerifier auth;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		WebApplicationContext ctxt = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
		sender = ctxt.getBean(AlarmSenderImpl.class);
		auth = ctxt.getBean(PasswordVerifier.class);
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
			if (auth.verifyPassword(pass, src)) {
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
