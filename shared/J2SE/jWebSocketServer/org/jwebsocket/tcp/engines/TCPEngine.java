//	---------------------------------------------------------------------------
//	jWebSocket - WebSocket TCP Engine
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
package org.jwebsocket.tcp.engines;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Map;
import javolution.util.FastMap;

import org.apache.log4j.Logger;
import org.jwebsocket.api.EngineConfiguration;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.api.WebSocketEngine;
import org.jwebsocket.config.JWebSocketCommonConstants;
import org.jwebsocket.engines.BaseEngine;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.tcp.connectors.TCPConnector;
import org.jwebsocket.kit.CloseReason;
import org.jwebsocket.kit.RequestHeader;
import org.jwebsocket.kit.WebSocketException;
import org.jwebsocket.kit.WebSocketHandshake;

/**
 * Implementation of the jWebSocket TCP engine. The TCP engine provide a Java
 * Socket implementation of the WebSocket protocol. It contains the handshake
 * @author aschulze
 */
public class TCPEngine extends BaseEngine {

	private static Logger mLog = Logging.getLogger(TCPEngine.class);
	private ServerSocket mServerSocket = null;
	private int mListenerPort = JWebSocketCommonConstants.DEFAULT_PORT;
	private int mSessionTimeout = JWebSocketCommonConstants.DEFAULT_TIMEOUT;
	private boolean mIsRunning = false;
	private Thread mEngineThread = null;

	public TCPEngine(EngineConfiguration aConfiguration) {
		super(aConfiguration);
		mListenerPort = aConfiguration.getPort();
		mSessionTimeout = aConfiguration.getTimeout();
	}

	@Override
	public void startEngine()
			throws WebSocketException {
		if (mLog.isDebugEnabled()) {
			mLog.debug("Starting TCP engine '"
					+ getId()
					+ "' at port " + mListenerPort
					+ " with default timeout "
					+ (mSessionTimeout > 0 ? mSessionTimeout + "ms" : "infinite")
					+ "...");
		}
		try {
			mServerSocket = new ServerSocket(mListenerPort);
			/*
			serverSocket = new ServerSocket(listenerPort); // listenerPort
			serverSocket.setReuseAddress(true);
			InetSocketAddress lISA = new InetSocketAddress(listenerPort);
			serverSocket.bind(lISA);
			 */
			setSessionTimeout(mSessionTimeout);

			EngineListener listener = new EngineListener(this);
			mEngineThread = new Thread(listener);
			mEngineThread.start();

		} catch (IOException ex) {
			throw new WebSocketException(ex.getMessage());
		}

		// TODO: results in firing started event twice! make more clean!
		// super.startEngine();
		if (mLog.isInfoEnabled()) {
			mLog.info("TCP engine '"
					+ getId() + "' started' at port "
					+ mListenerPort + " with default timeout "
					+ (mSessionTimeout > 0 ? mSessionTimeout + "ms" : "infinite")
					+ "...");
		}
	}

	@Override
	public void stopEngine(CloseReason aCloseReason)
			throws WebSocketException {
		if (mLog.isDebugEnabled()) {
			mLog.debug("Stopping TCP engine '" + getId() + "' at port " + mListenerPort + "...");
		}

		// resetting "isRunning" causes engine listener to terminate
		mIsRunning = false;
		long lStarted = new Date().getTime();

		try {
			// when done, close server socket
			// closing the server socket should lead to an IOExeption
			// at accept in the listener thread which terminates the listener
			if (mServerSocket != null && !mServerSocket.isClosed()) {
				mServerSocket.close();
				if (mLog.isInfoEnabled()) {
					mLog.info("TCP engine '" + getId() + "' stopped at port " + mListenerPort + " (closed=" + mServerSocket.isClosed() + ").");
				}
				mServerSocket = null;
			} else {
				mLog.warn("Stopping TCP engine '" + getId() + "': no server socket or server socket closed.");
			}
		} catch (Exception ex) {
			mLog.error(ex.getClass().getSimpleName() + " on stopping TCP engine '" + getId() + "': " + ex.getMessage());
		}

		if (mEngineThread != null) {
			try {
				// TODO: Make this timeout configurable one day
				mEngineThread.join(10000);
			} catch (Exception ex) {
				mLog.error(ex.getClass().getSimpleName() + ": " + ex.getMessage());
			}
			if (mLog.isDebugEnabled()) {
				long lDuration = new Date().getTime() - lStarted;
				if (mEngineThread.isAlive()) {
					mLog.warn("TCP engine '" + getId() + "' did not stop after " + lDuration + "ms.");
				} else {
					mLog.debug("TCP engine '" + getId() + "' stopped after " + lDuration + "ms.");
				}
			}
		}

		// inherited method stops all connectors
		lStarted = new Date().getTime();
		int lNumConns = getConnectors().size();
		super.stopEngine(aCloseReason);

		// now wait until all connectors have been closed properly
		try {
			while (getConnectors().size() > 0 && new Date().getTime() - lStarted < 10000) {
				Thread.sleep(250);
			}
		} catch (Exception ex) {
			mLog.error(ex.getClass().getSimpleName() + ": " + ex.getMessage());
		}
		if (mLog.isDebugEnabled()) {
			long lDuration = new Date().getTime() - lStarted;
			int lRemConns = getConnectors().size();
			if (lRemConns > 0) {
				mLog.warn(lRemConns + " of " + lNumConns
						+ " TCP connectors '" + getId()
						+ "' did not stop after " + lDuration + "ms.");
			} else {
				mLog.debug(lNumConns
						+ " TCP connectors '" + getId()
						+ "' stopped after " + lDuration + "ms.");
			}
		}
	}

	@Override
	public void connectorStarted(WebSocketConnector aConnector) {
		if (mLog.isDebugEnabled()) {
			mLog.debug("Detected new connector at port " + aConnector.getRemotePort() + ".");
		}
		super.connectorStarted(aConnector);
	}

	@Override
	public void connectorStopped(WebSocketConnector aConnector, CloseReason aCloseReason) {
		if (mLog.isDebugEnabled()) {
			mLog.debug("Detected stopped connector at port " + aConnector.getRemotePort() + ".");
		}
		super.connectorStopped(aConnector, aCloseReason);
	}

	private RequestHeader processHandshake(Socket aClientSocket)
			throws UnsupportedEncodingException, IOException {

		InputStream lIn = aClientSocket.getInputStream();
		OutputStream lOut = aClientSocket.getOutputStream();

		// TODO: Replace this structure by more dynamic ByteArrayOutputStream?
		byte[] lBuff = new byte[8192];
		int lRead = lIn.read(lBuff);
		if (lRead <= 0) {
			mLog.warn("Connection did not detect initial handshake.");
			return null;
		}
		byte[] lReq = new byte[lRead];
		System.arraycopy(lBuff, 0, lReq, 0, lRead);

		//if (mLog.isDebugEnabled()) {
		//    mLog.debug("Handshake Request:\n" + new String(lResp));
		//    mLog.debug("Parsing initial WebSocket handshake...");
		//}
		Map lRespMap = WebSocketHandshake.parseC2SRequest(lReq);
		// maybe the request is a flash policy-file-request
		String lFlashBridgeReq = (String) lRespMap.get("policy-file-request");
		if (lFlashBridgeReq != null) {
			mLog.warn("TCPEngine returned policy file request ('"
					+ lFlashBridgeReq + "'), check for FlashBridge plug-in.");
		}
		// generate the websocket handshake
		// if policy-file-request is found answer it
		byte[] lBA = WebSocketHandshake.generateS2CResponse(lRespMap);
		if (lBA == null) {
			if (mLog.isDebugEnabled()) {
				mLog.warn("TCPEngine detected illegal handshake.");
			}
			return null;
		}
		//if (log.isDebugEnabled()) {
		//	log.debug("Handshake Response:\n" + new String(lResp));
		//	mLog.debug("Flushing initial WebSocket handshake...");
		//}
		lOut.write(lBA);
		lOut.flush();

		// if we detected a flash policy-file-request return "null"
		// (no websocket header detected)
		if (lFlashBridgeReq != null) {
			mLog.warn("TCPEngine returned policy file response ('" + new String(lBA, "US-ASCII") + "'), check for FlashBridge plug-in.");
			return null;
		}

		RequestHeader lHeader = new RequestHeader();
		Map<String, String> lArgs = new FastMap<String, String>();
		String lPath = (String) lRespMap.get("path");

		// isolate search string
		String lSearchString = "";
		if (lPath != null) {
			int lPos = lPath.indexOf(JWebSocketCommonConstants.PATHARG_SEPARATOR);
			if (lPos >= 0) {
				lSearchString = lPath.substring(lPos + 1);
				if (lSearchString.length() > 0) {
					String[] lArgsArray = lSearchString.split(JWebSocketCommonConstants.ARGARG_SEPARATOR);
					for (int i = 0; i < lArgsArray.length; i++) {
						String[] lKeyValuePair = lArgsArray[i].split(JWebSocketCommonConstants.KEYVAL_SEPARATOR, 2);
						if (lKeyValuePair.length == 2) {
							lArgs.put(lKeyValuePair[0], lKeyValuePair[1]);
							if (mLog.isDebugEnabled()) {
								mLog.debug("arg" + i + ": " + lKeyValuePair[0] + "=" + lKeyValuePair[1]);
							}
						}
					}
				}
			}
		}

		if (mLog.isDebugEnabled()) {
			mLog.debug("Handshake flushed.");
		}

		// set default sub protocol if none passed
		if (lArgs.get("prot") == null) {
			lArgs.put("prot", JWebSocketCommonConstants.SUB_PROT_DEFAULT);
		}

		lHeader.put("host", lRespMap.get("host"));
		lHeader.put("origin", lRespMap.get("origin"));
		lHeader.put("location", lRespMap.get("location"));

		lHeader.put("path", lRespMap.get("path"));
		lHeader.put("searchString", lSearchString);
		lHeader.put("args", lArgs);

		return lHeader;
	}

	@Override
	/*
	 * Returns {@code true} if the TCP engine is running or {@code false}
	 * otherwise. The alive status represents the state of the TCP engine
	 * listener thread.
	 */
	public boolean isAlive() {
		return (mEngineThread != null && mEngineThread.isAlive());
	}

	private class EngineListener implements Runnable {

		private WebSocketEngine mEngine = null;

		/**
		 * Creates the server socket listener for new
		 * incoming socket connections.
		 * @param aEngine
		 */
		public EngineListener(WebSocketEngine aEngine) {
			mEngine = aEngine;
		}

		@Override
		public void run() {

			// notify server that engine has started
			engineStarted();

			mIsRunning = true;
			while (mIsRunning) {
				try {
					// accept is blocking so here is no need
					// to put any sleeps into this loop
					// if (log.isDebugEnabled()) {
					//	log.debug("Waiting for client...");
					// }
					Socket lClientSocket = mServerSocket.accept();
					boolean lTCPNoDelay = lClientSocket.getTcpNoDelay();
					lClientSocket.setTcpNoDelay(true);
					try {
						// process handshake to parse header data
						RequestHeader lHeader = processHandshake(lClientSocket);
						if (lHeader != null) {
							// set socket timeout to given amount of milliseconds
							// use tcp engine's timeout as default and
							// check system's min and max timeout ranges
							int lSessionTimeout = lHeader.getTimeout(getSessionTimeout());
							/* min and max range removed since 0.9.0.0602, see config documentation
							if (lSessionTimeout > JWebSocketServerConstants.MAX_TIMEOUT) {
							lSessionTimeout = JWebSocketServerConstants.MAX_TIMEOUT;
							} else if (lSessionTimeout < JWebSocketServerConstants.MIN_TIMEOUT) {
							lSessionTimeout = JWebSocketServerConstants.MIN_TIMEOUT;
							}
							 */
							if (mLog.isDebugEnabled()) {
								mLog.debug("Client accepted on port "
										+ lClientSocket.getPort()
										+ " with timeout "
										+ (lSessionTimeout > 0 ? lSessionTimeout + "ms" : "infinite")
										+ " (TCPNoDelay was: " + lTCPNoDelay + ")...");
							}
							if (lSessionTimeout > 0) {
								lClientSocket.setSoTimeout(lSessionTimeout);
							}
							// create connector and pass header
							// log.debug("Instantiating connector...");
							WebSocketConnector lConnector = new TCPConnector(mEngine, lClientSocket);
							// log.debug("Setting header to engine...");
							lConnector.setHeader(lHeader);
							// log.debug("Adding connector to engine...");
							getConnectors().put(lConnector.getId(), lConnector);
							if (mLog.isDebugEnabled()) {
								mLog.debug("Starting connector...");
							}
							lConnector.startConnector();
						} else {
							// if header could not be parsed properly
							// immediately disconnect the client.
							lClientSocket.close();
						}
					} catch (UnsupportedEncodingException ex) {
						mLog.error("(encoding) " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
					} catch (IOException ex) {
						mLog.error("(io) " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
					} catch (Exception ex) {
						mLog.error("(other) " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
					}
				} catch (Exception ex) {
					mIsRunning = false;
					mLog.error("(accept) " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
				}
			}

			// notify server that engine has stopped
			// this closes all connections
			engineStopped();
		}
	}
}
