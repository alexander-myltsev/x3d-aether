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

import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javolution.util.FastList;
import javolution.util.FastMap;

import org.jwebsocket.config.Config;
import org.jwebsocket.config.ConfigHandler;

/**
 * Config handler for reading plugins configuration
 * @author puran
 * @version $Id: PluginConfigHandler.java 596 2010-06-22 17:09:54Z fivefeetfurther $
 * 
 */
public class PluginConfigHandler implements ConfigHandler {

	private static final String ELEMENT_PLUGIN = "plugin";
	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String PACKAGE = "package";
	private static final String JAR = "jar";
	private static final String NAMESPACE = "ns";
	private static final String SERVERS = "server-assignments";
	private static final String SERVER = "server-assignment";
	private static final String SETTINGS = "settings";
	private static final String SETTING = "setting";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Config processConfig(XMLStreamReader aStreamReader)
			throws XMLStreamException {
		String lId = "", lName = "", lPackage = "", lJar = "", lNamespace = "";
		List<String> lServers = new FastList<String>();
		Map<String, String> lSettings = null;
		while (aStreamReader.hasNext()) {
			aStreamReader.next();
			if (aStreamReader.isStartElement()) {
				String elementName = aStreamReader.getLocalName();
				if (elementName.equals(ID)) {
					aStreamReader.next();
					lId = aStreamReader.getText();
				} else if (elementName.equals(NAME)) {
					aStreamReader.next();
					lName = aStreamReader.getText();
				} else if (elementName.equals(PACKAGE)) {
					aStreamReader.next();
					lPackage = aStreamReader.getText();
				} else if (elementName.equals(JAR)) {
					aStreamReader.next();
					lJar = aStreamReader.getText();
				} else if (elementName.equals(NAMESPACE)) {
					aStreamReader.next();
					lNamespace = aStreamReader.getText();
				} else if (elementName.equals(SETTINGS)) {
					lSettings = getSettings(aStreamReader);
				} else if (elementName.equals(SERVERS)) {
					lServers = getServers(aStreamReader);
				} else {
					// ignore
				}
			}
			if (aStreamReader.isEndElement()) {
				String lElementName = aStreamReader.getLocalName();
				if (lElementName.equals(ELEMENT_PLUGIN)) {
					break;
				}
			}
		}
		return new PluginConfig(lId, lName, lPackage, lJar, lNamespace, lServers, lSettings);
	}

	/**
	 * private method that reads the list of servers from the plugin configuration 
	 * @param streamReader the stream reader object
	 * @return the list of right ids
	 * @throws XMLStreamException if exception while reading
	 */
	private List<String> getServers(XMLStreamReader streamReader)
			throws XMLStreamException {
		List<String> servers = new FastList<String>();
		while (streamReader.hasNext()) {
			streamReader.next();
			if (streamReader.isStartElement()) {
				String elementName = streamReader.getLocalName();
				if (elementName.equals(SERVER)) {
					streamReader.next();
					String server = streamReader.getText();
					servers.add(server);
				}
			}
			if (streamReader.isEndElement()) {
				String elementName = streamReader.getLocalName();
				if (elementName.equals(SERVERS)) {
					break;
				}
			}
		}
		return servers;
	}

	/**
	 * Read the map of plug-in specific settings
	 * @param aStreamReader
	 *            the stream reader object
	 * @return the list of domains for the engine
	 * @throws XMLStreamException
	 *             in case of stream exception
	 */
	private Map<String, String> getSettings(XMLStreamReader aStreamReader)
			throws XMLStreamException {

		Map<String, String> lSettings = new FastMap<String, String>();
		while (aStreamReader.hasNext()) {
			aStreamReader.next();
			if (aStreamReader.isStartElement()) {
				String lElementName = aStreamReader.getLocalName();
				if (lElementName.equals(SETTING)) {
					// TODO: Don't just get first attribute here! Scan for key="xxx"!
					String lKey = aStreamReader.getAttributeValue(0);
					aStreamReader.next();
					String lValue = aStreamReader.getText();
					if (lKey != null && lValue != null) {
						lSettings.put(lKey, lValue);
					}
				}
			}
			if (aStreamReader.isEndElement()) {
				String lElementName = aStreamReader.getLocalName();
				if (lElementName.equals(SETTINGS)) {
					break;
				}
			}
		}
		return lSettings;
	}
}
