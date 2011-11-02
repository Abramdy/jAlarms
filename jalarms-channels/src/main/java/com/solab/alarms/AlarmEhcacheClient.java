package com.solab.alarms;

/** An alarm cache that uses ehcache to store the data it needs to know if an alarm message should be
 * resent. This is useful in environments where you have several applications which can be using similar
 * alarm channels, since the DefaultAlarmCache is internal to the app and events in two or more apps
 * will cause an alarm to be sent from each app.
 *
 * @author Enrique Zamudio
 *         Date: 02/11/11 14:33
 */
public class AlarmEhcacheClient implements AlarmCache {
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
