package com.solab.alarms

import org.junit.*
import com.solab.alarms.protobuf.AlarmProtos.Alarm

class TestProtobuf {

	@Test
	void testProtobuf() {
		def bout = new ByteArrayOutputStream()
		Alarm.newBuilder().setAlarm('Test 1').setAlways(false).build().writeDelimitedTo(bout)
		def bin = new ByteArrayInputStream(bout.toByteArray())
		println "El mensaje mide ${bout.toByteArray().length} bytes"
		Alarm parsed = Alarm.parseDelimitedFrom(bin)
		assert parsed != null
		assert parsed.alarm=='Test 1'
		assert !parsed.hasSource()
		assert parsed.hasAlways() && !parsed.always
	}

}
