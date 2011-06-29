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
 * */
package com.solab.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AlarmHash {

	private static final char[] hex = new char[]{
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
	};
	//MessageDigest instances are not thread-safe, so we store them in a ThreadLocal variable.
	private static ThreadLocal<MessageDigest> md5 = new ThreadLocal<MessageDigest>(){
		@Override
		protected MessageDigest initialValue() {
			try {
				return MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException ex) {
				return null;
			}
		}
	};

	/** Returns a MD5 hash of the specified message. It can be used to keep a map of the messages sent along
	 * with the last time for each one, as is done in the AbstractAlarmChannel. */
	public static String hash(final String msg) {
		MessageDigest _md = md5.get();
		String hash = "hash";
		if (_md != null) {
			_md.reset();
			byte[] buf = _md.digest(msg.getBytes());
			char[] c = new char[buf.length * 2];
			for (int i = 0; i < buf.length; i++) {
				c[i * 2] = hex[(buf[i] & 0xf0) >> 4];
				c[(i * 2) + 1] = hex[buf[i] & 0x0f];
			}
			hash = new String(c);
		}
		return hash;
	}

}
