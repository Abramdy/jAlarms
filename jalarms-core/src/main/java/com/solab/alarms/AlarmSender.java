/*
jAlarms A simple Java library to enable server apps to send alarms to sysadmins.
Copyright (C) 2009 Enrique Zamudio Lopez

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
*/
package com.solab.alarms;

/** This interface is used to connect to an AlarmSender instance over RMI, JNDI, etc.
 *
 * @author Enrique Zamudio
 */
public interface RemoteAlarmSender {

	/** Sends an alarm through all channels, to the users defined for the specified source in each channel,
	 * as long as the message hasn't been already sent very recently. If the message is sent, the time is recorded
	 * to avoid further sending until the interval has elapsed for each channel.
	 * 
	 * @param msg The message to be sent as an alarm.
	 * @param source The alarm source. The channels can use this information to determine the recipients
	 * of the alarm. If null, the default recipients for each channel are used.
	 */
	public void sendAlarm(final String msg, final String source);

	/** Sends an alarm through all channels, to the users defined for the specified source in each channel,
	 * regardless of the last time the same message was sent.
	 * 
	 * @param msg The alarm message to be sent through the channels.
	 * @param source The alarm source. The channels can use this information to determine
	 * the recipients of the alarm. If null, the default recipients for each channel are used.
	 */
	public void sendAlarmAlways(final String msg, final String source);

	/** Sends an alarm through all channels, to the default users defined for each channel, as long as
	 * the message hasn't been already sent very recently. If the message is sent, the time is recorded
	 * to avoid further sending until the interval has elapsed for each channel. */
	public void sendAlarm(final String msg);

	/** Sends an alarm through all channels, to the default users defined for each channel, regardless
	 * of the last time the same message was sent. */
	public void sendAlarmAlways(final String msg);

}
