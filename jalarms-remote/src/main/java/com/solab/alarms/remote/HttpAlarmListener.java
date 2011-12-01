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
    

    /** Creates a new instance, which will listen on the specified port when started.
     * @param tcpPort the port on which the receiver must listen for incoming HTTP requests. */
	public HttpAlarmListener(int tcpPort) {
		port = tcpPort;
	}

    /** Starts the HTTP server. This method is called from the startListening() method define on the superclass. */
	public void run() {
        bootstrap.setPipelineFactory(this);
        bootstrap.bind(new InetSocketAddress(port));
	}

    /** Shuts down the HTTP server. */
	@PreDestroy
	public void shutdown() {
        //This includes the tpool.shutdown
        bootstrap.releaseExternalResources();
	}

    /** This method is part of the ChannelPipeLineFactory, a Netty interface. */
    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pl = pipeline();
        pl.addLast("decoder", new HttpRequestDecoder());
        pl.addLast("encoder", new HttpResponseEncoder());
        pl.addLast("handler", new HttpRequestHandler(getAlarmSender()));
        return pl;
    }

}

