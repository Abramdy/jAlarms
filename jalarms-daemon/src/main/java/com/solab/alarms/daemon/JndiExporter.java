package com.solab.alarms.daemon;

import com.solab.alarms.AlarmSender;
import javax.annotation.*;
import org.springframework.jndi.JndiTemplate;

/** This component is used to bind the AlarmSender to a JNDI path.
 *
 * @author Enrique Zamudio
 */
public class JndiExporter {
	private String path;
	private final JndiTemplate jndi = new JndiTemplate();
	@Resource private AlarmSender bean;

	public JndiExporter(String jndiPath) {
		path = jndiPath;
	}

	@PostConstruct
	public void bindBean() throws javax.naming.NamingException {
		if (path != null && path.length() > 0) {
			jndi.bind(path, bean);
		}
	}

	@PreDestroy
	public void unbindBean() throws javax.naming.NamingException {
		if (path != null && path.length() > 0) {
			jndi.unbind(path);
		}
	}

}
