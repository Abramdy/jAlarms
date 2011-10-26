package com.solab.alarms.scala

import com.solab.alarms.AlarmSender

object bindings {

	implicit def bindAlarmSender(sender:AlarmSender) = new ScalarmSender(sender)

}

class ScalarmSender(sender:AlarmSender) {

	def withAlarm(alarm:String, source:String)(body: => Unit) {
		try {
			body
		} catch {
			case e =>
				sender.sendAlarm(alarm, source)
				throw e
		}
	}

	def withAlarm(alarm:String)(body: => Unit) {
		try {
			body
		} catch {
			case e =>
				sender sendAlarm alarm
				throw e
		}
	}

}
