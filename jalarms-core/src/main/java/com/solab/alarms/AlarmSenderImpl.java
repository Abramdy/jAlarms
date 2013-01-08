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

import com.solab.util.NamedThreadFactory;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/** This is the central class for jAlarms. An AlarmSender can have several AlarmChannel instances
 * to send alarms through different channels to different people.
 * 
 * @author Enrique Zamudio
 */
public class AlarmSenderImpl implements AlarmSender {

	private List<AlarmChannel> chans = Collections.emptyList();
	private AlarmCache cache;
	private int bufTime;
	private ScheduledExecutorService timer;
	private ConcurrentHashMap<String, CachedAlarm> buffer;

	/** Sets the time in milliseconds that the alarms sent via {@link #sendAlarmAlways(String, String)} are
	 * buffered before actually sending them. Default is 0 which causes alarms to be sent immediately. If
	 * set to a positive value, alarms are not sent immediately, but are rather queued to be sent after
	 * sitting for the time specified here. If several identical messages are queued, only one is sent,
	 * specifying how many times it was received. The queue is reviewed every 30 seconds, which means that
	 * alarms will always be transmitted no sooner than 30 to 60 seconds after they're received, if they were
	 * only received one time; an alarm that's been received 2 or more times will be queued for as long
	 * as the buffer specifies. */
	public void setAlarmTimeBuffer(int value) {
		bufTime = value;
	}
	public int getAlarmTimeBuffer() {
		return bufTime;
	}

	/** Sets the cache to use for alarm messages. */
	public void setAlarmCache(AlarmCache value) {
		cache = value;
	}

	/** Sets the alarm channels to be used for sending alarm messages. */
	public void setAlarmChannels(List<AlarmChannel> channels) {
		chans = channels;
	}

	/** Sends an alarm through all channels, to the users defined for the specified source in each channel,
	 * as long as the message hasn't been already sent very recently. If the message is sent, the time is recorded
	 * to avoid further sending until the interval has elapsed for each channel.
	 * 
	 * @param msg The message to be sent as an alarm.
	 * @param source The alarm source. The channels can use this information to determine the recipients
	 * of the alarm. If null, the default recipients for each channel are used.
	 */
	public void sendAlarm(final String msg, final String source) {
		if (msg != null) {
			if (cache == null) {
				//Setup default cache if there's none
				synchronized(this) {
					if (cache == null) {
						cache = new DefaultAlarmCache();
					}
				}
			}
			for (AlarmChannel c: chans) {
				if (cache.shouldResend(c, source, msg)) {
					cache.store(c, source, msg);
					c.send(msg, source);
				}
			}
		}
	}

	/** Sends an alarm through all channels, to the users defined for the specified source in each channel,
	 * regardless of the last time the same message was sent.
	 * 
	 * @param msg The alarm message to be sent through the channels.
	 * @param source The alarm source. The channels can use this information to determine
	 * the recipients of the alarm. If null, the default recipients for each channel are used.
	 */
	public void sendAlarmAlways(final String msg, final String source) {
		if (bufTime > 0) {
			String k = com.solab.util.AlarmHash.hash(String.format("%s:%s", source == null ? "" : source, msg));
			CachedAlarm ca = buffer.get(k);
			if (ca == null) {
				ca = new CachedAlarm(source, msg);
				buffer.put(k, ca);
			} else {
				ca.update();
			}
		} else {
			for (AlarmChannel c: chans) {
				c.send(msg, source);
			}
		}
	}

	/** Sends an alarm through all channels, to the default users defined for each channel, as long as
	 * the message hasn't been already sent very recently. If the message is sent, the time is recorded
	 * to avoid further sending until the interval has elapsed for each channel. */
	public void sendAlarm(final String msg) {
		sendAlarm(msg, null);
	}

	/** Sends an alarm through all channels, to the default users defined for each channel, regardless
	 * of the last time the same message was sent. */
	public void sendAlarmAlways(final String msg) {
		sendAlarmAlways(msg, null);
	}

	/** Sets up a ScheduledExecutorService if necessary, to buffer alarm messages that are supposed to be
	 * sent always. */
	@PostConstruct
	public void init() {
		if (bufTime > 0) {
			timer = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("jalarms-cached"));
			buffer = new ConcurrentHashMap<>();
			timer.scheduleWithFixedDelay(new Runnable(){
				public void run() {
					sendCachedAlarms();
				}
			}, 30, 30, TimeUnit.SECONDS);
		}
	}

	/** Sends the alarms that have been cached, if the conditions are right. The right conditions are:
	 * The alarm has been received more than once in the period of time specified in the alarmTimeBuffer property,
	 * or the alarm has not been received again in 30 seconds. */
	protected void sendCachedAlarms() {
		long now = System.currentTimeMillis();
		//check queue, send alarms
		Iterator<Map.Entry<String, CachedAlarm>> iter;
		for (iter = buffer.entrySet().iterator(); iter.hasNext();) {
			Map.Entry<String, CachedAlarm> e = iter.next();
			if (e.getValue().times > 1) {
				//Check firstSent against buffer time
				if (now-e.getValue().firstSent >= bufTime) {
					iter.remove();
					for (AlarmChannel c: chans) {
						c.send(String.format("%s (%dx)", e.getValue().msg, e.getValue().times),
							e.getValue().src);
					}
				}
			} else if (now-e.getValue().lastSent >= 29800) {
				//In practice, the scheduler tends to run the task a little under 30s
				iter.remove();
				for (AlarmChannel c: chans) {
					c.send(e.getValue().msg, e.getValue().src);
				}
			}
		}
	}

	/** Shuts down all channels, and the alarm cache. Also sends out any cached alarms. This method can block
	 * the calling thread for some time, if any channels block while they send their pending alarms. */
	@PreDestroy
	public void shutdown() {
		if (timer != null) {
			//There's a timer, so there's also a buffer
			timer.shutdownNow();
			for (Map.Entry<String, CachedAlarm> pend : buffer.entrySet()) {
				for (AlarmChannel c: chans) {
					if (pend.getValue().times > 1) {
						c.send(String.format("%s (%dx)", pend.getValue().msg, pend.getValue().times),
							pend.getValue().src);
					} else {
						c.send(pend.getValue().msg, pend.getValue().src);
					}
				}
			}
		}
		if (cache != null) {
			cache.shutdown();
		}
		for (AlarmChannel c: chans) {
			c.shutdown();
		}
	}

	public String getStatus() {
		return String.format("AlarmSender with %d channels, time buffer %d, cache %s",
			chans.size(), bufTime, cache);
	}

	/** This should be used by implementations of AlarmCache to store the necessary alarm data in the cache. */
	private static class CachedAlarm {
		/** The timestamp of the first time this alarm was sent (in the last batch) */
		long firstSent;
		/** The timestamp of the last time this alarm was sent. */
		long lastSent;
		/** The number of times the alarm has been sent. */
		int times = 1;
		String src;
		String msg;
		private CachedAlarm(String src, String msg) {
			lastSent = System.currentTimeMillis();
			firstSent = lastSent;
			this.src = src;
			this.msg = msg;
		}
		/** Updates the last sent time and increments the number of times the alarm has been sent. */
		private void update() {
			times++;
			lastSent = System.currentTimeMillis();
		}
	}

}
