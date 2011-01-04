//	---------------------------------------------------------------------------
//	jWebSocket - TokenFilterChain Implementation
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

import org.apache.log4j.Logger;
import org.jwebsocket.kit.FilterResponse;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.api.WebSocketFilter;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.api.WebSocketServer;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.server.TokenServer;
import org.jwebsocket.token.Token;

/**
 *
 * @author aschulze
 */
public class TokenFilterChain extends BaseFilterChain {

	private static Logger log = Logging.getLogger(TokenFilterChain.class);

	/**
	 *
	 * @param aServer
	 */
	public TokenFilterChain(WebSocketServer aServer) {
		super(aServer);
	}

	/**
	 * @return the server
	 */
	@Override
	public TokenServer getServer() {
		return (TokenServer)super.getServer();
	}

	@Override
	public void addFilter(WebSocketFilter aFilter) {
		if (aFilter != null) {
			if (log.isDebugEnabled()) {
				log.debug("Adding token filter " + aFilter + "...");
			}
			super.addFilter(aFilter);
		}
	}

	@Override
	public void removeFilter(WebSocketFilter aFilter) {
		if (aFilter != null) {
			if (log.isDebugEnabled()) {
				log.debug("Removing token filter " + aFilter + "...");
			}
			super.removeFilter(aFilter);
		}
	}

	@Override
	public FilterResponse processPacketIn(WebSocketConnector aConnector, WebSocketPacket aDataPacket) {
		// FilterResponse lFilterResponse = new FilterResponse();
		// return lFilterResponse;
		return null;
	}

	@Override
	public FilterResponse processPacketOut(WebSocketConnector aSource, WebSocketConnector aTarget, WebSocketPacket aDataPacket) {
		// FilterResponse lFilterResponse = new FilterResponse();
		// return lFilterResponse;
		return null;
	}

	/**
	 *
	 * @param aConnector
	 * @param aToken
	 * @return
	 */
	public FilterResponse processTokenIn(WebSocketConnector aConnector, Token aToken) {
		FilterResponse lFilterResponse = new FilterResponse();
		for (WebSocketFilter filter : getFilters()) {
			try {
				((TokenFilter) filter).processTokenIn(lFilterResponse, aConnector, aToken);
			} catch (Exception ex) {
				log.error(ex.getClass().getSimpleName() + ": " + ex.getMessage());
			}
			if (lFilterResponse.isRejected()) {
				break;
			}
		}
		return lFilterResponse;
	}

	public FilterResponse processTokenOut(WebSocketConnector aSource, WebSocketConnector aTarget, Token aToken) {
		FilterResponse lFilterResponse = new FilterResponse();
		for (WebSocketFilter filter : getFilters()) {
			try {
				((TokenFilter) filter).processTokenOut(lFilterResponse, aSource, aTarget, aToken);
			} catch (Exception ex) {
				log.error(ex.getClass().getSimpleName() + ": " + ex.getMessage());
			}
			if (lFilterResponse.isRejected()) {
				break;
			}
		}
		return lFilterResponse;
	}
}
