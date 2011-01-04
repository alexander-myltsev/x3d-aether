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

import org.jwebsocket.api.ServerConfiguration;
import org.jwebsocket.config.Config;
import org.jwebsocket.kit.WebSocketRuntimeException;

/**
 * Represents the server config 
 * @author puran
 * @version $Id: ServerConfig.java 616 2010-07-01 08:04:51Z fivefeetfurther $
 * 
 */
public final class ServerConfig implements Config, ServerConfiguration {

	private final String id;
	private final String name;
	private final String jar;

	public ServerConfig(String id, String name, String jar) {
		this.id = id;
		this.name = name;
		this.jar = jar;
		//validate the server configuration
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
	 * {@inheritDoc}
	 */
	@Override
	public void validate() {
		if ((id != null && id.length() > 0)
				&& (name != null && name.length() > 0)
				&& (jar != null && jar.length() > 0)) {
			return;
		}
		throw new WebSocketRuntimeException(
				"Missing one of the server configuration, please check your configuration file");
	}
}
