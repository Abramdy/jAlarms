package com.solab.alarms;

import javax.annotation.*;

import com.solab.util.AlarmHash;
import org.slf4j.LoggerFactory;
import net.sf.ehcache.*;

import java.net.URL;

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
    private int defint = 120;

    /** Sets the default resend interval, for storing alarms unrelated to a specific channel,
     * in seconds. Default is 2 minutes.
     * @param value The number of seconds after which alarms with no specific channel expire. */
    public void setDefaultInterval(int value) {
        defint = value;
    }
    public int getDefaultInterval() {
        return defint;
    }

	/** Sets the path of the config file, located in the classpath. Default is "/jalarms_ehcache.xml".
     * @param path The absolute path inside the classpath for the ehcache config. */
	public void setConfigPath(String path) {
		configPath = path;
	}
	public String getConfigPath() { return configPath; }

	/** Sets the name of the cache to use. Default is "jalarms".
     * @param name The name of the cache to use. */
	public void setCacheName(String name) {
		cacheName = name;
	}
	public String getCacheName() { return cacheName; }

    /** Creates an ehcache CacheManager with the specified config file, and gets the cache with the cacheName from it. */
	@PostConstruct
	public void init() {
        URL url = getClass().getClassLoader().getResource(getConfigPath());
        if (url == null) {
        	url = Thread.currentThread().getContextClassLoader().getResource(getConfigPath());
        	if (url == null) {
        		url = ClassLoader.getSystemResource(getConfigPath());
        		if (url == null) {
        			throw new IllegalArgumentException(String.format("AlarmEhcacheClient couldn't find %s anywhere", getConfigPath()));
        		}
        	}
        }
		cacheman = CacheManager.create(url);
		cache = cacheman.getEhcache(getCacheName());
	}

    @Override
    public void store(AlarmChannel channel, String source, String message) {
        if (cache == null) {
            synchronized (this) {
                if (cache == null) {
                    init();
                }
            }
        }
        String k = channel == null ? String.format("jalarms:ALL:%s:%s", source == null ? "" : source,
            AlarmHash.hash(message)): String.format("jalarms:chan%d:%s:%s", channel.hashCode(),
                source == null ? "" : source, AlarmHash.hash(message));
        //We don't care about the actual value, just that the key exists
        int secs = channel == null ? defint : (channel.getMinResendInterval() / 1000);
        cache.put(new Element(k, "y", false, secs, secs));
    }

    @Override
    public boolean shouldResend(AlarmChannel channel, String source, String message) {
        if (cache == null) {
            return true;
        }
        String k = channel == null ? String.format("jalarms:ALL:%s:%s", source == null ? "" : source,
            AlarmHash.hash(message)): String.format("jalarms:chan%d:%s:%s", channel.hashCode(),
                source == null ? "" : source, AlarmHash.hash(message));
        //If the entry exists, don't resend
        try {
            return cache.get(k) == null;
        } catch (CacheException ex) {
            log.error("jAlarms Retrieving key {} from ehcache", k, ex.getCause() == null ? ex : ex.getCause());
        }
        return true;
    }

    /** Shuts down the ehcache CacheManager. */
    @Override
    public void shutdown() {
        cacheman.shutdown();
    }

}
