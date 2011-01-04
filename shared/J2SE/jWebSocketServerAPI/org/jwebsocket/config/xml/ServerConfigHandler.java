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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.jwebsocket.config.Config;
import org.jwebsocket.config.ConfigHandler;
/**
 * Handler class that reads the server configuration
 * @author puran
 * @version $Id: ServerConfigHandler.java 596 2010-06-22 17:09:54Z fivefeetfurther $
 *
 */
public class ServerConfigHandler implements ConfigHandler {

	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String JAR = "jar";
	private static final String ELEMENT_SERVER = "server";
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Config processConfig(XMLStreamReader streamReader) throws XMLStreamException {
		String id = "", name = "", jar = "";
		while (streamReader.hasNext()) {
			streamReader.next();
			if (streamReader.isStartElement()) {
				String elementName = streamReader.getLocalName();
				if (elementName.equals(ID)) {
					streamReader.next();
					id = streamReader.getText();
				} else if (elementName.equals(NAME)) {
					streamReader.next();
					name = streamReader.getText();
				} else if (elementName.equals(JAR)) {
					streamReader.next();
					jar = streamReader.getText();
				} else {
					//ignore
				}
			}
			if (streamReader.isEndElement()) {
				String elementName = streamReader.getLocalName();
				if (elementName.equals(ELEMENT_SERVER)) {
					break;
				}
			}
		}
		return new ServerConfig(id, name, jar);
	}

}
