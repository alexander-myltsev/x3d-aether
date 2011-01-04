//---------------------------------------------------------------------------
//jWebSocket - jWebSocket Sample Plug-In
//Copyright (c) 2010 jWebSocket.org, Alexander Schulze, Innotrade GmbH
//---------------------------------------------------------------------------
//This program is free software; you can redistribute it and/or modify it
//under the terms of the GNU Lesser General Public License as published by the
//Free Software Foundation; either version 3 of the License, or (at your
//option) any later version.
//This program is distributed in the hope that it will be useful, but WITHOUT
//ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//more details.
//You should have received a copy of the GNU Lesser General Public License along
//with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
//---------------------------------------------------------------------------

package org.jwebsocket.plugins.sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javolution.util.FastMap;

import org.jwebsocket.api.EngineConfiguration;
import org.jwebsocket.api.ServerConfiguration;
import org.jwebsocket.api.WebSocketFilter;
import org.jwebsocket.api.WebSocketPlugIn;
import org.jwebsocket.api.WebSocketServer;
import org.jwebsocket.factory.AbstractJWebSocketInitializer;

/**
 * Example of custom JWebSocket intializer to initalize and register the custom
 * plugins, servers and filters to jWebSocket infrastructure. This is for development mode
 * so that developers can debug their plugins, filters etc.. at compile time.
 * @author puran
 * @version $Id: CustomInitializer.java 665 2010-07-13 05:48:54Z mailtopuran@gmail.com $
 * 
 */
public class CustomInitializer extends AbstractJWebSocketInitializer {

    @Override
    public FastMap<String, List<WebSocketPlugIn>> initializeCustomPlugins() {
        FastMap<String, List<WebSocketPlugIn>> pluginMap = new FastMap<String, List<WebSocketPlugIn>>();
        List<WebSocketPlugIn> plugins = new ArrayList<WebSocketPlugIn>();
        plugins.add(new SamplePlugIn());
        pluginMap.put("ts0", plugins);
        return pluginMap;
    }

    @Override
    public List<WebSocketServer> initializeCustomServers() {
        return Collections.emptyList();
    }

    @Override
    public EngineConfiguration getEngineConfiguration() {
        return null;
    }

    @Override
    public ServerConfiguration getServerConfiguration() {
        return null;
    }

    @Override
    public FastMap<String, List<WebSocketFilter>> initializeCustomFilters() {
        return null;
    }

}
