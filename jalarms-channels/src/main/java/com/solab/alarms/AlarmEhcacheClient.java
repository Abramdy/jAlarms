package com.solab.alarms;

import javax.annotation.*;

import org.slf4j.LoggerFactory;

import net.sf.ehcache.*;

/** An alarm cache that uses ehcache to store the data it needs to know if an alarm message should be
 * resent. This is useful in environments where you have several applications which can be using similar
 * alarm channels, since the DefaultAlarmCache is internal to the app and events in two or more apps
 * will cause an alarm to be sent from each app.
 *
 * @author Enrique Zamudio
 *         Date: 02/11/11 14:33
 */
public class AlarmEhcacheClient implements AlarmCache {

	private final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());
	private CacheManager cacheman;
	private Ehcache cache;
	private String configPath = "/jalarms_ehcache.xml";
	private String cacheName = "jalarms";

	/** Sets the path of the config file, located in the classpath. Default is "/jalarms_ehcache.xml". */
	public void setConfigPath(String path) {
		configPath = path;
	}
	public String getConfigPath() { return configPath; }

	/** Sets the name of the cache to use. Default is "jalarms". */
	public void setCacheName(String name) {
		cacheName = name;
	}
	public String getCacheName() { return cacheName; }

	@PostConstruct
	public void init() {
		cacheman = CacheManager.create(getClass().getResource(configPath));
		cache = cacheman.getEhcache("jalarms");
	}

    @Override
    public void store(AlarmChannel channel, String source, String message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean shouldResend(AlarmChannel channel, String source, String message) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void shutdown() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
