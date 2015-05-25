package com.solab.alarms.remote;

import com.solab.alarms.remote.http.HttpRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

import javax.annotation.PreDestroy;

/** A very simple remote alarm listener that dispatches HTTP requests.
 *
 * @author Enrique Zamudio
 */
public class HttpAlarmListener extends AbstractAlarmListener {

	private final int port;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Channel canal;

    /** Creates a new instance, which will listen on the specified port when started.
     * @param tcpPort the port on which the receiver must listen for incoming HTTP requests. */
	public HttpAlarmListener(int tcpPort) {
		port = tcpPort;
        bossGroup = new NioEventLoopGroup();
	}

    /** Starts the HTTP server. This method is called from the startListening() method defined on the superclass. */
	public void run() {
        try {
            ServerBootstrap sbs = new ServerBootstrap();
            sbs.option(ChannelOption.SO_BACKLOG, 1024);
            sbs.group(bossGroup, workerGroup)
                      .channel(NioServerSocketChannel.class)
                      .childHandler(new ChannelInitializer<SocketChannel>(){
                          public void initChannel(SocketChannel ch) {
                              final ChannelPipeline p = ch.pipeline();
                              p.addLast(new HttpServerCodec(),
                                      new HttpRequestHandler(getAlarmSender()));
                          }
                      });
            canal = sbs.bind(port).sync().channel();
        } catch (InterruptedException ex) {
            log.error("Setting up jAlarms HTTP alarm listener", ex);
        }
	}

    /** Shuts down the HTTP server. */
    @PreDestroy
    public void shutdown() {
        if (canal != null) {
            log.info("Shutting down jAlarms HTTP alarm listener");
            canal.close().syncUninterruptibly();
            canal = null;
        }
    }

}

