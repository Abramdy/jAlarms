package com.solab.alarms.remote

import com.solab.alarms.AlarmSenderImpl
import com.solab.alarms.channels.TestChannel
import java.io.*
import java.net.Socket
import spock.lang.*

/** Unit tests for the TextSocketListener.
 *
 * @author Enrique Zamudio
 */
class TestTextSocket extends Specification {

	@Shared AlarmSenderImpl sender = new AlarmSenderImpl(alarmChannels:[ new TestChannel() ])
	@Shared TextSocketListener server = new TextSocketListener(65000)

	void setupSpec() {
		server.alarmSender = sender
		server.startListening()
		Thread.sleep(500)
	}

	def "send alarms"() {
		given:
			Socket sock = new Socket("127.0.0.1", 65000)
			def pout = new PrintStream(sock.outputStream)
			def sin = new BufferedReader(new InputStreamReader(sock.inputStream))
		when:
			pout.println(alarm)
			pout.flush()
		then:
			sin.readLine() == "OK"
		cleanup:
			sock.close()
		where:
			alarm << ["alarm without source", "source::alarm with source"]
	}

	void cleanupSpec() {
		server.shutdown()
		sender.shutdown()
	}

}
