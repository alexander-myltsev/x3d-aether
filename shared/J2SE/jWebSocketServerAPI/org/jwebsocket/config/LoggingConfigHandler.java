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


import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.List;

/**
 * Handler for the logging configuration
 *
 * @author puran
 * @version $Id: LoggingConfigHandler.java 616 2010-07-01 08:04:51Z fivefeetfurther $
 */
public class LoggingConfigHandler implements ConfigHandler {

	private static final String APPENDER = "appender";
	private static final String PATTERN = "pattern";
	private static final String LEVEL = "level";
	private static final String FILENAME = "filename";
	private static final String BUFFERSIZE = "buffersize";
	private static final String ELEMENT_LOG4J = "log4j";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Config processConfig(XMLStreamReader streamReader)
			throws XMLStreamException {
		String appender = "", pattern = "", level = "", filename = "";
		Boolean isBuffered = true;
		Integer bufferSize = 2048;
		List<String> loggings = null;
		while (streamReader.hasNext()) {
			streamReader.next();
			if (streamReader.isStartElement()) {
				String elementName = streamReader.getLocalName();
				if (elementName.equals(APPENDER)) {
					streamReader.next();
					appender = streamReader.getText();
				} else if (elementName.equals(PATTERN)) {
					streamReader.next();
					pattern = streamReader.getText();
				} else if (elementName.equals(LEVEL)) {
					streamReader.next();
					level = streamReader.getText();
				} else if (elementName.equals(FILENAME)) {
					streamReader.next();
					filename = streamReader.getText();
				} else if (elementName.equals(BUFFERSIZE)) {
					streamReader.next();
					bufferSize = Integer.parseInt(streamReader.getText());
				} else {
					//ignore
				}
			}
			if (streamReader.isEndElement()) {
				String elementName = streamReader.getLocalName();
				if (elementName.equals(ELEMENT_LOG4J)) {
					break;
				}
			}
		}
		return new LoggingConfig(appender, pattern, level, filename,
				bufferSize);
	}
}
