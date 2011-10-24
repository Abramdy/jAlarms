package com.solab.alarms.scala

import scala.collection.JavaConversions._
import com.solab.alarms.scala.bindings._
import com.solab.alarms.AlarmSenderImpl
import com.solab.alarms.channels.TestChannel
import org.junit._

class TestBindings {

	val sender = new AlarmSenderImpl

	@Before
	def init() {
		sender.setAlarmChannels(List(new TestChannel))
	}

	@Test(expected=classOf[IllegalStateException])
	def testWithSource() {
		sender.withAlarm("Alarm with a source", "source") {
			println("doing something...")
			throw new IllegalStateException("with source!")
		}
	}

	@Test(expected=classOf[IllegalStateException])
	def testWithoutSource() {
		sender.withAlarm("Alarm without a source") {
			println("just hanging out...")
			throw new IllegalStateException("no source!")
		}
	}

	@Test
	def testNoAlarm() {
		sender.withAlarm("You should never read this") {
			println("won't fail this time")
		}
	}

}
