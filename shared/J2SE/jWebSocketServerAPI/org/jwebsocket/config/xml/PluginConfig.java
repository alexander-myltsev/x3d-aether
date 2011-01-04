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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jwebsocket.config.Config;
import org.jwebsocket.kit.WebSocketRuntimeException;

/**
 * Class that represents the plugin config
 * @author puran
 * @version $Id: PluginConfig.java 596 2010-06-22 17:09:54Z fivefeetfurther $
 * 
 */
public final class PluginConfig implements Config {
	private final String id;
	private final String name;
	private final String jar;
	private final String packageName;
	private final String namespace;
	private final List<String> servers;
	private final Map<String, String> settings;

	/**
	 * default constructor
	 * @param aId the plugin id
	 * @param aName the plugin name
	 * @param aJar the plugin jar
	 * @param aNamespace the namespace
	 * @param settings FastMap of settings key and value
	 */
	public PluginConfig(String aId, String aName, String aPackage, String aJar, String aNamespace,
		 List<String> servers, Map<String, String> settings) {
		this.id = aId;
		this.name = aName;
		this.packageName = aPackage;
		this.jar = aJar;
		this.namespace = aNamespace;
		this.servers = servers;
		this.settings = settings;
		validate();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the package 
	 */
	public String getPackage() {
		return packageName;
	}

	/**
	 * @return the jar
	 */
	public String getJar() {
		return jar;
	}

	/**
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}
	
	/**
	 * @return the list of servers
	 */
	public List<String> getServers() {
		return Collections.unmodifiableList(servers);
	}
	/**
	 * @return the settings
	 */
	public Map<String, String> getSettings() {
		return settings; // (FastMap)(settings.unmodifiable());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validate() {
		if ((id != null && id.length() > 0)
				&& (name != null && name.length() > 0)
				&& (jar != null && jar.length() > 0)
				&& (namespace != null && namespace.length() > 0)) {
			return;
		}
		throw new WebSocketRuntimeException(
				"Missing one of the plugin configuration, please check your configuration file");
	}

}
