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

import org.apache.log4j.Logger;
import org.jwebsocket.logging.Logging;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import javolution.util.FastList;
import javolution.util.FastMap;

import org.jwebsocket.api.EngineConfiguration;
import org.jwebsocket.api.ServerConfiguration;
import org.jwebsocket.api.WebSocketEngine;
import org.jwebsocket.api.WebSocketFilter;
import org.jwebsocket.api.WebSocketInitializer;
import org.jwebsocket.api.WebSocketPlugIn;
import org.jwebsocket.api.WebSocketServer;
import org.jwebsocket.config.JWebSocketConfig;
import org.jwebsocket.config.LoggingConfig;
import org.jwebsocket.config.xml.EngineConfig;
import org.jwebsocket.config.xml.FilterConfig;
import org.jwebsocket.config.xml.PluginConfig;
import org.jwebsocket.config.xml.ServerConfig;

/**
 * Intialize the engine, servers and plugins based on jWebSocket.xml
 * configuration
 * 
 * @author puran
 * @version $Id: JWebSocketXmlConfigInitializer.java 424 2010-05-01 19:11:04Z
 *          mailtopuran $
 * 
 */
public final class JWebSocketXmlConfigInitializer implements
		WebSocketInitializer {

	// don't initialize logger here! Will be initialized with loaded settings!
	private static Logger mLog = null;
	private final JWebSocketJarClassLoader mClassLoader = new JWebSocketJarClassLoader();
	private JWebSocketConfig mConfig;

	/**
	 * private constructor
	 */
	private JWebSocketXmlConfigInitializer(JWebSocketConfig aConfig) {
		this.mConfig = aConfig;
	}

	/**
	 * Returns the initializer object
	 * 
	 * @param aConfig
	 *            the jWebSocket config
	 * @return the initializer object
	 */
	public static JWebSocketXmlConfigInitializer getInitializer(
			JWebSocketConfig aConfig) {
		return new JWebSocketXmlConfigInitializer(aConfig);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializeLogging() {
		LoggingConfig lLoggingConfig = mConfig.getLoggingConfig();
		// initialize log4j logging engine
		// BEFORE instantiating any jWebSocket classes
		Logging.initLogs(lLoggingConfig.getLevel(), lLoggingConfig.getAppender(),
				lLoggingConfig.getFilename(), lLoggingConfig.getPattern(),
				lLoggingConfig.getBufferSize(),
				new String[]{"%JWEBSOCKET_HOME%/logs", "%CATALINA_HOME%/logs"});
		mLog = Logging.getLogger(JWebSocketXmlConfigInitializer.class);
		if (mLog.isDebugEnabled()) {
			mLog.debug("Logging settings"
					+ ": appender: " + lLoggingConfig.getAppender()
					+ ", filename: " + lLoggingConfig.getFilename()
					+ ", level: " + lLoggingConfig.getLevel()
					+ ", buffersize: " + lLoggingConfig.getBufferSize()
					+ ", pattern: " + lLoggingConfig.getPattern());
		}
		if (mLog.isDebugEnabled()) {
			mLog.debug("Starting jWebSocket Server Sub System...");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public WebSocketEngine initializeEngine() {
		WebSocketEngine newEngine = null;
		EngineConfig engineConfig = mConfig.getEngines().get(0);
		String jarFilePath = "-";
		try {
			Class lEngineClass = null;

			// try to load engine from classpath first, 
			// could be located in server bundle
			try {
				lEngineClass = Class.forName(engineConfig.getName());
				if (mLog.isDebugEnabled()) {
					mLog.debug("Engine '" + engineConfig.getName() + "' loaded from classpath.");
				}
			} catch (ClassNotFoundException ex) {
				// in case of a class not found exception we DO NOT want to
				// show the exception but subsequently load the class from
				if (mLog.isDebugEnabled()) {
					mLog.debug("Engine '" + engineConfig.getName() + "' not yet in classpath, hence trying to load from file...");
				}
			}

			// if not in classpath...
			// try to load engine from given .jar file
			if (lEngineClass == null) {
				jarFilePath = JWebSocketConfig.getLibraryFolderPath(engineConfig.getJar());
				// jarFilePath may be null if .jar is included in server bundle
				if (jarFilePath != null) {
					if (mLog.isDebugEnabled()) {
						mLog.debug("Loading engine '" + engineConfig.getName() + "' from '" + jarFilePath + "'...");
					}
					mClassLoader.addFile(jarFilePath);
					lEngineClass = (Class<WebSocketEngine>) mClassLoader.loadClass(engineConfig.getName());
				}
			}

			// if class found
			// try to create an instance
			if (lEngineClass != null) {
				Constructor<WebSocketEngine> ctor = lEngineClass.getDeclaredConstructor(EngineConfiguration.class);
				ctor.setAccessible(true);
				newEngine = ctor.newInstance(new Object[]{engineConfig});
				if (mLog.isDebugEnabled()) {
					mLog.debug("Engine '" + engineConfig.getId() + "' successfully instantiated.");
				}
			} else {
				mLog.error("jWebSocket engine class " + engineConfig.getName() + " could not be loaded.");
			}
		} catch (MalformedURLException e) {
			mLog.error("Couldn't load the jar file for engine, make sure jar file exists or name is correct", e);
		} catch (ClassNotFoundException e) {
			mLog.error("Engine class '" + engineConfig.getName() + "'@'" + jarFilePath + "' not found", e);
		} catch (InstantiationException e) {
			mLog.error("Engine class could not be instantiated", e);
		} catch (IllegalAccessException e) {
			mLog.error("Illegal Access Exception while intializing engine", e);
		} catch (NoSuchMethodException e) {
			mLog.error("No Constructor found with given 3 arguments", e);
		} catch (InvocationTargetException e) {
			mLog.error("Exception invoking engine object", e);
		}

		return newEngine;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<WebSocketServer> initializeServers() {
		List<WebSocketServer> lServers = new FastList<WebSocketServer>();
		List<ServerConfig> lServerConfigs = mConfig.getServers();
		for (ServerConfig lServerConfig : lServerConfigs) {
			WebSocketServer lServer = null;
			String lJarFilePath = "-";
			try {
				Class lServerClass = null;

				// try to load server from classpath first,
				// could be located in server bundle
				try {
					lServerClass = Class.forName(lServerConfig.getName());
					if (mLog.isDebugEnabled()) {
						mLog.debug("Server '" + lServerConfig.getName() + "' loaded from classpath.");
					}
				} catch (ClassNotFoundException ex) {
					// in case of a class not found exception we DO NOT want to
					// show the exception but subsequently load the class from
					if (mLog.isDebugEnabled()) {
						mLog.debug("Server '" + lServerConfig.getName() + "' not yet in classpath, hence trying to load from file...");
					}
				}

				// if not in classpath...
				// try to load server from given .jar file
				if (lServerClass == null) {
					lJarFilePath = JWebSocketConfig.getLibraryFolderPath(lServerConfig.getJar());
					// jarFilePath may be null if .jar is included in server bundle
					if (lJarFilePath != null) {
						if (mLog.isDebugEnabled()) {
							mLog.debug("Loading server '" + lServerConfig.getName() + "' from '" + lJarFilePath + "'...");
						}
						mClassLoader.addFile(lJarFilePath);
						lServerClass = (Class<WebSocketServer>) mClassLoader.loadClass(lServerConfig.getName());
					}
				}

				// if class found
				// try to create an instance
				if (lServerClass != null) {
					Constructor<WebSocketServer> ctor = lServerClass.getDeclaredConstructor(ServerConfiguration.class);
					ctor.setAccessible(true);
					lServer = ctor.newInstance(new Object[]{lServerConfig});
					if (mLog.isDebugEnabled()) {
						mLog.debug("Server '" + lServerConfig.getId() + "' successfully instantiated.");
					}
					// add the initialized server to the list
					lServers.add(lServer);
				} else {
					mLog.error("jWebSocket server class " + lServerConfig.getName() + " could not be loaded.");
				}
			} catch (MalformedURLException e) {
				mLog.error(
						"Couldn't load the jar file for server, make sure jar file '" + lJarFilePath + "' exists and name is correct.",
						e);
			} catch (ClassNotFoundException e) {
				mLog.error(
						"Server class '" + lServerConfig.getName() + "'@'" + lJarFilePath + "' not found.", e);
			} catch (InstantiationException e) {
				mLog.error(
						"Server class '" + lServerConfig.getName() + "' could not be instantiated.", e);
			} catch (IllegalAccessException e) {
				mLog.error(
						"Illegal Access Exception while intializing server '" + lServerConfig.getName() + "'.", e);
			} catch (NoSuchMethodException e) {
				mLog.error(
						"No constructor found with given 1 arguments", e);
			} catch (InvocationTargetException e) {
				mLog.error(
						"Exception invoking server object.", e);
			}
		}
		return lServers;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, List<WebSocketPlugIn>> initializePlugins() {
		Map<String, List<WebSocketPlugIn>> lPluginMap = new FastMap<String, List<WebSocketPlugIn>>();

		// populate the plugin FastMap with server id and empty list
		for (ServerConfig lServerConfig : mConfig.getServers()) {
			lPluginMap.put(lServerConfig.getId(), new FastList<WebSocketPlugIn>());
		}
		// now initialize the pluin
		for (PluginConfig lPluginConfig : mConfig.getPlugins()) {
			try {
				Class lPluginClass = null;

				// try to load plug-in from classpath first,
				// could be located in server bundle
				try {
					lPluginClass = Class.forName(lPluginConfig.getName());
					if (mLog.isDebugEnabled()) {
						mLog.debug("Plug-in '" + lPluginConfig.getName() + "' loaded from classpath.");
					}
				} catch (ClassNotFoundException ex) {
					// in case of a class not found exception we DO NOT want to
					// show the exception but subsequently load the class from
					if (mLog.isDebugEnabled()) {
						mLog.debug("Plug-in '" + lPluginConfig.getName() + "' not yet in classpath, hence trying to load from file...");
					}
				}

				// if not in classpath...
				// try to load plug-in from given .jar file
				if (lPluginClass == null) {
					String jarFilePath = JWebSocketConfig.getLibraryFolderPath(lPluginConfig.getJar());
					// jarFilePath may be null if .jar is included in server bundle
					if (jarFilePath != null) {
						mClassLoader.addFile(jarFilePath);
						if (mLog.isDebugEnabled()) {
							mLog.debug("Loading plug-in '" + lPluginConfig.getName() + "' from '" + jarFilePath + "'...");
						}
						lPluginClass = (Class<WebSocketPlugIn>) mClassLoader.loadClass(lPluginConfig.getName());
					}
				}

				// if class found
				// try to create an instance
				if (lPluginClass != null) {
					/*
					Constructor<WebSocketPlugIn> ctor = pluginClass.getDeclaredConstructor();
					ctor.setAccessible(true);
					Object lObj = ctor.newInstance(new Object[]{});
					log.debug("lObj.classname = " + lObj.getClass().getName());
					Object plugin = null;
					try {
					plugin = lObj;
					log.info(
					"lObj instanceof WebSocketPlugIn " + ( lObj instanceof WebSocketPlugIn ? "YES" : "NO" )
					);
					} catch (Exception ex) {
					log.error(
					ex.getClass().getSimpleName() + " while instantiating class '" + pluginConfig.getName() + "'.");
					}
					 */
					WebSocketPlugIn lPlugIn = (WebSocketPlugIn) lPluginClass.newInstance();
					// TODO: Also set id and name once these are available
					lPlugIn.addAllSettings(lPluginConfig.getSettings());

					if (mLog.isDebugEnabled()) {
						mLog.debug("Plug-in '" + lPluginConfig.getId() + "' successfully instantiated.");
					}

					// now add the plugin to plugin map based on server ids
					for (String lServerId : lPluginConfig.getServers()) {
						List<WebSocketPlugIn> lPlugIns = lPluginMap.get(lServerId);
						if (lPlugIns != null) {
							lPlugIns.add((WebSocketPlugIn) lPlugIn);
						}
					}
				}

			} catch (MalformedURLException ex) {
				mLog.error(
						"Couldn't load the jar file for plugin, make sure the jar file exists and the name is correct.", ex);
			} catch (ClassNotFoundException ex) {
				mLog.error(
						"Plugin class '" + lPluginConfig.getName() + "' not found.", ex);
			} catch (InstantiationException ex) {
				mLog.error(
						"Plugin class '" + lPluginConfig.getName() + "' could not be instantiated.", ex);
			} catch (IllegalAccessException ex) {
				mLog.error(
						"Illegal Access Exception while instantiating plugin.", ex);
			} catch (Exception ex) {
				mLog.error(
						ex.getClass().getSimpleName() + " while instantiating plugin '" + lPluginConfig.getName() + "'.", ex);
			}
		}
		return lPluginMap;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, List<WebSocketFilter>> initializeFilters() {
		Map<String, List<WebSocketFilter>> lFilterMap = new FastMap<String, List<WebSocketFilter>>();

		// populate the filter FastMap with server id and empty list
		for (ServerConfig lServerConfig : mConfig.getServers()) {
			lFilterMap.put(lServerConfig.getId(),
					new FastList<WebSocketFilter>());
		}
		// now initialize the filter
		for (FilterConfig lFilterConfig : mConfig.getFilters()) {
			try {
				Class lFilterClass = null;

				// try to load filter from classpath first,
				// could be located in server bundle
				try {
					lFilterClass = Class.forName(lFilterConfig.getName());
					if (mLog.isDebugEnabled()) {
						mLog.debug("Filter '" + lFilterConfig.getName() + "' loaded from classpath.");
					}
				} catch (ClassNotFoundException ex) {
					// in case of a class not found exception we DO NOT want to
					// show the exception but subsequently load the class from
					if (mLog.isDebugEnabled()) {
						mLog.debug("Filter '" + lFilterConfig.getName() + "' not yet in classpath, hence trying to load from file...");
					}
				}

				// if not in classpath...
				// try to load plug-in from given .jar file
				if (lFilterClass == null) {
					String jarFilePath = JWebSocketConfig.getLibraryFolderPath(lFilterConfig.getJar());
					// jarFilePath may be null if .jar is included in server bundle
					if (jarFilePath != null) {
						mClassLoader.addFile(jarFilePath);
						if (mLog.isDebugEnabled()) {
							mLog.debug("Loading filter '" + lFilterConfig.getName() + "' from '" + jarFilePath + "'...");
						}
						lFilterClass = (Class<WebSocketFilter>) mClassLoader.loadClass(lFilterConfig.getName());
					}
				}

				// if class found
				// try to create an instance
				if (lFilterClass != null) {
					Constructor<WebSocketFilter> lConstr = lFilterClass.getDeclaredConstructor(String.class);
					lConstr.setAccessible(true);

					WebSocketFilter lFilter = lConstr.newInstance(new Object[]{lFilterConfig.getId()});
					// TODO: Also set settings, id and name once these are available
					// filter.addAllSettings(filterConfig.getSettings());

					if (mLog.isDebugEnabled()) {
						mLog.debug("Filter '" + lFilterConfig.getName() + "' successfully instantiated.");
					}
					// now add the filter to filter FastMap based on server ids
					for (String lServerId : lFilterConfig.getServers()) {
						List<WebSocketFilter> lFilters = lFilterMap.get(lServerId);
						if (lFilters != null) {
							lFilters.add(lFilter);
						}
					}
				}

			} catch (MalformedURLException e) {
				mLog.error(
						"Couldn't Load the jar file for filter, make sure jar file exists and name is correct.",
						e);
			} catch (ClassNotFoundException e) {
				mLog.error(
						"Filter class not found.", e);
			} catch (InstantiationException e) {
				mLog.error(
						"Filter class could not be instantiated.", e);
			} catch (IllegalAccessException e) {
				mLog.error(
						"Illegal Access Exception while intializing filter.", e);
			} catch (NoSuchMethodException e) {
				mLog.error(
						"No Constructor found with given 3 arguments.", e);
			} catch (InvocationTargetException e) {
				mLog.error(
						"Exception invoking filter object.", e);
			}
		}
		return lFilterMap;
	}

}
