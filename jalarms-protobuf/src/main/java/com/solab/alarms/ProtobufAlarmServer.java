package com.solab.alarms;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import javax.annotation.Resource;
import org.slf4j.*;
import com.solab.alarms.protobuf.AlarmProtos.Alarm;

public class ProtobufAlarmServer extends Thread {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private final int port;
	private @Resource AlarmSender sender;
	private ExecutorService tpool = Executors.newCachedThreadPool();

	public ProtobufAlarmServer(int tcpPort) {
		port = tcpPort;
		setName("jalarms-protobuf");
	}

	public void setAlarmSender(AlarmSender value) { sender = value; }

	public void run() {
		try {
			ServerSocket server = new ServerSocket(port);
			while (true) {
				Socket sock = server.accept();
				tpool.execute(new AlarmListener(sock));
			}
		} catch (BindException ex) {
			log.error("Someone else already listening on port {}, cannot start jAlarms protobuf listener", ex);
		} catch (IOException ex) {
			log.error("Receiving connection on port {}", port, ex);
		}
	}

	private class AlarmListener implements Runnable {
		private final Socket sock;
		private AlarmListener(Socket s) { sock = s; }
		public void run() {
			try {
				sock.setSoTimeout(5000);
				while (true) {
					Alarm alarm = Alarm.parseDelimitedFrom(sock.getInputStream());
					if (alarm.getAlways()) {
						sender.sendAlarmAlways(alarm.getAlarm(), alarm.hasSource() ? alarm.getSource() : null);
					} else {
						sender.sendAlarm(alarm.getAlarm(), alarm.hasSource() ? alarm.getSource() : null);
					}
				}
			} catch (SocketException ex) {
				if (false/*ex is read timeout*/) {
					log.trace("Closing jAlarms protobuf client {}", sock.getRemoteSocketAddress());
				} else {
					log.error("Receiving alarm messages from {}", sock.getRemoteSocketAddress(), ex);
				}
			} catch (IOException ex) {
				log.error("Receiving alarm messages from {}", sock.getRemoteSocketAddress(), ex);
			} finally {
				try { sock.close(); } catch (IOException ex) {}
			}
		}
	}

}
