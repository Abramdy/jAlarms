package com.solab.alarms;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.*;
import org.slf4j.*;
import com.solab.alarms.protobuf.AlarmProtos.Alarm;

/** An AlarmSender implementation which connects to a remote AlarmSender using
 * Protocol Buffers.
 *
 * @author Enrique Zamudio
 */
public class ProtobufAlarmClient implements AlarmSender {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private final InetSocketAddress endpoint;
	private int connectTimeout = 1000;
	private long lastWrite;
	private Socket sock;
	private final ExecutorService queue = Executors.newSingleThreadExecutor();

	/** Creates a new AlarmSender which will connect to a ProtobufAlarmSender on the
	specified host and TCP port. */
	public ProtobufAlarmClient(String host, int port) {
		endpoint = new InetSocketAddress(host, port);
	}

	/** Sets the connection timeout. Default value is one second; should be set to
	 * something low, since the server should be on a local network. */
	public void setConnectTimeout(int value) {
		connectTimeout = value;
	}

	public void sendAlarm(final String msg, final String source) {
		send(Alarm.newBuilder().setAlarm(msg).setSource(source).setAlways(false).build());
	}

	public void sendAlarmAlways(final String msg, final String source) {
		send(Alarm.newBuilder().setAlarm(msg).setSource(source).setAlways(true).build());
	}

	public void sendAlarm(final String msg) {
		send(Alarm.newBuilder().setAlarm(msg).setAlways(false).build());
	}

	public void sendAlarmAlways(final String msg) {
		send(Alarm.newBuilder().setAlarm(msg).setAlways(true).build());
	}

	/** This is the method invoked by interface methods. Puts the call on a queue.
	 * @param alarm An Alarm message compiled from a .proto file. */
	protected void send(Alarm alarm) {
		queue.execute(new AlarmTask(alarm));
	}

	/** This is the private task that actually connects to the server to send the
	 * alarm message. It keeps the socket open for up to 5 seconds, so that when
	 * several alarms are sent consecutively, they all use the same connection. */
	private final class AlarmTask implements Runnable {
		private final Alarm alarm;
		private AlarmTask(Alarm a) { alarm = a; }
		public void run() {
			try {
				if (sock == null || !sock.isConnected() || System.currentTimeMillis() - lastWrite > 5000) {
					if (sock != null) sock.close();
					Socket sock = new Socket();
					sock.connect(endpoint, connectTimeout);
				}
				alarm.writeDelimitedTo(sock.getOutputStream());
			} catch (IOException ex) {
				log.error("Connecting to remote AlarmSender {}", endpoint, ex);
			}
		}
	}

}
