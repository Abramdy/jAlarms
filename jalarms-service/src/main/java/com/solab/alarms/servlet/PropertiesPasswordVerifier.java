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

import java.util.Properties;

/** A PasswordVerifier that uses a Properties object to load its passwords.
 *
 * @author Enrique Zamudio
 */
public class PropertiesPasswordVerifier implements PasswordVerifier {

	private final Properties props;
	private boolean strict;

	public PropertiesPasswordVerifier(Properties properties) {
		props = properties;
	}

	/** Set the strict property to true if you want to reject any password not found in the properties, or false
	 * if you want to allow alarms for unknown sources. For example if there is no source "X" defined, then
	 * an alarm with src="X" can be sent if strict=false, or it will be rejected if strict=true. */
	public void setStrict(boolean flag) {
		strict = flag;
	}

	public boolean verifyPassword(String password, String src) {
		String check = props.getProperty(src == null ? "defaultPassword" : src);
		if (strict && check == null) {
			return false;
		}
		return check == null || check.equals(password);
	}

}
