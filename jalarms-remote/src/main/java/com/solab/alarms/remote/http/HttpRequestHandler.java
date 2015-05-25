package com.solab.alarms.remote.http;

import com.solab.alarms.AlarmSender;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.util.List;
import java.util.Map;

/** The HTTP request handler. Taken from a Netty example.
 *
 * @author Enrique Zamudio
 *         Date: 29/11/11 14:55
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<HttpRequest> {

    public static final String okResp = "OK";
    public static final String emptyResp = "You must send 'alarm' and optionally 'src' in the query string of the URL";
    private final AlarmSender sender;

    public HttpRequestHandler(AlarmSender sender) {
        this.sender = sender;
    }

    public void channelRead0(ChannelHandlerContext ctx, final HttpRequest req) {
        final QueryStringDecoder decoder = new QueryStringDecoder(req.getUri());
        final Map<String, List<String>> params = decoder.parameters();
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
        final byte[] buf = resp.getBytes();
        HttpResponse htresp = new DefaultFullHttpResponse(req.getProtocolVersion(),
                HttpResponseStatus.OK, Unpooled.wrappedBuffer(buf));
        htresp.headers().add(HttpHeaders.Names.CONTENT_TYPE, "text/plain");
        htresp.headers().add(HttpHeaders.Names.CONTENT_LENGTH, buf.length);
        ctx.writeAndFlush(htresp).addListener(ChannelFutureListener.CLOSE);
    }

}
