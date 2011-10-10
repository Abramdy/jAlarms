package com.solab.alarms;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.*;
import org.slf4j.*;
import com.solab.alarms.protobuf.AlarmProtos.Alarm;

public class ProtobufAlarmClient implements AlarmSender {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private final InetSocketAddress endpoint;
	private int connectTimeout = 1000;
	private long lastWrite;
	private Socket sock;
	private final ExecutorService queue = Executors.newSingleThreadExecutor();

	public ProtobufAlarmClient(String host, int port) {
		endpoint = new InetSocketAddress(host, port);
	}

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

	protected void send(Alarm alarm) {
		queue.execute(new AlarmTask(alarm));
	}

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
