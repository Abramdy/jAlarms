package com.solab.alarms;

import javax.annotation.*;
import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.*;
import org.springframework.test.context.junit4.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/rmi-test.xml"})
public class TestRmi {
	@Resource private AlarmSender alarmSender;

	@Test
	public void sendAlarm() {
		alarmSender.sendAlarm("Testing remote AlarmSender - if you're reading this in the server log, it's OK!");
	}

}
