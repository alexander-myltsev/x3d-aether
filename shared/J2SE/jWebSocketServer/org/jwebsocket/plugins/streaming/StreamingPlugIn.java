//	---------------------------------------------------------------------------
//	jWebSocket - Streaming Plug-In
//	Copyright (c) 2010 jWebSocket.org by Innotrade GmbH Alexander Schulze
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
package org.jwebsocket.plugins.streaming;

import java.util.Map;
import javolution.util.FastMap;
import org.apache.log4j.Logger;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.api.WebSocketEngine;
import org.jwebsocket.config.JWebSocketServerConstants;
import org.jwebsocket.kit.CloseReason;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.kit.PlugInResponse;
import org.jwebsocket.plugins.TokenPlugIn;
import org.jwebsocket.server.TokenServer;
import org.jwebsocket.token.Token;

/**
 * implements the stream control plug-in to manage the various underlying
 * streams. Streams are instantiated by the application and registered at
 * the streaming plug-in. The streaming plug-in only can control streams
 * but not instantiate new streams.
 * @author aschulze
 */
public class StreamingPlugIn extends TokenPlugIn {

	private static Logger log = Logging.getLogger(StreamingPlugIn.class);
	private String NS_STREAMING_DEFAULT = JWebSocketServerConstants.NS_BASE + ".plugins.streaming";
	private Map<String, BaseStream> streams = new FastMap<String, BaseStream>();
	private boolean streamsInitialized = false;
	private TimeStream lTimeStream = null;
	private MonitorStream lMonitorStream = null;
	private StressStream lStressStream = null;
	private AetherStream lAetherStream = null;

	/**
	 * create a new instance of the streaming plug-in and set the default
	 * name space for the plug-in.
	 */
	public StreamingPlugIn() {
		if (log.isDebugEnabled()) {
			log.debug("Instantiating streaming plug-in...");
		}
		// specify default name space for streaming plugin
		this.setNamespace(NS_STREAMING_DEFAULT);

	}

	private void startStreams() {
		if (!streamsInitialized) {
			if (log.isDebugEnabled()) {
				log.debug("Starting registered streams...");
			}
			TokenServer lTokenServer = getServer();
			if (lTokenServer != null) {
				// create the stream for the time stream demo
				lTimeStream = new TimeStream("timeStream", lTokenServer);
				addStream(lTimeStream);
				// create the stream for the monitor stream demo
				lMonitorStream = new MonitorStream("monitorStream", lTokenServer);
				addStream(lMonitorStream);
				// create the stream for the monitor stream demo
				lStressStream = new StressStream("stressStream", lTokenServer);
				addStream(lStressStream);
				
				lAetherStream = new AetherStream("aetherStream", lTokenServer);
				addStream(lAetherStream);
				
				streamsInitialized = true;
			}
		}
	}

	private void stopStreams() {
		if (streamsInitialized) {
			if (log.isDebugEnabled()) {
				log.debug("Stopping registered streams...");
			}
			TokenServer lTokenServer = getServer();
			if (lTokenServer != null) {
				// stop the stream for the time stream demo
				if (lTimeStream != null) {
					lTimeStream.stopStream(3000);
				}
				// stop the stream for the monitor stream demo
				if (lMonitorStream != null) {
					lMonitorStream.stopStream(3000);
				}
				// stop the stream for the stress stream demo
				if (lStressStream != null) {
					lStressStream.stopStream(3000);
				}
				
				if (lAetherStream != null) {
					lAetherStream.stopStream(3000);
				}
				
				lTimeStream = null;
				lMonitorStream = null;
				lStressStream = null;
				lAetherStream = null;
				streamsInitialized = false;
			}
		}
	}

	/**
	 * adds a new stream to the mapo of streams. The stream must not be null
	 * and must have a valid and unqiue id.
	 * @param aStream
	 */
	public void addStream(BaseStream aStream) {
		if (aStream != null && aStream.getStreamID() != null) {
			streams.put(aStream.getStreamID(), aStream);
		}
	}

	@Override
	public void processToken(PlugInResponse aAction, WebSocketConnector aConnector, Token aToken) {
		String lType = aToken.getType();
		String lNS = aToken.getNS();

		if (lType != null && (lNS == null || lNS.equals(getNamespace()))) {
			if (lType.equals("register")) {
				registerConnector(aConnector, aToken);
			} else if (lType.equals("unregister")) {
				unregisterConnector(aConnector, aToken);
			}
		}
	}

	/**
	 * registers a connector at a certain stream.
	 * @param aConnector
	 * @param aToken
	 */
	public void registerConnector(WebSocketConnector aConnector, Token aToken) {
		if (log.isDebugEnabled()) {
			log.debug("Processing register...");
		}

		BaseStream lStream = null;
		String lStreamID = (String) aToken.get("stream");
		if (lStreamID != null) {
			lStream = streams.get(lStreamID);
		}

		if (lStream != null) {
			if (!lStream.isConnectorRegistered(aConnector)) {
				if (log.isDebugEnabled()) {
					log.debug("Registering client at stream '" + lStreamID + "'...");
				}
				lStream.registerConnector(aConnector);
			}
			// else...
			// todo: error handling
		}
		// else...
		// todo: error handling
	}

	/**
	 * registers a connector from a certain stream.
	 * @param aConnector
	 * @param aToken
	 */
	public void unregisterConnector(WebSocketConnector aConnector, Token aToken) {
		if (log.isDebugEnabled()) {
			log.debug("Processing unregister...");
		}

		BaseStream lStream = null;
		String lStreamID = (String) aToken.get("stream");
		if (lStreamID != null) {
			lStream = streams.get(lStreamID);
		}

		if (lStream != null) {
			if (lStream.isConnectorRegistered(aConnector)) {
				if (log.isDebugEnabled()) {
					log.debug("Unregistering client from stream '"
							+ lStreamID + "'...");
				}
				lStream.unregisterConnector(aConnector);
			}
			// else...
			// todo: error handling
		}
		// else...
		// todo: error handling
	}

	@Override
	public void connectorStopped(WebSocketConnector aConnector, CloseReason aCloseReason) {
		// if a connector terminates, unregister it from all streams.
		for (BaseStream lStream : streams.values()) {
			try {
				lStream.unregisterConnector(aConnector);
			} catch (Exception ex) {
				log.error(ex.getClass().getSimpleName()
						+ " on stopping conncector: " + ex.getMessage());
			}
		}
	}

	@Override
	public void engineStarted(WebSocketEngine aEngine) {
		startStreams();
	}

	@Override
	public void engineStopped(WebSocketEngine aEngine) {
		stopStreams();
	}
}
