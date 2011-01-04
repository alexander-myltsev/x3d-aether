//	---------------------------------------------------------------------------
//	jWebSocket - Base Connector Implementation
//	Copyright (c) 2010 Alexander Schulze, Innotrade GmbH
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
package org.jwebsocket.connectors;

import java.net.InetAddress;
import java.util.Map;
import javolution.util.FastMap;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.api.WebSocketEngine;
import org.jwebsocket.kit.CloseReason;
import org.jwebsocket.kit.RequestHeader;
import org.jwebsocket.kit.WebSocketSession;

/**
 * Provides the basic implementation of the jWebSocket connectors.
 * The {@code BaseConnector} is supposed to be used as ancestor for the
 * connector implementations like e.g. the {@code TCPConnector} or the
 * {@code NettyConnector }.
 * @author aschulze
 */
public class BaseConnector implements WebSocketConnector {

	/**
	 * Default name for shared custom variable <tt>username</tt>.
	 */
	public final static String VAR_USERNAME = "$username";
	/**
	 * Default name for shared custom variable <tt>nodeid</tt>.
	 */
	public final static String VAR_NODEID = "$nodeid";
	private WebSocketEngine engine = null;
	private RequestHeader header = null;
	private final WebSocketSession session = new WebSocketSession();
	private final Map<String, Object> customVars = new FastMap<String, Object>();

	/**
	 * 
	 * @param aEngine
	 */
	public BaseConnector(WebSocketEngine aEngine) {
		engine = aEngine;
	}

	@Override
	public void startConnector() {
		if (engine != null) {
			engine.connectorStarted(this);
		}
	}

	@Override
	public void stopConnector(CloseReason aCloseReason) {
		if (engine != null) {
			engine.connectorStopped(this, aCloseReason);
		}
	}

	@Override
	public void processPacket(WebSocketPacket aDataPacket) {
		if (engine != null) {
			engine.processPacket(this, aDataPacket);
		}
	}

	@Override
	public void sendPacket(WebSocketPacket aDataPacket) {
	}

	@Override
	public WebSocketEngine getEngine() {
		return engine;
	}

	@Override
	public RequestHeader getHeader() {
		return header;
	}

	/**
	 * @param header the header to set
	 */
	@Override
	public void setHeader(RequestHeader header) {
		// TODO: the sub protocol should be a connector variable! not part of the header!
		this.header = header;

		// TODO: this can be improved, maybe distinguish between header and URL args!
		Map lArgs = header.getArgs();
		String lNodeId = (String) lArgs.get("unid");
		if (lNodeId != null) {
			setNodeId(lNodeId);
			lArgs.remove("unid");
		}
	}

	@Override
	public Object getVar(String aKey) {
		return customVars.get(aKey);
	}

	@Override
	public void setVar(String aKey, Object aValue) {
		customVars.put(aKey, aValue);
	}

	@Override
	public Boolean getBoolean(String aKey) {
		return (Boolean) getVar(aKey);
	}

	@Override
	public boolean getBool(String aKey) {
		Boolean lBool = getBoolean(aKey);
		return (lBool != null && lBool);
	}

	@Override
	public void setBoolean(String aKey, Boolean aValue) {
		setVar(aKey, aValue);
	}

	@Override
	public String getString(String aKey) {
		return (String) getVar(aKey);
	}

	@Override
	public void setString(String aKey, String aValue) {
		setVar(aKey, aValue);
	}

	@Override
	public Integer getInteger(String aKey) {
		return (Integer) getVar(aKey);
	}

	@Override
	public void setInteger(String aKey, Integer aValue) {
		setVar(aKey, aValue);
	}

	@Override
	public void removeVar(String aKey) {
		customVars.remove(aKey);
	}

	@Override
	public String generateUID() {
		return null;
	}

	@Override
	public int getRemotePort() {
		return -1;
	}

	@Override
	public InetAddress getRemoteHost() {
		return null;
	}

	@Override
	public String getId() {
		return String.valueOf(getRemotePort());
	}

	/*
	 * Returns the session for the websocket connection.
	 * @return
	 */
	@Override
	public WebSocketSession getSession() {
		return session;
	}

	// some convenience methods to easier process username (login-status)
	// and configured unique node id for clusters (independent from tcp port)
	/**
	 *
	 * @return
	 */
	@Override
	public String getUsername() {
		return getString(BaseConnector.VAR_USERNAME);
	}

	/**
	 *
	 * @param aUsername
	 */
	@Override
	public void setUsername(String aUsername) {
		setString(BaseConnector.VAR_USERNAME, aUsername);
	}

	/**
	 *
	 */
	@Override
	public void removeUsername() {
		removeVar(BaseConnector.VAR_USERNAME);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getNodeId() {
		return getString(BaseConnector.VAR_NODEID);
	}

	/**
	 *
	 * @param aNodeId
	 */
	@Override
	public void setNodeId(String aNodeId) {
		setString(BaseConnector.VAR_NODEID, aNodeId);
	}

	/**
	 *
	 */
	@Override
	public void removeNodeId() {
		removeVar(BaseConnector.VAR_NODEID);
	}
}
