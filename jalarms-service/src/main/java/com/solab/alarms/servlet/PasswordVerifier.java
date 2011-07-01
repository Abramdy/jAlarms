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

/** A very simple interface that is used by the AlarmServlet to check that a password is valid before sending an alarm.
 *
 * @author Enrique Zamudio
 */
public interface PasswordVerifier {

	/** This method must return true if the password is correct, for the given source.
	 * @param password The password to verify
	 * @param src The alarm source (each source can have its own password). Pass null to check against the default password. */
	public boolean verifyPassword(String password, String src);

}
