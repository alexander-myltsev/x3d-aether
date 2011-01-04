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
package org.jwebsocket.factory;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.log4j.Logger;
import org.jwebsocket.api.EngineConfiguration;
import org.jwebsocket.api.ServerConfiguration;
import org.jwebsocket.api.WebSocketEngine;
import org.jwebsocket.api.WebSocketFilter;
import org.jwebsocket.api.WebSocketInitializer;
import org.jwebsocket.api.WebSocketPlugIn;
import org.jwebsocket.api.WebSocketServer;
import org.jwebsocket.filters.custom.CustomTokenFilter;
import org.jwebsocket.filters.system.SystemFilter;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.plugins.flashbridge.FlashBridgePlugIn;
import org.jwebsocket.plugins.rpc.RPCPlugIn;
import org.jwebsocket.plugins.streaming.StreamingPlugIn;
import org.jwebsocket.plugins.system.SystemPlugIn;
import org.jwebsocket.server.TokenServer;
import org.jwebsocket.tcp.engines.TCPEngine;

/**
 * Abstract initializer class
 * @author puran
 * @version $Id: AbstractJWebSocketInitializer.java 437 2010-05-03 22:10:20Z mailtopuran $
 */
public abstract class AbstractJWebSocketInitializer implements WebSocketInitializer {

	private static Logger log = Logging.getLogger(AbstractJWebSocketInitializer.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializeLogging() {
	}

	/**
	 * Initialize the engine to be started based on configuration.
	 * @return the initialized engine ready to start
	 */
	@Override
	public WebSocketEngine initializeEngine() {
		if (log.isDebugEnabled()) {
			log.debug("Instantiating engine...");
		}
		EngineConfiguration config = getEngineConfiguration();
		WebSocketEngine newEngine = null;
		try {
			newEngine = new TCPEngine(config);
			//newEngine = new NettyEngine(config);
		} catch (Exception e) {
			System.out.println("Error instantiating engine: " + e.getMessage());
			System.exit(0);
		}
		if (log.isInfoEnabled()) {
			log.info("Engine " + newEngine.getId() + " instantiated.");
		}
		return newEngine;
	}

	/**
	 * Initializes all the servers configured via jWebSocket configuration
	 * @return the list of initialized servers
	 */
	@Override
	public List<WebSocketServer> initializeServers() {
		if (log.isDebugEnabled()) {
			log.debug("Instantiating default servers...");
		}
		List<WebSocketServer> servers = new FastList<WebSocketServer>();
		// instantiate the Token server by default
		ServerConfiguration config = getServerConfiguration();
		TokenServer tokenServer = new TokenServer(config);
		servers.add(tokenServer);
		if (log.isInfoEnabled()) {
			log.info("Default server " + tokenServer.getId() + " instantiated.");
		}

		List<WebSocketServer> customServers = initializeCustomServers();
		if (customServers != null) {
			if (log.isDebugEnabled()) {
				log.debug("Instantiating custom servers...");
			}
			servers.addAll(customServers);
			if (log.isInfoEnabled()) {
				log.info("Custom servers instantiated.");
			}
		}
		return servers;
	}

	/**
	 * intialize the plugins as per the serverss
	 * @return the FastMap of server id to list of plugins
	 */
	@Override
	public Map<String, List<WebSocketPlugIn>> initializePlugins() {
		if (log.isDebugEnabled()) {
			log.debug("Instantiating default plug-ins...");
		}
		Map<String, List<WebSocketPlugIn>> pluginMap = new FastMap<String, List<WebSocketPlugIn>>();
		List<WebSocketPlugIn> defaultPlugins = new FastList<WebSocketPlugIn>();

		defaultPlugins.add(new SystemPlugIn());
		defaultPlugins.add(new RPCPlugIn());
		defaultPlugins.add(new StreamingPlugIn());
		defaultPlugins.add(new FlashBridgePlugIn());

		pluginMap.put("ts0", defaultPlugins);
		if (log.isInfoEnabled()) {
			log.info("Default plug-ins instantiated.");
		}

		if (log.isDebugEnabled()) {
			log.debug("Instantiating custom plug-ins...");
		}

		Map<String, List<WebSocketPlugIn>> customPluginMap = initializeCustomPlugins();
		for (Map.Entry<String, List<WebSocketPlugIn>>  entry : customPluginMap.entrySet()) {
			String id = entry.getKey();
			if (pluginMap.containsKey(id)) {
				pluginMap.get(id).addAll(entry.getValue());
			} else {
				pluginMap.put(id, entry.getValue());
			}
		}
		if (log.isInfoEnabled()) {
			log.info("Custom plug-ins instantiated.");
		}
		return pluginMap;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, List<WebSocketFilter>> initializeFilters() {

		if (log.isDebugEnabled()) {
			log.debug("Instantiating default filters...");
		}
		Map<String, List<WebSocketFilter>> filterMap = new FastMap<String, List<WebSocketFilter>>();
		List<WebSocketFilter> defaultFilters = new FastList<WebSocketFilter>();
		defaultFilters.add(new SystemFilter("systemFilter"));
		defaultFilters.add(new CustomTokenFilter("userFilter"));

		filterMap.put("ts0", defaultFilters);
		if (log.isInfoEnabled()) {
			log.info("Default filters instantiated.");
		}

		if (log.isDebugEnabled()) {
			log.debug("Instantiating custom filters...");
		}
		Map<String, List<WebSocketFilter>> customFilterMap = initializeCustomFilters();
		for (Map.Entry<String, List<WebSocketFilter>>  entry : customFilterMap.entrySet()) {
			String id = entry.getKey();
			if (filterMap.containsKey(id)) {
				filterMap.get(id).addAll(entry.getValue());
			} else {
				filterMap.put(id, entry.getValue());
			}
		}
		
		if (log.isInfoEnabled()) {
			log.info("Custom filters instantiated.");
		}
		return filterMap;
	}

	/**
	 * Allow subclass of this class to initialize custom plugins.
	 * @return the FastMap of custom plugins to server id.
	 */
	public abstract Map<String, List<WebSocketPlugIn>> initializeCustomPlugins();

	/**
	 * Allow the subclass of this class to initialize custom servers
	 * @return the list of custom servers
	 */
	public abstract List<WebSocketServer> initializeCustomServers();

	/**
	 * Allow the subclass of this class to initialize custom filters
	 * @return the list of custom filters to server id
	 */
	public abstract Map<String, List<WebSocketFilter>> initializeCustomFilters();

	/**
	 * 
	 * @return
	 */
	public abstract EngineConfiguration getEngineConfiguration();

	/**
	 *
	 * @return
	 */
	public abstract ServerConfiguration getServerConfiguration();
}
