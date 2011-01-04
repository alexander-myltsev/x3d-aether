//	---------------------------------------------------------------------------
//	jWebSocket - Copyright (c) 2010
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

import javax.net.ssl.SSLEngine;

import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ServerChannel;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jwebsocket.netty.http.HttpRequestDecoder;

/**
 * Creates a channel pipeline to handle the incoming requests and outgoing
 * responses.
 * <p>
 * When a {@linkplain ServerChannel server-side channel} accepts a new incoming
 * connection, a new child channel is created for each newly accepted
 * connection. A new child channel uses a new {@link ChannelPipeline}, which is
 * created by the {@link ChannelPipelineFactory} specified in the server-side
 * channel's {@link ChannelConfig#getPipelineFactory() "pipelineFactory"}
 * option.
 * </p>
 * 
 * @author Puran Singh
 * @version $Id: NettyEnginePipeLineFactory.java 613 2010-07-01 07:13:29Z mailtopuran@gmail.com $
 */
public class NettyEnginePipeLineFactory implements ChannelPipelineFactory {

    private NettyEngine engine;

    private boolean sslEnabled = false;

    /**
     * constructor that takes engine
     * 
     * @param engine
     */
    public NettyEnginePipeLineFactory(NettyEngine engine) {
        this.engine = engine;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * NOTE: initially when the server is started <tt>HTTP</tt> encoder/decoder
     * are added in the channel pipeline which is required for the initial
     * handshake request for WebSocket connection. Once the connection is made
     * by sending the appropriate response the encoder/decoder is replaced at
     * runtime by {@code WebSocketFrameDecoder} and {@code
     * WebSocketFrameEncoder}.
     */
    @Override
    public ChannelPipeline getPipeline() throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = Channels.pipeline();

        // Add SSL handler first to encrypt and decrypt everything.
        if (sslEnabled) {
            SSLEngine sslEngine = JWebSocketSslContextFactory.getServerContext().createSSLEngine();
            sslEngine.setUseClientMode(false);

            pipeline.addLast("ssl", new SslHandler(sslEngine));

        }
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
        pipeline.addLast("encoder", new HttpResponseEncoder());

        // create a new handler instance for each new channel to avoid a
        // race condition where a unauthenticated client can get the
        // confidential information:
        pipeline.addLast("handler", new NettyEngineHandler(engine));
        return pipeline;
    }
}
