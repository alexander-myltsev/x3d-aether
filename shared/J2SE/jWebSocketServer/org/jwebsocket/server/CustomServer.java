//	---------------------------------------------------------------------------
//	jWebSocket - WebSocket Token Server (abstract)
//	Copyright (c) 2010 Alexander Schulze, Innotrade GmbH
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
package org.jwebsocket.server;

import java.util.List;
import org.apache.log4j.Logger;
import org.jwebsocket.api.ServerConfiguration;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.kit.RequestHeader;
import org.jwebsocket.api.WebSocketPlugIn;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.api.WebSocketEngine;
import org.jwebsocket.api.WebSocketServerListener;
import org.jwebsocket.config.JWebSocketCommonConstants;
import org.jwebsocket.kit.CloseReason;
import org.jwebsocket.kit.WebSocketServerEvent;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.plugins.BasePlugInChain;

/**
 *
 * @author aschulze
 */
public class CustomServer extends BaseServer {

	private static Logger log = Logging.getLogger(CustomServer.class);

	/**
	 * Creates a new instance of the CustomeServer. The custom server is a
	 * low-level data packet handler which is provided rather as an example
	 *
	 * @param aId
	 */
	public CustomServer(ServerConfiguration aServerConfig) {
		super(aServerConfig);
		plugInChain = new BasePlugInChain(this);
	}

	@Override
	public void processPacket(WebSocketEngine aEngine, WebSocketConnector aConnector, WebSocketPacket aDataPacket) {
		if (log.isDebugEnabled()) {
			log.debug("Processing data packet '" + aDataPacket.getUTF8() + "'...");
		}
		RequestHeader lHeader = aConnector.getHeader();
		String lSubProt = (lHeader != null ? lHeader.getSubProtocol(null) : null);

		// the custom server here answers with a simple echo packet.
		// this section can be used as an example for your own protol handling.
		if (lSubProt != null && lSubProt.equals(JWebSocketCommonConstants.SUB_PROT_CUSTOM)) {
			// send a modified echo packet back to sender.

			// you also could broadcast the packet here...
			// broadcastPacket(aDataPacket);
			// ...or forward it to your custom specific plug-in chain
			// PlugInResponse response = new PlugInResponse();
			// plugInChain.processPacket(response, aConnector, aDataPacket);

			// forward the token to the listener chain
			List<WebSocketServerListener> lListeners = getListeners();
			WebSocketServerEvent lEvent = new WebSocketServerEvent(aConnector, this);
			for (WebSocketServerListener lListener : lListeners) {
				if (lListener != null && lListener instanceof WebSocketServerListener) {
					((WebSocketServerListener) lListener).processPacket(lEvent, aDataPacket);
				}
			}
		}
	}

	/**
	 * removes a plugin from the plugin chain of the server.
	 * @param aPlugIn
	 */
	public void removePlugIn(WebSocketPlugIn aPlugIn) {
		plugInChain.removePlugIn(aPlugIn);
	}

	@Override
	public void engineStarted(WebSocketEngine aEngine) {
		if (log.isDebugEnabled()) {
			log.debug("Processing engine started...");
		}
		plugInChain.engineStarted(aEngine);
	}

	@Override
	public void engineStopped(WebSocketEngine aEngine) {
		if (log.isDebugEnabled()) {
			log.debug("Processing engine stopped...");
		}
		plugInChain.engineStopped(aEngine);
	}

	@Override
	public void connectorStarted(WebSocketConnector aConnector) {
		if (log.isDebugEnabled()) {
			log.debug("Processing connector started...");
		}
		// notify plugins that a connector has started,
		// i.e. a client was sconnected.
		plugInChain.connectorStarted(aConnector);
		super.connectorStarted(aConnector);
	}

	@Override
	public void connectorStopped(WebSocketConnector aConnector, CloseReason aCloseReason) {
		if (log.isDebugEnabled()) {
			log.debug("Processing connector stopped...");
		}
		// notify plugins that a connector has stopped,
		// i.e. a client was disconnected.
		plugInChain.connectorStopped(aConnector, aCloseReason);
		super.connectorStopped(aConnector, aCloseReason);
	}

	/**
	 * @return the plugInChain
	 */
	@Override
	public BasePlugInChain getPlugInChain() {
		return (BasePlugInChain) plugInChain;
	}
}
