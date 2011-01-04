//	---------------------------------------------------------------------------
//	jWebSocket - jWebSocket Administration Plug-In
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
package org.jwebsocket.plugins.admin;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.apache.log4j.Logger;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.config.JWebSocketServerConstants;
import org.jwebsocket.instance.JWebSocketInstance;
import org.jwebsocket.kit.PlugInResponse;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.plugins.TokenPlugIn;
import org.jwebsocket.security.SecurityFactory;
import org.jwebsocket.server.TokenServer;
import org.jwebsocket.token.Token;

/**
 *
 * @author aschulze
 */
public class AdminPlugIn extends TokenPlugIn {

	private static Logger log = Logging.getLogger(AdminPlugIn.class);
	// if namespace changed update client plug-in accordingly!
	private String NS_ADMIN = JWebSocketServerConstants.NS_BASE + ".plugins.admin";

	/**
	 *
	 */
	public AdminPlugIn() {
		if (log.isDebugEnabled()) {
			log.debug("Instantiating admin plug-in...");
		}
		// specify default name space for admin plugin
		this.setNamespace(NS_ADMIN);
	}

	@Override
	public void processToken(PlugInResponse aResponse, WebSocketConnector aConnector, Token aToken) {
		String lType = aToken.getType();
		String lNS = aToken.getNS();

		if (lType != null && (lNS == null || lNS.equals(getNamespace()))) {
			// remote shut down server
			if (lType.equals("shutdown")) {
				shutdown(aConnector, aToken);
			} else if (lType.equals("getConnections")) {
				getConnections(aConnector, aToken);
			}
		}
	}

	/**
	 * shutdown server
	 * @param aConnector
	 * @param aToken
	 */
	public void shutdown(WebSocketConnector aConnector, Token aToken) {
		TokenServer lServer = getServer();

		if (log.isDebugEnabled()) {
			log.debug("Processing 'shutdown'...");
		}

		// TODO: check for allowShutdown setting, irrespective of user right!

		// check if user is allowed to run 'shutdown' command
		if (!SecurityFactory.checkRight(lServer.getUsername(aConnector), NS_ADMIN + ".shutdown")) {
			lServer.sendToken(aConnector, lServer.createAccessDenied(aToken));
			return;
		}

		// notify clients about shutdown
		Token lResponseToken = lServer.createResponse(aToken);
		lResponseToken.put("msg", "Shutdown in progress...");
		lServer.broadcastToken(lResponseToken);

		JWebSocketInstance.setStatus(JWebSocketInstance.SHUTTING_DOWN);

	}

	/**
	 * return all session
	 * @param aConnector
	 * @param aToken
	 */
	public void getConnections(WebSocketConnector aConnector, Token aToken) {
		TokenServer lServer = getServer();

		if (log.isDebugEnabled()) {
			log.debug("Processing 'getConnections'...");
		}

		// check if user is allowed to run 'getConnections' command
		if (!SecurityFactory.checkRight(lServer.getUsername(aConnector), NS_ADMIN + ".getConnections")) {
			lServer.sendToken(aConnector, lServer.createAccessDenied(aToken));
			return;
		}

		Token lResponse = lServer.createResponse(aToken);
		try {
			List<Map> lResultList = new FastList<Map>();
			Map lConnectorMap = lServer.getAllConnectors();
			Collection<WebSocketConnector> lConnectors = lConnectorMap.values();
			for (WebSocketConnector lConnector : lConnectors) {
				Map lResultItem = new FastMap<String,Object>();
				lResultItem.put("port", lConnector.getRemotePort());
				lResultItem.put("usid", lConnector.getSession().getSessionId());
				lResultItem.put("username", lConnector.getUsername());
				lResultItem.put("isToken", lConnector.getBoolean(TokenServer.VAR_IS_TOKENSERVER));
				lResultList.add(lResultItem);
			}
			lResponse.put("connections", lResultList);
		} catch (Exception ex) {
			log.error(ex.getClass().getSimpleName()
					+ " on getConnections: " + ex.getMessage());
		}

		lServer.sendToken(aConnector, lResponse);
	}
}
