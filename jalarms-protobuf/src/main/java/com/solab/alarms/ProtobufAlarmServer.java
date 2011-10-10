package com.solab.alarms;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import javax.annotation.Resource;
import org.slf4j.*;
import com.solab.alarms.protobuf.AlarmProtos.Alarm;

/** A TCP server for the Protocol Buffers AlarmSender client.
 * Once configured, the start() method must be invoked on the component
 * so that it starts listening on the TCP port and starts sending the alarms.
 * This components requires a configured AlarmSender to forward the alarms to.
 * Internally, this component uses a cached thread pool to handle the incoming
 * connections. Each connection can send any number of alarms, no more than 5
 * seconds apart. After 5 seconds of inactivity, the connection is closed.
 *
 * @author Enrique Zamudio
 */
public class ProtobufAlarmServer extends Thread {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private final int port;
	private @Resource AlarmSender sender;
	private final ExecutorService tpool = Executors.newCachedThreadPool();

	/** Creates a new instance which listens on the specified TCP port. */
	public ProtobufAlarmServer(int tcpPort) {
		port = tcpPort;
		setName("jalarms-protobuf");
	}

	/** Specified the AlarmSender to forward the alarms to. */
	public void setAlarmSender(AlarmSender value) { sender = value; }

	/** Specifies the maximum number of threads that the internal thread pool
	 * should allow. By default it is unbound. */
	public void setMaxThreads(int value) {
		((ThreadPoolExector)tpool).setMaximumPoolSize(value);
	}

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

	/** This task is used to handle a connection inside the thread pool. It can
	 * read any number of incoming messages, as long as no more than 5 seconds pass
	 * between the last message and the next; after 5 seconds of inactivity, the
	 * connection is closed. */
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
