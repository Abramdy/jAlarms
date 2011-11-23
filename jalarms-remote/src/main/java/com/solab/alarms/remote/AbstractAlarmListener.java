package com.solab.alarms.remote;

import com.solab.alarms.AlarmSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** A base class for remote alarm listeners. Holds a reference to an AlarmSender, and a property to specify if it should autostart. The subclasses
 * must implement the run() method, to start receiving connections and sending the alarms that are requested through them; this method will be called
 * from its own dedicated thread so it can be a simple infinite loop.
 * This class provides a cached ThreadPool so that subclasses can queue tasks to dispatch incoming requests.
 *
 * @author Enrique Zamudio
 */
public abstract class AbstractAlarmListener implements Runnable {

	protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final ExecutorService tpool = Executors.newCachedThreadPool();

	@Resource private AlarmSender sender;
	private boolean autostart = true;

	public void setAlarmSender(AlarmSender value) { sender = value; }
	public AlarmSender getAlarmSender() { return sender; }

	/** Tells the listener whether to autostart. Useful if defined as a component in a DI manager which can call the init() method annotated with @PostConstruct.
	 * default is true. */
	public void setAutostart(boolean flag) { autostart = flag; }

	/** This method can be called by a DI manager. It's annotated as @PostConstruct; if autostart is set to true, then this method calls startListening,
	 * otherwise does nothing. */
	@PostConstruct
	public void init() {
		if (autostart) {
			startListening();
		}
	}

	/** Starts a thread with itself as the target. */
	public void startListening() {
		if (sender == null) {
			throw new IllegalStateException("AlarmSender has not been set, no point in starting server");
		}
		new Thread(this, getClass().getSimpleName()).start();
	}

	/** Command the received to stop listening for incoming requests. Implementations should invoke shutdown on the thread pool as well. */
	public abstract void shutdown();

}
