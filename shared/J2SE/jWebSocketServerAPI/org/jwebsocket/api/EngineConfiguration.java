//  ---------------------------------------------------------------------------
//  jWebSocket - Copyright (c) 2010 jwebsocket.org
//  ---------------------------------------------------------------------------
//  This program is free software; you can redistribute it and/or modify it
//  under the terms of the GNU Lesser General Public License as published by the
//  Free Software Foundation; either version 3 of the License, or (at your
//  option) any later version.
//  This program is distributed in the hope that it will be useful, but WITHOUT
//  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//  more details.
//  You should have received a copy of the GNU Lesser General Public License along
//  with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
//  ---------------------------------------------------------------------------
package org.jwebsocket.api;

import java.util.List;

/**
 * Base interface that provides the read-only access to all the engine configuration 
 * values configured via <tt>jWebSocket.xml</tt> file for a given engine 
 * @author puran 
 * @version $Id: EngineConfiguration.java 615 2010-07-01 07:49:54Z mailtopuran@gmail.com $
 */
public interface EngineConfiguration extends Configuration {
    /**
     * Returns the fully qualified name of the external jar file from which 
     * the engine is loaded. In case, no external library or jar file is used 
     * then this value will return null or empty string.
     * @return the jar file name or null value
     */
    String getJar();
    /**
     * Returns the port at which the engine is running
     * @return the port number by default it's 8787 for jWebSocket
     */
    int getPort();
    /**
     * Engine timeout value in milliseconds
     * @return timeout value
     */
    int getTimeout();
    /**
     * The maximum frame size in KB, any data frame with 
     * size greater than this value will cause connection to be terminated
     * @return the maximum frame size
     */
    int getMaxFramesize();
    /**
     * These are the list of allowed domains for accepting connections for the origin
     * based security model. Any connection request with different origin than the origins
     * in this list is not accepted and the connection is terminated immediately.
     * 
     * @return the list of allowed domains
     */
    List<String> getDomains();
    
}
