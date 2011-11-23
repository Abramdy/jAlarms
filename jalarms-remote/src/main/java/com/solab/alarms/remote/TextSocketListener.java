package com.solab.alarms.remote;

import java.io.*;
import java.net.*;

/** A very simple remote alarm listener. It reads a text line from a socket and sends an alarm with that message.
 *
 * @author Enrique Zamudio
 */
public class TextSocketListener extends AbstractAlarmListener {

	private final int port;

	public TextSocketListener(int tcpPort) {
		port = tcpPort;
	}

	public void run() {
		try {
			ServerSocket server = new ServerSocket(port);
			while (true) {
				Socket sock = server.accept();
			}
		} catch (IOException ex) {
		}
	}

}
