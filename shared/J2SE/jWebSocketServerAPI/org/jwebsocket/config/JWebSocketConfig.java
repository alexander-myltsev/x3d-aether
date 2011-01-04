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
package org.jwebsocket.config;

import static org.jwebsocket.config.JWebSocketCommonConstants.DEFAULT_PROTOCOL;
import static org.jwebsocket.config.JWebSocketServerConstants.CATALINA_HOME;
import static org.jwebsocket.config.JWebSocketServerConstants.JWEBSOCKET_HOME;
import static org.jwebsocket.config.JWebSocketServerConstants.JWEBSOCKET_XML;
import static org.jwebsocket.config.JWebSocketServerConstants.DEFAULT_INSTALLATION;

import java.io.File;
import java.net.URI;
import java.net.URL;
import org.jwebsocket.config.xml.*;
import org.jwebsocket.kit.WebSocketRuntimeException;

import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.jwebsocket.logging.Logging;

/**
 * Represents the jWebSocket configuration. This class is immutable and should
 * not be overridden.
 *
 * @author puran
 * @version $Id: JWebSocketConfig.java 345 2010-04-10 20:03:48Z fivefeetfurther$
 */
public final class JWebSocketConfig implements Config {

	// DON'T SET LOGGER HERE! NEEDS TO BE INITIALIZED FIRST!
	private static Logger mLog = null;

	private final String mInstallation;
	private final String mProtocol;
	private final String jWebSocketHome;
	private final String mLibraryFolder;
	private final String mInitializer;
	private final List<EngineConfig> mEngines;
	private final List<ServerConfig> mServers;
	private final List<UserConfig> mUsers;
	private final List<PluginConfig> mPlugins;
	private final List<FilterConfig> mFilters;
	private final LoggingConfig mLoggingConfig;
	private final List<RightConfig> mGlobalRights;
	private final List<RoleConfig> mGlobalRoles;
	private static JWebSocketConfig mConfig = null;

	/**
	 * @return the installation
	 */
	public String getInstallation() {
		if (mInstallation == null || mInstallation.length() == 0) {
			return DEFAULT_INSTALLATION;
		}
		return mInstallation;
	}

	/**
	 * @return the protocol
	 */
	public String getProtocol() {
		if (mProtocol == null || mProtocol.length() == 0) {
			return DEFAULT_PROTOCOL;
		}
		return mProtocol;
	}

	/**
	 * @return the jWebSocketHome
	 */
	public String getjWebSocketHome() {
		return jWebSocketHome;
	}

	/**
	 * @return the libraryFolder
	 */
	public String getLibraryFolder() {
		return mLibraryFolder;
	}

	/**
	 * @return the initializer
	 */
	public String getInitializer() {
		return mInitializer;
	}

	/**
	 * @return the config
	 */
	public static JWebSocketConfig getConfig() {
		return mConfig;
	}

	/**
	 * private constructor used by the builder
	 */
	private JWebSocketConfig(Builder builder) {
		if (builder.engines == null || builder.servers == null
				|| builder.users == null || builder.globalRights == null
				|| builder.globalRoles == null || builder.filters == null
				|| builder.loggingConfig == null) {
			throw new WebSocketRuntimeException(
					"Configuration is not loaded completely.");
		}
		mInstallation = builder.installation;
		mProtocol = builder.protocol;
		jWebSocketHome = builder.jWebSocketHome;
		mLibraryFolder = builder.libraryFolder;
		mInitializer = builder.initializer;
		mEngines = builder.engines;
		mServers = builder.servers;
		mUsers = builder.users;
		mPlugins = builder.plugins;
		mFilters = builder.filters;
		mLoggingConfig = builder.loggingConfig;
		mGlobalRights = builder.globalRights;
		mGlobalRoles = builder.globalRoles;
		// validate the config
		validate();
	}

	/**
	 * Config builder class.
	 *
	 * @author puran
	 * @version $Id: JWebSocketConfig.java 596 2010-06-22 17:09:54Z fivefeetfurther $
	 */
	public static class Builder {

		private String installation;
		private String protocol;
		private String jWebSocketHome;
		private String libraryFolder;
		private String initializer;
		private List<EngineConfig> engines;
		private List<ServerConfig> servers;
		private List<UserConfig> users;
		private List<PluginConfig> plugins;
		private List<FilterConfig> filters;
		private LoggingConfig loggingConfig;
		private List<RightConfig> globalRights;
		private List<RoleConfig> globalRoles;

		public Builder addInstallation(String theInstallation) {
			installation = theInstallation;
			return this;
		}

		public Builder addProtocol(String theProtocol) {
			protocol = theProtocol;
			return this;
		}

		public Builder addJWebSocketHome(String theJWebSocketHome) {
			jWebSocketHome = theJWebSocketHome;
			return this;
		}

		public Builder addInitializer(String theInitializer) {
			initializer = theInitializer;
			return this;
		}

		public Builder addLibraryFolder(String theLibraryFolder) {
			libraryFolder = theLibraryFolder;
			return this;
		}

		public Builder addEngines(List<EngineConfig> theEngines) {
			engines = theEngines;
			return this;
		}

		public Builder addServers(List<ServerConfig> theServers) {
			servers = theServers;
			return this;
		}

		public Builder addPlugins(List<PluginConfig> thePlugins) {
			plugins = thePlugins;
			return this;
		}

		public Builder addFilters(List<FilterConfig> theFilters) {
			filters = theFilters;
			return this;
		}

		public Builder addLoggingConfig(List<LoggingConfig> theLoggingConfigs) {
			loggingConfig = theLoggingConfigs.get(0);
			return this;
		}

		public Builder addGlobalRights(List<RightConfig> theRights) {
			globalRights = theRights;
			return this;
		}

		public Builder addGlobalRoles(List<RoleConfig> theRoles) {
			globalRoles = theRoles;
			return this;
		}

		public Builder addUsers(List<UserConfig> theUsers) {
			users = theUsers;
			return this;
		}

		public synchronized JWebSocketConfig buildConfig() {
			if (mConfig == null) {
				mConfig = new JWebSocketConfig(this);
			}
			return mConfig;
		}
	}

	/**
	 * @return the engines
	 */
	public List<EngineConfig> getEngines() {
		return Collections.unmodifiableList(mEngines);
	}

	/**
	 * @return the servers
	 */
	public List<ServerConfig> getServers() {
		return Collections.unmodifiableList(mServers);
	}

	/**
	 * @return the users
	 */
	public List<UserConfig> getUsers() {
		return Collections.unmodifiableList(mUsers);
	}

	/**
	 * @return the plugins
	 */
	public List<PluginConfig> getPlugins() {
		return Collections.unmodifiableList(mPlugins);
	}

	/**
	 * @return the filters
	 */
	public List<FilterConfig> getFilters() {
		return Collections.unmodifiableList(mFilters);
	}

	/**
	 * @return the logging config object
	 */
	public LoggingConfig getLoggingConfig() {
		return mLoggingConfig;
	}

	/**
	 * @return the globalRights
	 */
	public List<RightConfig> getGlobalRights() {
		return Collections.unmodifiableList(mGlobalRights);
	}

	/**
	 * @return the globalRoles
	 */
	public List<RoleConfig> getGlobalRoles() {
		return Collections.unmodifiableList(mGlobalRoles);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validate() {
		if ((mEngines == null || mEngines.isEmpty())
				|| (mServers == null || mServers.isEmpty())
				|| (mUsers == null || mUsers.isEmpty())
				|| (mPlugins == null || mPlugins.isEmpty())
				|| (mFilters == null || mFilters.isEmpty())
				|| (mLoggingConfig == null)
				|| (mGlobalRights == null || mGlobalRights.isEmpty())
				|| (mGlobalRoles == null || mGlobalRoles.isEmpty())) {
			throw new WebSocketRuntimeException(
					"Missing one of the server configuration, please check your configuration file");
		}
	}

	private static void checkLogs() {
		if (mLog == null) {
			mLog = Logging.getLogger(JWebSocketConfig.class);
		}
	}

	/**
	 * private method that checks the path of the jWebSocket.xml file
	 *
	 * @return the path to jWebSocket.xml
	 */
	public static String getConfigurationPath() {
		String lWebSocketXML = null;
		String lWebSocketHome = null;
		String lFileSep = System.getProperty("file.separator");
		File lFile;

		// try to obtain JWEBSOCKET_HOME environment variable
		lWebSocketHome = System.getenv(JWEBSOCKET_HOME);
		if (lWebSocketHome != null) {
			// append trailing slash if needed
			if (!lWebSocketHome.endsWith(lFileSep)) {
				lWebSocketHome += lFileSep;
			}
			// jWebSocket.xml can be located in %JWEBSOCKET_HOME%/conf
			lWebSocketXML = lWebSocketHome + "conf" + lFileSep + JWEBSOCKET_XML;
			lFile = new File(lWebSocketXML);
			if (lFile.exists()) {
				return lWebSocketXML;
			}
		}

		// try to obtain CATALINA_HOME environment variable
		lWebSocketHome = System.getenv(CATALINA_HOME);
		if (lWebSocketHome != null) {
			// append trailing slash if needed
			if (!lWebSocketHome.endsWith(lFileSep)) {
				lWebSocketHome += lFileSep;
			}
			// jWebSocket.xml can be located in %CATALINA_HOME%/conf
			lWebSocketXML = lWebSocketHome + "conf" + lFileSep + JWEBSOCKET_XML;
			lFile = new File(lWebSocketXML);
			if (lFile.exists()) {
				return lWebSocketXML;
			}
		}

		// finally try to find config file at %CLASSPATH%/conf/
		URL lURL = Thread.currentThread().getContextClassLoader().getResource("conf/" + JWEBSOCKET_XML);
		if (lURL != null) {
/*
			lWebSocketXML = lURL.getFile();
			System.out.println("WebSocketXML - Filename: " + lWebSocketXML);
			lFile = new File(lWebSocketXML);
			if (lFile.exists()) {
				return lWebSocketXML;
			}
*/
			try {
				URI lFilename = lURL.toURI();
				// System.out.println("URI Filename: " + lFilename);
				lFile = new File(lFilename);
				if (lFile.exists()) {
					lWebSocketXML = lFile.getPath();
					return lWebSocketXML;
				}
			} catch (Exception ex) {
				//TODO: log exception
				// System.out.println(ex.getClass().getSimpleName() + ": " + ex.getMessage());
			}
		}

		return null;
	}

	/**
	 * private method that checks the path of the jWebSocket.xml file
	 *
	 * @return the path to jWebSocket.xml
	 */
	public static String getLibraryFolderPath(String fileName) {
		String lWebSocketLib = null;
		String lWebSocketHome = null;
		String lFileSep = null;
		File lFile = null;

		checkLogs();
		
		// try to load lib from %JWEBSOCKET_HOME%/libs folder
		lWebSocketHome = System.getenv(JWEBSOCKET_HOME);
		lFileSep = System.getProperty("file.separator");
		if (lWebSocketHome != null) {
			// append trailing slash if needed
			if (!lWebSocketHome.endsWith(lFileSep)) {
				lWebSocketHome += lFileSep;
			}
			// jar can to be located in %JWEBSOCKET_HOME%/libs
			lWebSocketLib = lWebSocketHome + "libs" + lFileSep + fileName;
			lFile = new File(lWebSocketLib);
			if (lFile.exists()) {
				if (mLog.isDebugEnabled()) {
					mLog.debug("Found lib at " + lWebSocketLib + "...");
				}
				return lWebSocketLib;
			} else {
				if (mLog.isDebugEnabled()) {
					mLog.debug(fileName + " not found at %" + JWEBSOCKET_HOME + "%/libs.");
				}
			}
		}

		// try to load lib from %CATALINA_HOME%/libs folder
		lWebSocketHome = System.getenv(CATALINA_HOME);
		lFileSep = System.getProperty("file.separator");
		if (lWebSocketHome != null) {
			// append trailing slash if needed
			if (!lWebSocketHome.endsWith(lFileSep)) {
				lWebSocketHome += lFileSep;
			}
			// jars can to be located in %CATALINA_HOME%/lib
			lWebSocketLib = lWebSocketHome + "lib" + lFileSep + fileName;
			lFile = new File(lWebSocketLib);
			if (lFile.exists()) {
				if (mLog.isDebugEnabled()) {
					mLog.debug("Found lib at " + lWebSocketLib + "...");
				}
				return lWebSocketLib;
			} else {
				if (mLog.isDebugEnabled()) {
					mLog.debug(fileName + " not found at %" + CATALINA_HOME + "/lib%.");
				}
			}
		}

		return null;
	}
}
