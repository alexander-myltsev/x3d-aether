//	---------------------------------------------------------------------------
//	jWebSocket - Copyright (c) 2010 jwebsocket.org
//	---------------------------------------------------------------------------
//	This program is free software; you can redistribute it and/or modify it
//	under the terms of the GNU Lesser General Public License as published by the
//	Free Software Foundation; either version 3 of the License, or (at your
//	option) any later version.
//	This program is distributed in the hope that it will be useful, but WITHOUT
//	ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//	FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//	more details.
//	You should have received a copy of the GNU Lesser General Public License along
//	with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
//	---------------------------------------------------------------------------
package org.jwebsocket.netty.engines;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javolution.util.FastMap;
import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChildChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.WriteCompletionEvent;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrame;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrameDecoder;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrameEncoder;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.util.CharsetUtil;
import org.jwebsocket.api.EngineConfiguration;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.config.JWebSocketCommonConstants;
import org.jwebsocket.kit.CloseReason;
import org.jwebsocket.kit.RawPacket;
import org.jwebsocket.kit.RequestHeader;
import org.jwebsocket.kit.WebSocketRuntimeException;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.netty.connectors.NettyConnector;
import org.jwebsocket.netty.http.HttpHeaders;

/**
 * Handler class for the <tt>NettyEngine</tt> that recieves the events based on
 * event types and notifies the client connectors. This handler also handles the
 * initial handshaking for WebSocket connection with a appropriate hand shake
 * response. This handler is created for each new connection channel.
 * <p>
 * Once the handshaking is successful after sending the handshake {@code
 * HttpResponse} it replaces the {@code HttpRequestDecoder} and {@code
 * HttpResponseEncoder} from the channel pipeline with {@code
 * WebSocketFrameDecoder} as WebSocket frame data decoder and {@code
 * WebSocketFrameEncoder} as WebSocket frame data encoder. Also it starts the
 * <tt>NettyConnector</tt>.
 * </p>
 * 
 * @author <a href="http://www.purans.net/">Puran Singh</a>
 * @version $Id: NettyEngineHandler.java 613 2010-07-01 07:13:29Z mailtopuran@gmail.com $
 */
public class NettyEngineHandler extends SimpleChannelUpstreamHandler {

    private static Logger log = Logging.getLogger(NettyEngineHandler.class);

    private NettyEngine engine = null;

    private WebSocketConnector connector = null;

    private ChannelHandlerContext context = null;

    private static final ChannelGroup channels = new DefaultChannelGroup();

    private static final String CONTENT_LENGTH = "Content-Length";

    private static final String ARGS = "args";
    private static final String ORIGIN = "origin";
    private static final String LOCATION = "location";
    private static final String PATH = "path";
    private static final String SEARCH_STRING = "searchString";
    private static final String HOST = "host";

    public NettyEngineHandler(NettyEngine aEngine) {
        this.engine = aEngine;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void channelBound(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        this.context = ctx;
        super.channelBound(ctx, e);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        this.context = ctx;
        super.channelClosed(ctx, e);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        this.context = ctx;
        super.channelConnected(ctx, e);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Channel is disconnected");
        }
        // remove the channel
        channels.remove(e.getChannel());

        this.context = ctx;
        super.channelDisconnected(ctx, e);
        engine.connectorStopped(connector, CloseReason.CLIENT);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void channelInterestChanged(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        this.context = ctx;
        super.channelInterestChanged(ctx, e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        this.context = ctx;
        super.channelOpen(ctx, e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void channelUnbound(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        this.context = ctx;
        super.channelUnbound(ctx, e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void childChannelClosed(ChannelHandlerContext ctx, ChildChannelStateEvent e) throws Exception {
        this.context = ctx;
        super.childChannelClosed(ctx, e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void childChannelOpen(ChannelHandlerContext ctx, ChildChannelStateEvent e) throws Exception {
        this.context = ctx;
        super.childChannelOpen(ctx, e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        e.getCause().printStackTrace();
        this.context = ctx;
        if (log.isDebugEnabled()) {
            log.debug("Channel is disconnected:" + e.getCause().getLocalizedMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent arg1) throws Exception {
        this.context = ctx;
        super.handleUpstream(ctx, arg1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        this.context = ctx;
        if (log.isDebugEnabled()) {
            log.debug("message received in the engine handler");
        }
        Object msg = e.getMessage();
        if (msg instanceof HttpRequest) {
            handleHttpRequest(ctx, (HttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    /**
     * private method that sends the handshake response for WebSocket connection
     * 
     * @param ctx the channel context
     * @param req http request object
     * @param res http response object
     */
    private void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
        // Generate an error page if response status code is not OK (200).
        if (res.getStatus().getCode() != 200) {
            res.setContent(ChannelBuffers.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8));
            setContentLength(res, res.getContent().readableBytes());
        }

        // Send the response and close the connection if necessary.
        ChannelFuture f = ctx.getChannel().write(res);
        if (!isKeepAlive(req) || res.getStatus().getCode() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * Check if the request header has Keep-Alive
     * 
     * @param req the http request object
     * @return {@code true} if keep-alive is set in the header {@code false}
     *         otherwise
     */
    private boolean isKeepAlive(HttpRequest req) {
        String keepAlive = req.getHeader(HttpHeaders.Values.KEEP_ALIVE);
        if (keepAlive != null && keepAlive.length() > 0) {
            return true;
        } else {
            // TODO: Keep-Alive value is like 'timeout=15, max=500'
            return false;
        }
    }

    /**
     * Set the content length in the response
     * 
     * @param res the http response object
     * @param readableBytes the length of the bytes
     */
    private void setContentLength(HttpResponse res, int readableBytes) {
        res.setHeader(CONTENT_LENGTH, readableBytes);
    }

    /**
     * private method that handles the web socket frame data, this method is
     * used only after the WebSocket connection is established.
     * 
     * @param ctx the channel handler context
     * @param msg the web socket frame data
     */
    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame msg) throws WebSocketRuntimeException {
        String textData = "";
        if (msg.isBinary()) {
            // TODO: handle binary data
        } else if (msg.isText()) {
            textData = msg.getTextData();
        } else {
            throw new WebSocketRuntimeException("Frame Doesn't contain any type of data");
        }
        engine.processPacket(connector, new RawPacket(textData));
    }

    /**
     * Handles the initial HTTP request for handshaking if the http request
     * contains Upgrade header value as WebSocket then this method sends the
     * handshake response and also fires the events on client connector.
     * 
     * @param ctx the channel handler context
     * @param req  the request message
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req) {
        // Allow only GET methods.
        if (req.getMethod() != HttpMethod.GET) {
            sendHttpResponse(ctx, req, new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
            return;
        }
        // Serve the WebSocket handshake request.
        if (HttpHeaders.Values.UPGRADE.equalsIgnoreCase(req.getHeader(HttpHeaders.Names.CONNECTION)) && 
                HttpHeaders.Values.WEBSOCKET.equalsIgnoreCase(req.getHeader(HttpHeaders.Names.UPGRADE))) {
            // Create the WebSocket handshake response.
            HttpResponse response = null;
            try {
                response = constructHandShakeResponse(req, ctx);
            } catch (NoSuchAlgorithmException e) {
                // better to close the channel
                log.debug("Channel is disconnected");
                ctx.getChannel().close();
            }

            // write the response
            ctx.getChannel().write(response);

            channels.add(ctx.getChannel());

            // since handshaking is done, replace the encoder/decoder with
            // web socket data frame encoder/decoder
            ChannelPipeline p = ctx.getChannel().getPipeline();
            p.remove("aggregator");
            EngineConfiguration config = engine.getConfiguration();
            if (config == null || config.getMaxFramesize() == 0) {
                p.replace("decoder", "jwsdecoder", new WebSocketFrameDecoder(JWebSocketCommonConstants.DEFAULT_MAX_FRAME_SIZE));
            } else {
                p.replace("decoder", "jwsdecoder", new WebSocketFrameDecoder(config.getMaxFramesize()));
            }
            p.replace("encoder", "jwsencoder", new WebSocketFrameEncoder());
            
            //if the WebSocket connection URI is wss then start SSL TLS handshaking
            if (req.getUri().startsWith("wss:")) {
                // Get the SslHandler in the current pipeline.
                final SslHandler sslHandler = ctx.getPipeline().get(SslHandler.class);
                // Get notified when SSL handshake is done.
                ChannelFuture handshakeFuture = sslHandler.handshake();
                handshakeFuture.addListener(new SecureWebSocketConnectionListener(sslHandler));
            }
            // initialize the connector
            connector = initializeConnector(ctx, req);

            return;
        }

        // Send an error page otherwise.
        sendHttpResponse(ctx, req, new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
    }

    /**
     * Constructs the <tt>HttpResponse</tt> object for the handshake response
     * 
     * @param req the http request object
     * @param ctx the channel handler context
     * @return the http handshake response
     * @throws NoSuchAlgorithmException
     */
    private HttpResponse constructHandShakeResponse(HttpRequest req, ChannelHandlerContext ctx) throws NoSuchAlgorithmException {
        HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, new HttpResponseStatus(101, "Web Socket Protocol Handshake"));
        res.addHeader(HttpHeaders.Names.UPGRADE, HttpHeaders.Values.WEBSOCKET);
        res.addHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.UPGRADE);

        // Fill in the headers and contents depending on handshake method.
        if (req.containsHeader(HttpHeaders.Names.SEC_WEBSOCKET_KEY1) && req.containsHeader(HttpHeaders.Names.SEC_WEBSOCKET_KEY2)) {
            // New handshake method with a challenge:
            res.addHeader(HttpHeaders.Names.SEC_WEBSOCKET_ORIGIN, req.getHeader(HttpHeaders.Names.ORIGIN));
            res.addHeader(HttpHeaders.Names.SEC_WEBSOCKET_LOCATION, getWebSocketLocation(req));
            String protocol = req.getHeader(HttpHeaders.Names.SEC_WEBSOCKET_PROTOCOL);
            if (protocol != null) {
                res.addHeader(HttpHeaders.Names.SEC_WEBSOCKET_PROTOCOL, protocol);
            }

            // Calculate the answer of the challenge.
            String key1 = req.getHeader(HttpHeaders.Names.SEC_WEBSOCKET_KEY1);
            String key2 = req.getHeader(HttpHeaders.Names.SEC_WEBSOCKET_KEY2);
            int a = (int) (Long.parseLong(key1.replaceAll("[^0-9]", "")) / key1.replaceAll("[^ ]", "").length());
            int b = (int) (Long.parseLong(key2.replaceAll("[^0-9]", "")) / key2.replaceAll("[^ ]", "").length());
            long c = req.getContent().readLong();
            ChannelBuffer input = ChannelBuffers.buffer(16);
            input.writeInt(a);
            input.writeInt(b);
            input.writeLong(c);
            ChannelBuffer output = ChannelBuffers.wrappedBuffer(MessageDigest.getInstance("MD5").digest(input.array()));
            res.setContent(output);
        } else {
            // Old handshake method with no challenge:
            res.addHeader(HttpHeaders.Names.WEBSOCKET_ORIGIN, req.getHeader(HttpHeaders.Names.ORIGIN));
            res.addHeader(HttpHeaders.Names.WEBSOCKET_LOCATION, getWebSocketLocation(req));
            String protocol = req.getHeader(HttpHeaders.Names.WEBSOCKET_PROTOCOL);
            if (protocol != null) {
                res.addHeader(HttpHeaders.Names.WEBSOCKET_PROTOCOL, protocol);
            }
        }
        return res;

    }

    /**
     * Initialize the {@code NettyConnector} after initial handshaking is
     * successfull.
     * 
     * @param ctx the channel handler context
     * @param req the http request object
     */
    private WebSocketConnector initializeConnector(ChannelHandlerContext ctx, HttpRequest req) {

        RequestHeader header = getRequestHeader(req);
        int lSessionTimeout = header.getTimeout(JWebSocketCommonConstants.DEFAULT_TIMEOUT);
        if (lSessionTimeout > 0) {
            ctx.getChannel().getConfig().setConnectTimeoutMillis(lSessionTimeout);
        }
        // create connector
        WebSocketConnector theConnector = new NettyConnector(engine, this);
        theConnector.setHeader(header);

        engine.getConnectors().put(theConnector.getId(), theConnector);
        theConnector.startConnector();
        // allow descendant classes to handle connector started event
        engine.connectorStarted(theConnector);
        return theConnector;

    }

    /**
     * Construct the request header to save it in the connector
     * 
     * @param req the http request header
     * @return the request header
     */
    private RequestHeader getRequestHeader(HttpRequest req) {
        RequestHeader header = new RequestHeader();
        FastMap<String, String> args = new FastMap<String, String>();
        String searchString = "";
        String path = req.getUri();

        // isolate search string
        int pos = path.indexOf(JWebSocketCommonConstants.PATHARG_SEPARATOR);
        if (pos >= 0) {
            searchString = path.substring(pos + 1);
            if (searchString.length() > 0) {
                String[] lArgs = searchString.split(JWebSocketCommonConstants.ARGARG_SEPARATOR);
                for (int i = 0; i < lArgs.length; i++) {
                    String[] lKeyValuePair = lArgs[i].split(JWebSocketCommonConstants.KEYVAL_SEPARATOR, 2);
                    if (lKeyValuePair.length == 2) {
                        args.put(lKeyValuePair[0], lKeyValuePair[1]);
                        if (log.isDebugEnabled()) {
                            log.debug("arg" + i + ": " + lKeyValuePair[0] + "=" + lKeyValuePair[1]);
                        }
                    }
                }
            }
        }
        // set default sub protocol if none passed
        if (args.get("prot") == null) {
            args.put("prot", JWebSocketCommonConstants.SUB_PROT_DEFAULT);
        }
        header.put(ARGS, args);
        header.put(ORIGIN, req.getHeader(HttpHeaders.Names.ORIGIN));
        header.put(LOCATION, getWebSocketLocation(req));
        header.put(PATH, req.getUri());

        header.put(SEARCH_STRING, searchString);
        header.put(HOST, req.getHeader(HttpHeaders.Names.HOST));
        return header;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent e) throws Exception {
        super.writeComplete(ctx, e);
    }

    /**
     * Returns the web socket location URL
     * 
     * @param req the http request object
     * @return the location url string
     */
    private String getWebSocketLocation(HttpRequest req) {
        //TODO: fix this URL for wss: (secure)
        String location = "ws://" + req.getHeader(HttpHeaders.Names.HOST) + req.getUri();
        return location;
    }

    /**
     * Returns the channel context
     * 
     * @return the channel context
     */
    public ChannelHandlerContext getChannelHandlerContext() {
        return context;
    }

    /**
     * Listener class for SSL TLS handshake completion.
     */
    private static final class SecureWebSocketConnectionListener implements ChannelFutureListener {

        private final SslHandler sslHandler;

        SecureWebSocketConnectionListener(SslHandler sslHandler) {
            this.sslHandler = sslHandler;
        }

        public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
                // that means SSL handshaking is done.
            } else {
                future.getChannel().close();
            }
        }
    }
}
