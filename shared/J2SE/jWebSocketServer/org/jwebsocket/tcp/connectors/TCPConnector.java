/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jwebsocket.tcp.connectors;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import org.apache.log4j.Logger;
import org.jwebsocket.api.EngineConfiguration;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.api.WebSocketEngine;
import org.jwebsocket.config.JWebSocketCommonConstants;
import org.jwebsocket.connectors.BaseConnector;
import org.jwebsocket.kit.CloseReason;
import org.jwebsocket.kit.RawPacket;
import org.jwebsocket.logging.Logging;

/**
 * Implementation of the jWebSocket TCP socket connector.
 * @author aschulze
 */
public class TCPConnector extends BaseConnector {

	private static Logger mLog = Logging.getLogger(TCPConnector.class);
	private InputStream mIn = null;
	private OutputStream mOut = null;
	private Socket mClientSocket = null;
	private boolean mIsRunning = false;
	private CloseReason mCloseReason = CloseReason.TIMEOUT;

	/**
	 * creates a new TCP connector for the passed engine using the passed 
	 * client socket. Usually connectors are instantiated by their engine
	 * only, not by the application.
	 * @param aEngine
	 * @param aClientSocket
	 */
	public TCPConnector(WebSocketEngine aEngine, Socket aClientSocket) {
		super(aEngine);
		mClientSocket = aClientSocket;
		try {
			mIn = mClientSocket.getInputStream();
			mOut = new PrintStream(mClientSocket.getOutputStream(), true, "UTF-8");
		} catch (Exception ex) {
			mLog.error(ex.getClass().getSimpleName()
					+ " instantiating " + getClass().getSimpleName()
					+ ": " + ex.getMessage());
		}
	}

	@Override
	public void startConnector() {
		int lPort = -1;
		int lTimeout = -1;
		try {
			lPort = mClientSocket.getPort();
			lTimeout = mClientSocket.getSoTimeout();
		} catch (Exception ex) {
		}
		if (mLog.isDebugEnabled()) {
			mLog.debug("Starting TCP connector on port "
					+ lPort + " with timeout "
					+ (lTimeout > 0 ? lTimeout + "ms" : "infinite")
					+ "");
		}
		ClientProcessor clientProc = new ClientProcessor(this);
		Thread clientThread = new Thread(clientProc);
		clientThread.start();
		if (mLog.isInfoEnabled()) {
			mLog.info("Started TCP connector on port "
					+ lPort + " with timeout "
					+ (lTimeout > 0 ? lTimeout + "ms" : "infinite")
					+ "");
		}
	}

	@Override
	public void stopConnector(CloseReason aCloseReason) {
		if (mLog.isDebugEnabled()) {
			mLog.debug("Stopping TCP connector (" + aCloseReason.name() + ")...");
		}
		int lPort = mClientSocket.getPort();
		mCloseReason = aCloseReason;
		mIsRunning = false;

		try {
			mIn.close();
			if (mLog.isInfoEnabled()) {
				mLog.info("Stopped TCP connector ("
						+ aCloseReason.name() + ") on port " + lPort + ".");
			}
		} catch (IOException ex) {
			if (mLog.isDebugEnabled()) {
				mLog.info(ex.getClass().getSimpleName() + " while stopping TCP connector ("
						+ aCloseReason.name() + ") on port " + lPort + ": " + ex.getMessage());
			}
		}
	}

	@Override
	public void processPacket(WebSocketPacket aDataPacket) {
		// forward the data packet to the engine
		getEngine().processPacket(this, aDataPacket);
	}

	@Override
	public synchronized void sendPacket(WebSocketPacket aDataPacket) {
		try {
			if (aDataPacket.getFrameType() == RawPacket.FRAMETYPE_BINARY) {
				// each packet is enclosed in 0xFF<length><data>
				// TODO: for future use! Not yet finally spec'd in IETF drafts!
				mOut.write(0xFF);
				byte[] lBA = aDataPacket.getByteArray();
				// TODO: implement multi byte length!
				mOut.write(lBA.length);
				mOut.write(lBA);
			} else {
				// each packet is enclosed in 0x00<data>0xFF
				mOut.write(0x00);
				mOut.write(aDataPacket.getByteArray());
				mOut.write(0xFF);
			}
			mOut.flush();
		} catch (IOException ex) {
			mLog.error(ex.getClass().getSimpleName()
					+ " sending data packet: " + ex.getMessage());
		}
	}

	private class ClientProcessor implements Runnable {

		private WebSocketConnector connector = null;

		/**
		 * Creates the new socket listener thread for this connector.
		 * @param aConnector
		 */
		public ClientProcessor(WebSocketConnector aConnector) {
			connector = aConnector;
		}

		@Override
		public void run() {
			WebSocketEngine engine = getEngine();

			int lMaxFrameSize = JWebSocketCommonConstants.DEFAULT_MAX_FRAME_SIZE;
			EngineConfiguration config = engine.getConfiguration();
			if (config != null && config.getMaxFramesize() > 0) {
				lMaxFrameSize = config.getMaxFramesize();
			}
			byte[] lBuff = new byte[lMaxFrameSize];
			int pos = -1;
			int lStart = -1;

			try {
				// start client listener loop
				mIsRunning = true;

				// call connectorStarted method of engine
				engine.connectorStarted(connector);

				while (mIsRunning) {
					try {
						int b = mIn.read();
						// start of frame
						if (b == 0x00) {
							pos = 0;
							lStart = 0;
							// end of frame
						} else if (b == 0xff) {
							if (lStart >= 0) {
								if (pos <= lMaxFrameSize) {
									RawPacket lPacket = new RawPacket(Arrays.copyOf(lBuff, pos));
									try {
										engine.processPacket(connector, lPacket);
									} catch (Exception ex) {
										mLog.error(ex.getClass().getSimpleName()
												+ " in processPacket of connector "
												+ connector.getClass().getSimpleName()
												+ ": " + ex.getMessage());
									}
								} else {
									mLog.error("Datapacket exceeded maximum size of " + lMaxFrameSize + " bytes and will not be processed!");
								}
							}
							lStart = -1;
							// end of stream
						} else if (b < 0) {
							mCloseReason = CloseReason.CLIENT;
							mIsRunning = false;
							// any other byte within or outside a frame
						} else {
							if (lStart >= 0 && pos < lMaxFrameSize) {
								lBuff[pos] = (byte) b;
							}
							pos++;
						}
					} catch (SocketTimeoutException ex) {
						mLog.error("(timeout) "
								+ ex.getClass().getSimpleName()
								+ ": " + ex.getMessage());
						mCloseReason = CloseReason.TIMEOUT;
						mIsRunning = false;
					} catch (Exception ex) {
						mLog.error("(other) "
								+ ex.getClass().getSimpleName()
								+ ": " + ex.getMessage());
						mCloseReason = CloseReason.SERVER;
						mIsRunning = false;
					}
				}

				// call client stopped method of engine
				// (e.g. to release client from streams)
				engine.connectorStopped(connector, mCloseReason);

				// br.close();
				mIn.close();
				mOut.close();
				mClientSocket.close();

			} catch (Exception ex) {
				// ignore this exception for now
				mLog.error("(close) "
						+ ex.getClass().getSimpleName()
						+ ": " + ex.getMessage());
			}
		}
	}

	@Override
	public String generateUID() {
		String lUID = mClientSocket.getInetAddress().getHostAddress()
				+ "@" + mClientSocket.getPort();
		return lUID;
	}

	@Override
	public int getRemotePort() {
		return mClientSocket.getPort();
	}

	@Override
	public InetAddress getRemoteHost() {
		return mClientSocket.getInetAddress();
	}

	@Override
	public String toString() {
		// TODO: weird results like... '0:0:0:0:0:0:0:1:61130'... on JDK 1.6u19 Windows 7 64bit
		String lRes = getRemoteHost().getHostAddress() + ":" + getRemotePort();
		// TODO: don't hard code. At least use JWebSocketConstants field here.
		String lUsername = getString("org.jWebSocket.plugins.system.username");
		if (lUsername != null) {
			lRes += " (" + lUsername + ")";
		}
		return lRes;
	}
}
