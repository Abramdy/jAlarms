package com.solab.alarms.remote;

import javax.annotation.PreDestroy;

/** A very simple remote alarm listener that dispatches HTTP requests.
 *
 * @author Enrique Zamudio
 */
public class HttpAlarmListener extends AbstractAlarmListener {

	private final int port;

	public HttpAlarmListener(int tcpPort) {
		port = tcpPort;
	}

	public void run() {
	}

	@PreDestroy
	public void shutdown() {
		tpool.shutdown();
	}

}

