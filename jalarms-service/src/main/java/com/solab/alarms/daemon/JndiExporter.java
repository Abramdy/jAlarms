package com.solab.alarms.daemon;

import com.solab.alarms.AlarmSender;
import javax.annotation.*;
import javax.naming.*;
import org.slf4j.*;

/** This component is used to bind the AlarmSender to a JNDI path.
 *
 * @author Enrique Zamudio
 */
public class JndiExporter {

	private final String name;
	private final Logger log = LoggerFactory.getLogger(getClass());
	@Resource private AlarmSender bean;

	public JndiExporter(String name) {
		this.name = name;
	}

	@PostConstruct
	public void bindBean() throws javax.naming.NamingException {
		if (name != null && name.length() > 0) {
			Context jndi = new InitialContext();
			jndi = (Context)jndi.lookup("java:");
			jndi.bind(name, bean);
			log.info("Bound AlarmSender to JNDI name java:{}", name);
		}
	}

	@PreDestroy
	public void unbindBean() throws javax.naming.NamingException {
		if (name != null && name.length() > 0) {
			InitialContext jndi = new InitialContext();
			jndi.unbind(name);
			log.info("Unbound AlarmSender from JNDI {}", name);
		}
	}

}
