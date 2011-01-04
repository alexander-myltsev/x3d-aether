//	---------------------------------------------------------------------------
//	jWebSocket - Filter API
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
package org.jwebsocket.api;

import org.jwebsocket.kit.FilterResponse;

/**
 *
 * @author aschulze
 */
public interface WebSocketFilter {

	/**
	 *
	 * @param aResponse
	 * @param aConnector
	 * @param aPacket
	 */
	void processPacketIn(FilterResponse aResponse, WebSocketConnector aConnector, WebSocketPacket aPacket);

	/**
	 *
	 * @param aResponse
	 * @param aSource
	 * @param aTarget
	 * @param aPacket
	 */
	void processPacketOut(FilterResponse aResponse, WebSocketConnector aSource, WebSocketConnector aTarget, WebSocketPacket aPacket);

	/**
	 *
	 * @param aFilterChain
	 */
	public void setFilterChain(WebSocketFilterChain aFilterChain);

	/**
	 * @return the filterChain
	 */
	public WebSocketFilterChain getFilterChain();
}
