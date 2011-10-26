package com.solab.alarms

import com.solab.alarms.*

/** This class is used to decorate an AlarmSender with two variants of withAlarm() method.
 *
 * @author Enrique Zamudio
 */
class AlarmSenderDecorator {

	/** Adds two variants of withAlarm() to the target. One variant receives an alarm only and the other receives an alarm and an alarm source;
	 * both receive a closure that is executed and if an exception is thrown then the alarm is sent. */
	static decorate(AlarmSender sender) {
		sender.metaClass.withAlarm << { String alarm, Closure body ->
			try {
				body()
			} catch (Throwable t) {
				sender.sendAlarm(alarm)
				throw t
			}
		} << { String alarm, String src, Closure body ->
			try {
				body()
			} catch (Throwable t) {
				sender.sendAlarm(alarm, src)
				throw t
			}
		}
	}

	/** This is a convenience method; it allows you to define a decorator in a DI context to decorate an AlarmSender at configuration time. */
	AlarmSenderDecorator(AlarmSender target) {
		AlarmSenderDecorator.decorate(target)
	}

}
