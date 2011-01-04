//	---------------------------------------------------------------------------
//	jWebSocket - Server Configuration Constants
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
package org.jwebsocket.config;

/**
 * Provides a global shared container for the jWebSocket configuration settings.
 *
 * @author aschulze
 * @version $Id: JWebSocketServerConstants.java 624 2010-07-06 12:28:44Z fivefeetfurther $
 */
public final class JWebSocketServerConstants {

	/**
	 * Current version string of the jWebSocket package.
	 */
	public static final String VERSION_STR = "0.10.0818 beta";
	/**
	 * Namespace base for tokens and plug-ins.
	 */
	public static final String NS_BASE = "org.jWebSocket";
	/**
	 * constant for default installation
	 */
	public static final String DEFAULT_INSTALLATION = "prod";
	/**
	 * Constant for JWEBSOCKET_HOME
	 */
	public static final String JWEBSOCKET_HOME = "JWEBSOCKET_HOME";
	/**
	 * Constant for CATALINA_HOME
	 */
	public static final String CATALINA_HOME = "CATALINA_HOME";
	/**
	 * Constant for jWebSocket.xml configuration file
	 */
	public static final String JWEBSOCKET_XML = "jWebSocket.xml";
	/**
	 * Default engine for jWebSocket server.
	 */
	public static String DEFAULT_ENGINE = "tcp";
}
