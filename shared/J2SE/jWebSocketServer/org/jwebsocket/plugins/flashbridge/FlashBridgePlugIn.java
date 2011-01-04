//	---------------------------------------------------------------------------
//	jWebSocket - FlashBridge Plug-In
//	Copyright (c) 2010 Innotrade GmbH (http://jWebSocket.org)
//	---------------------------------------------------------------------------
//	This program is free software; you can redistribute it and/or modify it
//	under the terms of the GNU Lesser General Public License as published by the
//	Free Software Foundation; either version 3 of the License, or (at your
//	option) any later version.
//	This program is distributed in the hope that it will be useful, but WITHOUT
//	ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//	FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License 
//  for more details.
//	You should have received a copy of the GNU Lesser General Public License along
//	with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
//	---------------------------------------------------------------------------
package org.jwebsocket.plugins.flashbridge;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import org.apache.log4j.Logger;
import org.jwebsocket.api.WebSocketEngine;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.plugins.TokenPlugIn;

/**
 * This plug-in processes the policy-file-request from the browser side
 * flash plug-in. This makes jWebSocket cross-browser-compatible.
 * @author aschulze
 */
public class FlashBridgePlugIn extends TokenPlugIn {

	private static Logger mLog = Logging.getLogger(FlashBridgePlugIn.class);
	private ServerSocket mServerSocket = null;
	private int mListenerPort = 843;
	private boolean mIsRunning = false;
	private int mEngineInstanceCount = 0;
	private BridgeProcess mBridgeProcess = null;
	private Thread mBridgeThread = null;

	/**
	 *
	 */
	public FlashBridgePlugIn() {
		if (mLog.isDebugEnabled()) {
			mLog.debug("Starting FlashBridge...");
		}
		try {
			mServerSocket = new ServerSocket(mListenerPort);

			mBridgeProcess = new BridgeProcess(this);
			mBridgeThread = new Thread(mBridgeProcess);
			mBridgeThread.start();
			if (mLog.isInfoEnabled()) {
				mLog.info("FlashBridge started.");
			}
		} catch (IOException ex) {
			mLog.error("FlashBridge could not be started: " + ex.getMessage());
		}
	}

	private class BridgeProcess implements Runnable {

		private final FlashBridgePlugIn mPlugIn;

		/**
		 * creates the server socket bridgeProcess for new
		 * incoming socket connections.
		 * @param aPlugIn
		 */
		public BridgeProcess(FlashBridgePlugIn aPlugIn) {
			this.mPlugIn = aPlugIn;
		}

		@Override
		public void run() {

			if (mLog.isDebugEnabled()) {
				mLog.debug("Starting FlashBridge process...");
			}
			mIsRunning = true;
			while (mIsRunning) {
				try {
					// accept is blocking so here is no need
					// to put any sleeps into the loop
					if (mLog.isDebugEnabled()) {
						mLog.debug("Waiting on flash policy-file-request on port " + mServerSocket.getLocalPort() + "...");
					}
					Socket clientSocket = mServerSocket.accept();
					if (mLog.isDebugEnabled()) {
						mLog.debug("Client connected...");
					}
					try {
						// clientSocket.setSoTimeout(TIMEOUT);
						InputStream lIS = clientSocket.getInputStream();
						OutputStream lOS = clientSocket.getOutputStream();
						byte[] ba = new byte[1024];
						String lLine = "";
						boolean lFoundPolicyFileRequest = false;
						int lLen = 0;
						while (lLen >= 0 && !lFoundPolicyFileRequest) {
							lLen = lIS.read(ba);
							if (lLen > 0) {
								lLine += new String(ba, 0, lLen, "US-ASCII");
							}
							if (mLog.isDebugEnabled()) {
								mLog.debug("Received " + lLine + "...");
							}
							lFoundPolicyFileRequest = lLine.indexOf("policy-file-request") >= 0; // "<policy-file-request/>"
						}
						if (lFoundPolicyFileRequest) {
							if (mLog.isDebugEnabled()) {
								mLog.debug("Answering on flash policy-file-request (" + lLine + ")...");
							}
							lOS.write(("<cross-domain-policy>"
									+ "<allow-access-from domain=\"*\" to-ports=\"*\" />"
									+ "</cross-domain-policy>").getBytes());
							lOS.flush();
						} else {
							mLog.warn("Received invalid policy-file-request (" + lLine + ")...");
						}
					} catch (UnsupportedEncodingException ex) {
						mLog.error("(encoding) " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
					} catch (IOException ex) {
						mLog.error("(io) " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
					} catch (Exception ex) {
						mLog.error("(other) " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
					}

					clientSocket.close();
					if (mLog.isDebugEnabled()) {
						mLog.debug("Client disconnected...");
					}
				} catch (Exception ex) {
					mIsRunning = false;
					mLog.error("Socket state: " + ex.getMessage());
				}
			}
			if (mLog.isDebugEnabled()) {
				mLog.debug("FlashBridge process stopped.");
			}
		}
	}

	@Override
	public void engineStarted(WebSocketEngine aEngine) {
		if (mLog.isDebugEnabled()) {
			mLog.debug("Engine '" + aEngine.getId() + "' started.");
		}
		// every time an engine starts increment counter
		mEngineInstanceCount++;
	}

	@Override
	public void engineStopped(WebSocketEngine aEngine) {
		if (mLog.isDebugEnabled()) {
			mLog.debug("Engine '" + aEngine.getId() + "' stopped.");
		}
		// every time an engine starts decrement counter
		mEngineInstanceCount--;
		// when last engine stopped also stop the FlashBridge
		if (mEngineInstanceCount <= 0) {
			super.engineStopped(aEngine);

			mIsRunning = false;
			long lStarted = new Date().getTime();

			try {
				// when done, close server socket
				// closing the server socket should lead to an exception
				// at accept in the bridgeProcess thread which terminates the bridgeProcess
				if (mLog.isDebugEnabled()) {
					mLog.debug("Closing FlashBridge server socket...");
				}
				mServerSocket.close();
				if (mLog.isDebugEnabled()) {
					mLog.debug("Closed FlashBridge server socket.");
				}
			} catch (Exception ex) {
				mLog.error("(accept) " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
			}

			try {
				mBridgeThread.join(10000);
			} catch (Exception ex) {
				mLog.error(ex.getClass().getSimpleName() + ": " + ex.getMessage());
			}
			if (mLog.isDebugEnabled()) {
				long lDuration = new Date().getTime() - lStarted;
				if (mBridgeThread.isAlive()) {
					mLog.warn("FlashBridge did not stopped after " + lDuration + "ms.");
				} else {
					mLog.debug("FlashBridge stopped after " + lDuration + "ms.");
				}
			}
		}
	}
}
