//	---------------------------------------------------------------------------
//	jWebSocket - BaseFilter Implementation
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
package org.jwebsocket.filter;

import org.jwebsocket.kit.FilterResponse;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.api.WebSocketFilter;
import org.jwebsocket.api.WebSocketFilterChain;
import org.jwebsocket.api.WebSocketPacket;

/**
 *
 * @author aschulze
 */
public class BaseFilter implements WebSocketFilter {

	private String id = null;
	// every filter has a backward reference to its filter chain
	private WebSocketFilterChain filterChain = null;

	public BaseFilter(String aId) {
		id = aId;
	}

	@Override
	public String toString() {
		return id;
	}

	@Override
	public void processPacketIn(FilterResponse aResponse, WebSocketConnector aConnector, WebSocketPacket aPacket) {
	}

	@Override
	public void processPacketOut(FilterResponse aResponse, WebSocketConnector aSource, WebSocketConnector aTarget, WebSocketPacket aPacket) {
	}

	/**
	 *
	 * @param aFilterChain
	 */
	@Override
	public void setFilterChain(WebSocketFilterChain aFilterChain) {
		filterChain = aFilterChain;
	}

	/**
	 * @return the filterChain
	 */
	@Override
	public WebSocketFilterChain getFilterChain() {
		return filterChain;
	}

}
