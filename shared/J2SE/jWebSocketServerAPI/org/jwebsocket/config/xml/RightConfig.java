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
import org.jwebsocket.config.Config;
import org.jwebsocket.kit.WebSocketRuntimeException;
/**
 * immutable class that represents the <tt>right</tt> configuration 
 * @author puran
 * @version $Id: RightConfig.java 596 2010-06-22 17:09:54Z fivefeetfurther $
 *
 */
public final class RightConfig implements Config {

	private final String id;
	private final String namespace;
	private final String description;
	
	/**
	 * default constructor
	 * @param id the right id
	 * @param namespace the right namespace
	 * @param description the description
	 */
	public RightConfig(String id, String namespace, String description) {
		this.id = id;
		this.namespace = namespace;
		this.description = description;
		//validate right config
		validate();
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validate() {
		if ((id != null && id.length() > 0)
				&& (namespace != null && namespace.length() > 0)
				&& (description != null && description.length() > 0)) {
			return;
		}
		throw new WebSocketRuntimeException(
				"Missing one of the right configuration, please check your configuration file");
	}

}
