//	---------------------------------------------------------------------------
//	jWebSocket - System Filter for security
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
package org.jwebsocket.filters.system;

import org.apache.log4j.Logger;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.filter.TokenFilter;
import org.jwebsocket.kit.FilterResponse;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.server.TokenServer;
import org.jwebsocket.token.Token;

/**
 *
 * @author aschulze
 */
public class SystemFilter extends TokenFilter {

	private static Logger log = Logging.getLogger(SystemFilter.class);

	/**
	 *
	 * @param aId
	 */
	public SystemFilter(String aId) {
		super(aId);
		if (log.isDebugEnabled()) {
			log.debug("Instantiating system filter...");
		}
	}

	/**
	 *
	 * @param aResponse
	 * @param aConnector
	 * @param aToken
	 */
	@Override
	public void processTokenIn(FilterResponse aResponse, WebSocketConnector aConnector, Token aToken) {
		if (log.isDebugEnabled()) {
			log.debug("Checking incoming token from " + (aConnector != null ? aConnector.getId() : "[not given]")
					+ ": " + aToken.toString() + "...");
		}

		TokenServer lServer = getServer();
		String lUsername = lServer.getUsername(aConnector);

		// TODO: very first security test, replace by user's locked state!
		if( "locked".equals(lUsername) ) {
			Token lToken = lServer.createAccessDenied(aToken);
			lServer.sendToken(aConnector, lToken);
			aResponse.rejectMessage();
			return;
		}
	}

	/**
	 *
	 * @param aResponse
	 * @param aSource
	 * @param aTarget
	 * @param aToken
	 */
	@Override
	public void processTokenOut(FilterResponse aResponse, WebSocketConnector aSource,
			WebSocketConnector aTarget, Token aToken) {
		if (log.isDebugEnabled()) {
			log.debug("Checking outgoing token from "
					+ (aSource != null ? aSource.getId() : "[not given]")
					+ " to "
					+ (aTarget != null ? aTarget.getId() : "[not given]")
					+ ": " + aToken.toString() + "...");
		}
	}
}
