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
package org.jwebsocket.factory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.jwebsocket.api.EngineConfiguration;
import org.jwebsocket.api.ServerConfiguration;
import org.jwebsocket.api.WebSocketFilter;
import org.jwebsocket.api.WebSocketPlugIn;
import org.jwebsocket.api.WebSocketServer;
import org.jwebsocket.config.JWebSocketCommonConstants;

/**
 * Class that performs the default servers and plugins initialization
 * 
 * @author puran
 * @version $Id: JWebSocketInitializer.java 399 2010-04-30 01:00:57Z mailtopuran$
 */
public class JWebSocketInitializer extends AbstractJWebSocketInitializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, List<WebSocketPlugIn>> initializeCustomPlugins() {
        return new FastMap<String, List<WebSocketPlugIn>>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WebSocketServer> initializeCustomServers() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, List<WebSocketFilter>> initializeCustomFilters() {
        return new FastMap<String, List<WebSocketFilter>>();
    }

    @Override
    public EngineConfiguration getEngineConfiguration() {
        return new DefaultEngineConfiguration();
    }

    @Override
    public ServerConfiguration getServerConfiguration() {
        return new DefaultServerConfiguration();
    }

    private class DefaultEngineConfiguration implements EngineConfiguration {

        @Override
        public List<String> getDomains() {
            List<String> domains =  new FastList<String>();
            domains.add("localhost");
            return domains;
        }

        @Override
        public String getJar() {
            return null;
        }

        @Override
        public int getMaxFramesize() {
            return JWebSocketCommonConstants.DEFAULT_MAX_FRAME_SIZE;
        }

        @Override
        public int getPort() {
            return JWebSocketCommonConstants.DEFAULT_PORT;
        }

        @Override
        public int getTimeout() {
            return JWebSocketCommonConstants.DEFAULT_TIMEOUT;
        }

        @Override
        public String getId() {
            return "netty0";
        }

        @Override
        public String getName() {
            return "Netty";
        }
        
    }

    private class DefaultServerConfiguration implements ServerConfiguration {

        @Override
        public String getJar() {
            return null;
        }

        @Override
        public String getId() {
            return "ts0";
        }

        @Override
        public String getName() {
            return "Server";
        }

    }

}
