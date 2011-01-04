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
package org.jwebsocket.config.xml;

import org.jwebsocket.api.EngineConfiguration;
import org.jwebsocket.kit.WebSocketRuntimeException;

import java.util.List;
import org.jwebsocket.config.Config;

/**
 * Class that represents the engine config
 *
 * @author puran
 * @version $Id: EngineConfig.java 616 2010-07-01 08:04:51Z fivefeetfurther $
 */
public final class EngineConfig implements Config, EngineConfiguration {

	private final String id;
	private final String name;
	private final String jar;
	private final int port;
	private final int timeout;
	private final int maxframesize;
	private final List<String> domains;

	/**
	 * Constructor for engine
	 *
	 * @param id           the engine id
	 * @param name         the name of the engine
	 * @param jar          the jar file name
	 * @param port         the port number where engine runs
	 * @param timeout      the timeout value
	 * @param maxFrameSize the maximum frame size that engine will
	 *                     receive without closing the connection
	 * @param domains      list of domain names
	 */
	public EngineConfig(String id, String name, String jar, int port, int timeout,
			int maxFrameSize, List<String> domains) {
		this.id = id;
		this.name = name;
		this.jar = jar;
		this.port = port;
		this.timeout = timeout;
		this.maxframesize = maxFrameSize;
		this.domains = domains;
		validate();
	}

	/**
	 * @return the id
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @return the jar
	 */
	@Override
	public String getJar() {
		return jar;
	}

	/**
	 * @return the port
	 */
	@Override
	public int getPort() {
		return port;
	}

	/**
	 * @return the timeout
	 */
	@Override
	public int getTimeout() {
		return timeout;
	}

	/**
	 * @return the max frame size
	 */
	@Override
	public int getMaxFramesize() {
		return maxframesize;
	}

	/**
	 * @return the domains
	 */
	@Override
	public List<String> getDomains() {
		return domains;
	}

	/**
	 * validate the engine configuration
	 *
	 * @throws WebSocketRuntimeException if any of the engine configuration is mising
	 */
	@Override
	public void validate() {
		if ((id != null && id.length() > 0)
				&& (name != null && name.length() > 0)
				&& (jar != null && jar.length() > 0)
				&& (domains != null && domains.size() > 0)
				&& port >= 1024
				&& timeout >= 0) {
			return;
		}
		throw new WebSocketRuntimeException(
				"Missing one of the engine configuration, "
				+ "please check your configuration file");
	}
}
