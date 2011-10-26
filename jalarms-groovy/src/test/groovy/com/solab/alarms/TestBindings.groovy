package com.solab.alarms

import com.solab.alarms.AlarmSenderImpl
import com.solab.alarms.channels.TestChannel

import org.junit.*

class TestBindings {

	def sender = new AlarmSenderImpl()

	@Before
	void init() {
		sender.alarmChannels = [ new TestChannel() ]
		AlarmSenderDecorator.decorate(sender)
	}

	@Test(expected=IllegalStateException.class)
	void testWithSource() {
		sender.withAlarm("With source", "source") {
			throw new IllegalStateException("with a source")
		}
	}

	@Test(expected=IllegalStateException.class)
	void testWithoutSource() {
		sender.withAlarm("no source") {
			throw new IllegalStateException("no source!!!")
		}
	}

	@Test
	void testNoAlarm() {
		sender.withAlarm("You shouldn't ever read this") {
			println("This code doesn't fail")
		}
	}

}

