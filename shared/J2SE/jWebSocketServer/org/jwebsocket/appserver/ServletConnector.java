//	---------------------------------------------------------------------------
//	jWebSocket - Servlet Connector Implementation
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
package org.jwebsocket.appserver;

import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jwebsocket.api.WebSocketEngine;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.connectors.BaseConnector;
import org.jwebsocket.kit.CloseReason;
import org.jwebsocket.kit.RequestHeader;

/**
 *
 * @author aschulze
 */
public class ServletConnector extends BaseConnector {

	HttpServletRequest request = null;
	HttpServletResponse response = null;
	String plainResponse = "";

	/**
	 *
	 * @param aRequest
	 * @param aResponse
	 */
	public ServletConnector(HttpServletRequest aRequest, HttpServletResponse aResponse) {
		// no "engine" available here
		super(null);
		request = aRequest;
		response = aResponse;
		// TODO: Overhaul this hardcoded reference! See TokenServer class!
		setBoolean("org.jWebSocket.tokenserver.isTS", true);
		RequestHeader lHeader = new RequestHeader();
		lHeader.put("prot", "json");
		setHeader(lHeader);
	}

	@Override
	public void startConnector() {
	}

	@Override
	public void stopConnector(CloseReason aCloseReason) {
	}

	@Override
	public void processPacket(WebSocketPacket aDataPacket) {
	}

	@Override
	public void sendPacket(WebSocketPacket aDataPacket) {
		plainResponse += aDataPacket.getUTF8() + "\n";
	}

	@Override
	public WebSocketEngine getEngine() {
		return null;
	}

	@Override
	public String generateUID() {
		return request.getSession().getId();
	}

	@Override
	public int getRemotePort() {
		// TODO: Need to return remote(!) port here
		return request.getServerPort();
	}

	@Override
	public InetAddress getRemoteHost() {
		try {
			return InetAddress.getByName(request.getRemoteAddr());
		} catch (UnknownHostException ex) {
			return null;
		}
	}

	@Override
	public String getId() {
		return String.valueOf(getRemotePort());
	}

	public String getPlainResponse() {
		return "response:\n" + plainResponse;
	}
}
