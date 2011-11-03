package com.solab.alarms

import org.slf4j.LoggerFactory
import org.junit.*

/**
 * Blabla.
 *
 * @author Enrique Zamudio
 *         Date: 02/11/11 14:58
 */
class TestEhcacheClient implements UnitTestChannel.ChanDelegate {
    private AlarmEhcacheClient cache
    final org.slf4j.Logger log = LoggerFactory.getLogger(getClass())
    AlarmSenderImpl sender = new AlarmSenderImpl()
    protected UnitTestChannel chan1 = new UnitTestChannel()
    protected UnitTestChannel chan2 = new UnitTestChannel()
    int c1t = 1000
    int c2t = 1500
    int w1 = 600

    protected AlarmCache createCache() {
        c1t = 4000
        c2t = 6000
        w1 = 2400
        try {
            cache = new AlarmEhcacheClient()
            cache.configPath='ehcache_test.xml'
            cache.cacheName='jalarms_test'
            cache.init()
        } catch (IllegalArgumentException ex) {
            log.warn('No ehcache config available, skipping test')
            cache = null
        }
        cache
    }

    @Before
    void setup() {
        sender.alarmCache=createCache()
        chan1.resend = c1t
        chan2.resend = c2t
        chan1.delegate = this
        chan2.delegate = this
        sender.alarmChannels=[chan1, chan2]
    }

    @Test
    void testCache() {
        if (cache == null) {
            log.info("Skipping cache test");
            return;
        }
        log.info("${chan1.name} resends every ${chan1.resend} millis, ${chan2.name} resends every ${chan2.resend} millis")

        //First, check that msg1 is sent through both channels
        log.info("Sending msg1 which should be sent immediately")
        chan1.prepare()
        chan2.prepare()
        sender.sendAlarm("msg1", "src1")
        sender.sendAlarm("msg1", "src2")
        sender.sendAlarm("msg1")
        sender.sendAlarmAlways("nochan")
        chan2.waitForSend()
        long ls1 = chan1.lastSent
        long ls2 = chan2.lastSent
        assert ls1 - chan1.stamp > 0 && ls1 - chan1.stamp < 1000
        assert ls2 - chan2.stamp > 0 && ls2 - chan2.stamp < 1000

        //Wait
        log.info("waiting #1: {} millis {}", w1, String.format("%TT", new Date()))
        Thread.sleep(w1)
        //msg1 should be ignored by both channels
        log.info("Both channels ignore msg1 {}", String.format("%TT", new Date()))
        chan1.prepare()
        chan2.prepare()
        sender.sendAlarm("msg1", "src1")
        sender.sendAlarm("msg1", "src2")
        sender.sendAlarm("msg1")
        Thread.sleep(50)
        assert !chan1.sent.get() && !chan2.sent.get()
        assert chan1.lastSent == ls1 && chan2.lastSent == ls2

        //Send using 'always', should be sent
        log.info("Sending using 'always', should be sent right away {}", String.format("%TT", new Date()))
        chan1.prepare()
        chan2.prepare()
        //By now time is about w1+50
        sender.sendAlarmAlways("always")
        chan2.waitForSend()
        assert ls1 < chan1.lastSent && ls2 < chan2.lastSent

        //Wait
        log.info("waiting #2: {} millis {}", w1, String.format("%TT", new Date()))
        Thread.sleep(w1)
        log.info("msg1 should be sent through ${chan1.name}, ignored by ${chan2.name}")
        ls2 = chan2.lastSent
        chan1.prepare()
        chan2.prepare()
        sender.sendAlarm("msg1", "src1")
        sender.sendAlarm("msg1", "src2")
        sender.sendAlarm("msg1")
        chan1.waitForSend()
        ls1 = chan1.lastSent
        assert ls1 - chan1.stamp >= 0 && ls1 - chan1.stamp < 1000
        //chan2 should have not sent anything
        assert !chan2.sent.get() && chan2.lastSent == ls2

        //Check that msg2 is sent through both channels
        log.info("Sending msg2 {}", String.format("%TT", new Date()))
        chan1.prepare()
        chan2.prepare()
        sender.sendAlarm("msg2", "src1")
        sender.sendAlarm("msg2", "src2")
        chan2.waitForSend()
        ls1 = chan1.lastSent
        ls2 = chan2.lastSent
        assert ls1 - chan1.stamp >= 0 && ls1 - chan1.stamp < 1000
        assert ls2 - chan2.stamp >= 0 && ls2 - chan2.stamp < 1000

        //Wait
        log.info("waiting #3: {} millis {}", w1, String.format("%TT", new Date()))
        Thread.sleep(w1)
        log.info("msg1 should be sent through ${chan2.name}, ignored by ${chan1.name}")
        chan1.prepare()
        chan2.prepare()
        sender.sendAlarm("msg1", "src1")
        sender.sendAlarm("msg1", "src2")
        sender.sendAlarm("msg1")
        chan2.waitForSend()
        ls2 = chan2.lastSent
        assert ls2 - chan2.stamp >= 0 && ls2 - chan1.stamp < 1000
        //chan2 should have not sent anything
        assert !chan1.sent.get() && chan1.lastSent == ls1

        //msg2 should be ignored by both channels
        log.info("Testing that msg2 is ignored by both channels {}", String.format("%TT", new Date()))
        ls1 = chan1.lastSent
        ls2 = chan2.lastSent
        chan1.prepare()
        chan2.prepare()
        //Time here is 2*w2 +100
        sender.sendAlarm("msg2", "src1")
        sender.sendAlarm("msg2", "src2")
        Thread.sleep(50)
        assert !chan1.sent.get() && !chan2.sent.get()
        assert chan1.lastSent == ls1 && chan2.lastSent == ls2

        //Wait
        log.info("waiting #4: {} millis {}", w1, String.format("%TT", new Date()))
        Thread.sleep(w1)

        log.info("msg1 should be sent through ${chan1.name}, ignored by ${chan2.name}")
        chan1.prepare()
        chan2.prepare()
        sender.sendAlarm("msg1", "src1")
        sender.sendAlarm("msg1", "src2")
        sender.sendAlarm("msg1")
        chan1.waitForSend()
        ls1 = chan1.lastSent
        assert ls1 - chan1.stamp >= 0 && ls1 - chan1.stamp < 1000
        //chan2 should have not sent anything
        assert !chan2.sent.get() && chan2.lastSent == ls2

        log.info("msg2 should be sent through ${chan1.name}, ignored by ${chan2.name}")
        chan1.prepare()
        chan2.prepare()
        ls2 = chan2.lastSent
        sender.sendAlarm("msg2", "src1")
        sender.sendAlarm("msg2", "src2")
        chan1.waitForSend()
        ls1 = chan1.lastSent
        assert ls1 - chan1.stamp >= 0 && ls1 - chan1.stamp < 1000
        //chan2 should have not sent anything
        assert !chan2.sent.get() && chan2.lastSent == ls2

        //Wait
        log.info("waiting #5: {} millis {}", w1, String.format("%TT", new Date()))
        Thread.sleep(w1)
        log.info("Both channels ignore msg1 {}", String.format("%TT", new Date()))
        chan1.prepare()
        chan2.prepare()
        sender.sendAlarm("msg1", "src1")
        sender.sendAlarm("msg1", "src2")
        sender.sendAlarm("msg1")
        Thread.sleep(50)
        assert !chan1.sent.get() && !chan2.sent.get()
        assert chan1.lastSent == ls1 && chan2.lastSent == ls2

        log.info("msg2 should be sent through ${chan2.name}, ignored by ${chan1.name}")
        chan1.prepare()
        chan2.prepare()
        ls1 = chan1.lastSent
        sender.sendAlarm("msg2", "src1")
        sender.sendAlarm("msg2", "src2")
        chan2.waitForSend()
        ls2 = chan2.lastSent
        assert ls2 - chan2.stamp >= 0 && ls2 - chan1.stamp < 1000
        //chan2 should have not sent anything
        assert !chan1.sent.get() && chan1.lastSent == ls1

        //Wait
        log.info("waiting #6: {} millis {}", w1, String.format("%TT", new Date()))
        Thread.sleep(w1)
        log.info("Sending msg1 through both")
        chan1.prepare()
        chan2.prepare()
        sender.sendAlarm("msg1", "src1")
        sender.sendAlarm("msg1", "src2")
        sender.sendAlarm("msg1")
        sender.sendAlarmAlways("nochan")
        chan2.waitForSend()
        ls1 = chan1.lastSent
        ls2 = chan2.lastSent
        assert ls1 - chan1.stamp > 0 && ls1 - chan1.stamp < 1000
        assert ls2 - chan2.stamp > 0 && ls2 - chan2.stamp < 1000
        log.info("msg2 should be sent through ${chan1.name}, ignored by ${chan2.name}")
        chan1.prepare()
        chan2.prepare()
        ls2 = chan2.lastSent
        sender.sendAlarm("msg2", "src1")
        sender.sendAlarm("msg2", "src2")
        chan1.waitForSend()
        ls1 = chan1.lastSent
        assert ls1 - chan1.stamp >= 0 && ls1 - chan1.stamp < 1000
        //chan2 should have not sent anything
        assert !chan2.sent.get() && chan2.lastSent == ls2
    }

    @Override
    void alarmReceived(String msg, long when) {
        //nothing is needed here
    }

    @After
    void shutdown() {
        cache?.shutdown()
    }

}
