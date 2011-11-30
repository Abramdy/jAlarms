package com.solab.alarms.remote

import spock.lang.*
import com.solab.alarms.AlarmSenderImpl
import com.solab.alarms.channels.TestChannel
import com.solab.alarms.remote.http.HttpRequestHandler

/** Integration tests for the Http server.
 *
 * @author Enrique Zamudio
 * Date: 30/11/11 15:53
 */
class TestHttpServer extends Specification {

    @Shared AlarmSenderImpl sender = new AlarmSenderImpl(alarmChannels:[ new TestChannel() ])
    @Shared HttpAlarmListener server = new HttpAlarmListener(65001)

    void setupSpec() {
        server.alarmSender = sender
        server.startListening()
    }

    def "empty response"() {
        when:
            URL url = 'http://127.0.0.1:65001/'.toURL()
        then:
            url.text == HttpRequestHandler.emptyResp
    }

    def "send alarms"() {
        expect:
            url.toURL().text == HttpRequestHandler.okResp
        where:
            url << ['http://127.0.0.1:65001/?alarm=Something+something',
                    'http://127.0.0.1:65001/?alarm=Something+else&src=source' ]
    }

    void cleanupSpec() {
        server.shutdown()
        sender.shutdown()
    }

}
