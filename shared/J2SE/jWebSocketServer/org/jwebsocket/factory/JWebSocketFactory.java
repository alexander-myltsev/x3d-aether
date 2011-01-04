/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jwebsocket.factory;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jwebsocket.api.WebSocketEngine;
import org.jwebsocket.api.WebSocketFilter;
import org.jwebsocket.api.WebSocketInitializer;
import org.jwebsocket.api.WebSocketPlugIn;
import org.jwebsocket.api.WebSocketServer;
import org.jwebsocket.instance.JWebSocketInstance;
import org.jwebsocket.kit.CloseReason;
import org.jwebsocket.kit.WebSocketException;
import org.jwebsocket.logging.Logging;

/**
 * Factory to initialize and start the jWebSocket components
 * @author aschulze
 * @version $Id:$
 */
public class JWebSocketFactory {

	// don't instantiate logger here! first read args!
	private static Logger mLog = null;
	private static WebSocketEngine mEngine = null;
	private static List<WebSocketServer> mServers = null;

	public static void start() {

		JWebSocketInstance.setStatus(JWebSocketInstance.STARTING);

		JWebSocketLoader loader = new JWebSocketLoader();
		try {
			WebSocketInitializer lInitializer = loader.initialize();
			if (lInitializer == null) {
				// System.err.println("ERROR:jWebSocket Server sub system could not be initialized.");
				JWebSocketInstance.setStatus(JWebSocketInstance.SHUTTING_DOWN);
				return;
			}
			lInitializer.initializeLogging();

			mLog = Logging.getLogger(JWebSocketFactory.class);
			if (mLog.isDebugEnabled()) {
				mLog.debug("Starting jWebSocket Server Sub System...");
			}
			mEngine = lInitializer.initializeEngine();
			if (mEngine == null) {
				// the loader already logs an error!
				JWebSocketInstance.setStatus(JWebSocketInstance.SHUTTING_DOWN);
				return;
			}

			// initialize and start the server
			if (mLog.isDebugEnabled()) {
				mLog.debug("Initializing servers...");
			}
			mServers = lInitializer.initializeServers();

			Map<String, List<WebSocketPlugIn>> lPluginMap = lInitializer.initializePlugins();
			if (mLog.isDebugEnabled()) {
				mLog.debug("Initializing plugins...");
			}
			for (WebSocketServer lServer : mServers) {
				lServer.addEngine(mEngine);
				List<WebSocketPlugIn> lPlugIns = lPluginMap.get(lServer.getId());
				for (WebSocketPlugIn lPlugIn : lPlugIns) {
					lServer.getPlugInChain().addPlugIn(lPlugIn);
				}
			}
			if (mLog.isInfoEnabled()) {
				mLog.info("Plugins initialized.");
			}

			Map<String, List<WebSocketFilter>> lFilterMap = lInitializer.initializeFilters();
			if (mLog.isDebugEnabled()) {
				mLog.debug("Initializing filters...");
			}
			for (WebSocketServer lServer : mServers) {
				lServer.addEngine(mEngine);
				List<WebSocketFilter> lFilters = lFilterMap.get(lServer.getId());
				for (WebSocketFilter lFilter : lFilters) {
					lServer.getFilterChain().addFilter(lFilter);
				}
			}
			if (mLog.isInfoEnabled()) {
				mLog.info("Filters initialized.");
			}

			boolean lEngineStarted = false;

			// first start the engine
			if (mLog.isDebugEnabled()) {
				mLog.debug("Starting engine '" + mEngine.getId() + "'...");
			}

			try {
				mEngine.startEngine();
				lEngineStarted = true;
			} catch (Exception ex) {
				mLog.error("Starting engine '" + mEngine.getId() + "' failed ("
						+ ex.getClass().getSimpleName() + ": "
						+ ex.getMessage() + ").");
			}

			// do not start any servers if engine could not be started
			if (lEngineStarted) {
				// now start the servers
				if (mLog.isDebugEnabled()) {
					mLog.debug("Starting servers...");
				}
				for (WebSocketServer lServer : mServers) {
					try {
						lServer.startServer();
					} catch (Exception ex) {
						mLog.error("Starting server '" + lServer.getId() + "' failed ("
								+ ex.getClass().getSimpleName() + ": "
								+ ex.getMessage() + ").");
					}
				}

				if (mLog.isInfoEnabled()) {
					mLog.info("jWebSocket server startup complete");
				}

				// if everything went fine...
				JWebSocketInstance.setStatus(JWebSocketInstance.STARTED);
			} else {

				// if engine couldn't be started due to whatever reasons...
				JWebSocketInstance.setStatus(JWebSocketInstance.SHUTTING_DOWN);
			}


		} catch (WebSocketException ex) {
			if (mLog != null) {
				if (mLog.isDebugEnabled()) {
					mLog.debug("Exception during startup", ex);
				}
			} else {
				System.out.println(ex.getClass().getSimpleName() 
						+ " during jWebSocket Server startup: "
						+ ex.getMessage());
			}
			if (mLog != null && mLog.isInfoEnabled()) {
				mLog.info("jWebSocketServer failed to start.");
			}

			JWebSocketInstance.setStatus(JWebSocketInstance.SHUTTING_DOWN);
		}

	}

	public static void stop() {

		JWebSocketInstance.setStatus(JWebSocketInstance.STOPPING);

		if (mLog != null && mLog.isDebugEnabled()) {
			mLog.debug("Stopping jWebSocket Sub System...");
		}

		// String lEngineId = "?";
		// stop engine if previously started successfully
		if (mEngine != null) {
			// now stop the servers
			if (mLog != null && mLog.isDebugEnabled()) {
				mLog.debug("Stopping engine...");
			}
			try {
				mEngine.stopEngine(CloseReason.SHUTDOWN);
				if (mLog != null && mLog.isInfoEnabled()) {
					mLog.info("jWebSocket engine '" + mEngine.getId() + "' stopped");
				}
			} catch (WebSocketException ex) {
				if (mLog != null) {
					mLog.error("Stopping engine: " + ex.getMessage());
				}
			}
		}

		if (mServers != null) {
			// now stop the servers
			if (mLog != null && mLog.isDebugEnabled()) {
				mLog.debug("Stopping servers...");
			}
			for (WebSocketServer lServer : mServers) {
				try {
					lServer.stopServer();
					if (mLog != null && mLog.isInfoEnabled()) {
						mLog.info("jWebSocket server '" + lServer.getId() + "' stopped");
					}
				} catch (WebSocketException ex) {
					if (mLog != null) {
						mLog.error("Stopping server: " + ex.getMessage());
					}
				}
			}
		}

		if (mLog != null && mLog.isInfoEnabled()) {
			mLog.info("jWebSocket Server Sub System stopped.");
		}
		Logging.exitLogs();

		JWebSocketInstance.setStatus(JWebSocketInstance.STOPPED);
	}

	public static WebSocketEngine getEngine() {
		return mEngine;
	}

	public static List<WebSocketServer> getServers() {
		return mServers;
	}

	/**
	 * Returns the server identified by it's id or <tt>null</tt> if no server
	 * with that id could be found in the factory.
	 * @param aId id of the server to be returned.
	 * @return WebSocketServer with the given id or <tt>null</tt> if not found.
	 */
	public static WebSocketServer getServer(String aId) {
		if (aId != null && mServers != null) {
			for (WebSocketServer lServer : mServers) {
				if (lServer != null && aId.equals(lServer.getId())) {
					return lServer;
				}
			}
		}
		return null;
	}
}
