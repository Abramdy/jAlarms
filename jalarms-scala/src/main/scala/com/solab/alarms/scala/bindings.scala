package com.solab.alarms.scala

import com.solab.alarms.AlarmSender

/*
The "bindings" object offers an implicit conversion to create a ScalarmSender containing an AlarmSender, so that the "withAlarm" functions
can be easily used within Scala code.
*/
object bindings {

	implicit def bindAlarmSender(sender:AlarmSender) = new ScalarmSender(sender)

}

class ScalarmSender(sender:AlarmSender) {

	def withAlarm(alarm:String, source:String)(body: => Unit) {
		try {
			body
		} catch {
			case e:Exception =>
				sender.sendAlarm(alarm, source)
				throw e
		}
	}

	def withAlarm(alarm:String)(body: => Unit) {
		try {
			body
		} catch {
			case e:Exception =>
				sender sendAlarm alarm
				throw e
		}
	}

}
