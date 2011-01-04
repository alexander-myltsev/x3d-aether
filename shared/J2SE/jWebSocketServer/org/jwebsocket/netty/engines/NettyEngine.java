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

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jwebsocket.api.EngineConfiguration;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.engines.BaseEngine;
import org.jwebsocket.kit.CloseReason;
import org.jwebsocket.kit.WebSocketException;
import org.jwebsocket.logging.Logging;

/**
 * Netty based implementation of {@code WebSocketEngine} engine.It uses the
 * low-level <tt>ServerBootStrap</tt> to start the server and handles the
 * incoming/outgoing request/response using {@code NettyEngineHandler} class.
 *
 * @author puran
 * @version $Id: NettyEngine.java 596 2010-06-22 17:09:54Z fivefeetfurther $
 * @see NettyEngineHandler
 */
public class NettyEngine extends BaseEngine {

	private static Logger log = Logging.getLogger(NettyEngine.class);
	private volatile boolean isRunning = false;
	private static final ChannelGroup allChannels = new DefaultChannelGroup("jWebSocket-NettyEngine");
	private Channel channel = null;

	/**
	 * constructor
	 * @param config
	 */
	public NettyEngine(EngineConfiguration configuration) {
		super(configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startEngine() throws WebSocketException {
		if (log.isDebugEnabled()) {
			log.debug("Starting Netty engine (" + getId() + ")...");
		}
		// Configure the server.
		ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new NettyEnginePipeLineFactory(this));
		// Bind and start to accept incoming connections.
		channel = bootstrap.bind(new InetSocketAddress(getConfiguration().getPort()));

		//set the timeout value if only it's greater than 0 in configuration
		if (getConfiguration().getTimeout() > 0) {
			channel.getConfig().setConnectTimeoutMillis(getConfiguration().getTimeout());
		}

		// fire the engine start event
		engineStarted();

		allChannels.add(channel);

		isRunning = true;

		if (log.isInfoEnabled()) {
			log.info("Netty engine '" + getId() + "' started.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stopEngine(CloseReason aCloseReason) throws WebSocketException {
		log.debug("Stopping Netty engine '" + getId() + "'...");
		isRunning = false;

		super.stopEngine(aCloseReason);
		engineStopped();

		// Added by Alex 2010-08-09
		if (channel != null) {
			channel.close();
		}
		ChannelGroupFuture future = allChannels.close();
		future.awaitUninterruptibly();
		channel.getFactory().releaseExternalResources();
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void connectorStarted(WebSocketConnector aConnector) {
		log.debug("Detected new connector at port " + aConnector.getRemotePort() + ".");
		super.connectorStarted(aConnector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void connectorStopped(WebSocketConnector aConnector, CloseReason aCloseReason) {
		log.debug("Detected stopped connector at port " + aConnector.getRemotePort() + ".");
		super.connectorStopped(aConnector, aCloseReason);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAlive() {
		if (isRunning) {
			return true;
		} else {
			return false;
		}
	}
}
