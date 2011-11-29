package com.solab.alarms.remote;

import com.solab.alarms.remote.http.HttpRequestHandler;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import static org.jboss.netty.channel.Channels.pipeline;

/** A very simple remote alarm listener that dispatches HTTP requests.
 *
 * @author Enrique Zamudio
 */
public class HttpAlarmListener extends AbstractAlarmListener implements ChannelPipelineFactory {

	private final int port;
    private final ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
            Executors.newCachedThreadPool(), tpool));
    

	public HttpAlarmListener(int tcpPort) {
		port = tcpPort;
	}

	public void run() {
        bootstrap.setPipelineFactory(this);
        bootstrap.bind(new InetSocketAddress(port));
	}

	@PreDestroy
	public void shutdown() {
        //This includes the tpool.shutdown
        bootstrap.releaseExternalResources();
	}

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pl = pipeline();
        pl.addLast("decoder", new HttpRequestDecoder());
        pl.addLast("encoder", new HttpResponseEncoder());
        pl.addLast("handler", new HttpRequestHandler(getAlarmSender()));
        return pl;
    }
}

