package com.solab.alarms.remote.http;

import com.solab.alarms.AlarmSender;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.util.CharsetUtil;

import java.util.List;
import java.util.Map;

/** Manejador de peticiones HTTP.
 *
 * @author Enrique Zamudio
 *         Date: 29/11/11 14:55
 */
public class HttpRequestHandler extends SimpleChannelUpstreamHandler {

    public static final String okResp = "OK";
    public static final String emptyResp = "You must send 'alarm' and optionally 'src' in the query string of the URL";
    private final AlarmSender sender;

    public HttpRequestHandler(AlarmSender sender) {
        this.sender = sender;
    }

    public void messageReceived(ChannelHandlerContext ctx, MessageEvent ev) {
        final HttpRequest req = (HttpRequest)ev.getMessage();
        final QueryStringDecoder decoder = new QueryStringDecoder(req.getUri());
        final Map<String, List<String>> params = decoder.getParameters();
        String alarm = params.get("alarm") != null && params.get("alarm").size() > 0 ? params.get("alarm").get(0) : null;
        String src = params.get("src") != null && params.get("src").size() > 0 ? params.get("src").get(0) : null;
        String resp = okResp;
        if (alarm == null) {
            resp = emptyResp;
        } else if (src == null) {
            sender.sendAlarm(alarm);
        } else {
            sender.sendAlarm(alarm, src);
        }
        HttpResponse htresp = new DefaultHttpResponse(req.getProtocolVersion(), HttpResponseStatus.OK);
        htresp.setHeader(HttpHeaders.Names.CONTENT_TYPE, "text/plain");
        htresp.setContent(ChannelBuffers.copiedBuffer(resp.toCharArray(), CharsetUtil.UTF_8));
        htresp.setHeader(HttpHeaders.Names.CONTENT_LENGTH, htresp.getContent().readableBytes());
        ev.getChannel().write(htresp).addListener(ChannelFutureListener.CLOSE);
    }

}
