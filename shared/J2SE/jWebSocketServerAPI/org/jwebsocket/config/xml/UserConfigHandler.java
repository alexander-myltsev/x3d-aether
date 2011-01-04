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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javolution.util.FastList;
import org.jwebsocket.config.Config;
import org.jwebsocket.config.ConfigHandler;
/**
 * The config handler for user config
 * @author puran
 * @version $Id: UserConfigHandler.java 596 2010-06-22 17:09:54Z fivefeetfurther $
 */
public class UserConfigHandler implements ConfigHandler {
	
	private final static String LOGIN_NAME = "loginname";
	private final static String FIRST_NAME = "firstname";
	private final static String LAST_NAME = "lastname";
	private final static String PASSWORD = "password";
	private final static String DESCRIPTION= "description";
	private final static String STATUS = "status";
	private final static String ELEMENT_USER = "user";
	private final static String ELEMENT_ROLES = "roles";
	private final static String ELEMENT_ROLE = "role";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Config processConfig(XMLStreamReader streamReader) throws XMLStreamException {
		String loginname = "", firstname = "", lastname = "", password = "", description = "";
		int status = 0;
		List<String> roles = null;
		while (streamReader.hasNext()) {
			streamReader.next();
			if (streamReader.isStartElement()) {
				String elementName = streamReader.getLocalName();
				if (elementName.equals(LOGIN_NAME)) {
					streamReader.next();
					loginname = streamReader.getText();
				} else if (elementName.equals(FIRST_NAME)) {
					streamReader.next();
					firstname = streamReader.getText();
				} else if (elementName.equals(LAST_NAME)) {
					streamReader.next();
					lastname = streamReader.getText();
				} else if (elementName.equals(PASSWORD)) {
					streamReader.next();
					//TODO: temporary fix to handle error because of blank password value
					//better figure out something cleaner.
					if (streamReader.getEventType() != 2) {
						password = streamReader.getText();
					}
				} else if (elementName.equals(DESCRIPTION)) {
					streamReader.next();
					description = streamReader.getText();
				} else if (elementName.equals(STATUS)) {
					streamReader.next();
					status  = Integer.parseInt(streamReader.getText());
				} else if (elementName.equals(ELEMENT_ROLES)) {
					streamReader.next();
					roles = getRoles(streamReader);
				} else {
					//ignore
				}
			}
			if (streamReader.isEndElement()) {
				String elementName = streamReader.getLocalName();
				if (elementName.equals(ELEMENT_USER)) {
					break;
				}
			}
		}
		return new UserConfig(loginname, firstname, lastname, password, description, status, roles);
	}

	/**
	 * private method that reads the roles 
	 * @param streamReader the stream reader object
	 * @return the list of user roles
	 */
	private List<String> getRoles(XMLStreamReader streamReader) throws XMLStreamException {
		List<String> roles = new FastList<String>();
		while (streamReader.hasNext()) {
			streamReader.next();
			if (streamReader.isStartElement()) {
				String elementName = streamReader.getLocalName();
				if (elementName.equals(ELEMENT_ROLE)) {
					streamReader.next();
					String role = streamReader.getText();
					roles.add(role);
				}
			}
			if (streamReader.isEndElement()) {
				String elementName = streamReader.getLocalName();
				if (elementName.equals(ELEMENT_ROLES)) {
					break;
				}
			}
		}
		return roles;
	}

}
