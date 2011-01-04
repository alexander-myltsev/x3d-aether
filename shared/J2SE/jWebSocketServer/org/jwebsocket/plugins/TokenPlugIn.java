//	---------------------------------------------------------------------------
//	jWebSocket - Wrapper for Token based PlugIns (Convenience Class)
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
package org.jwebsocket.plugins;

import org.jwebsocket.kit.PlugInResponse;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.api.WebSocketEngine;
import org.jwebsocket.kit.BroadcastOptions;
import org.jwebsocket.kit.CloseReason;
import org.jwebsocket.server.TokenServer;
import org.jwebsocket.token.Token;

/**
 *
 * @author aschulze
 */
public class TokenPlugIn extends BasePlugIn {

	private String namespace = null;

	@Override
	public void engineStarted(WebSocketEngine aEngine) {
	}

	@Override
	public void engineStopped(WebSocketEngine aEngine) {
	}

	/**
	 *
	 * @param aConnector
	 */
	@Override
	public void connectorStarted(WebSocketConnector aConnector) {
	}

	/**
	 *
	 * @param aResponse
	 * @param aConnector
	 * @param aToken
	 */
	public void processToken(PlugInResponse aResponse, WebSocketConnector aConnector, Token aToken) {
	}

	@Override
	public void processPacket(PlugInResponse aResponse, WebSocketConnector aConnector, WebSocketPacket aDataPacket) {
		//
	}

	/**
	 *
	 * @param aConnector
	 */
	@Override
	public void connectorStopped(WebSocketConnector aConnector, CloseReason aCloseReason) {
	}

	/**
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * @param namespace the namespace to set
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public TokenServer getServer() {
		// TODO: obtaining server for a plug-in probably can be improved
		return (TokenServer)super.getServer();
		/*
		TokenServer lServer =
		TokenPlugInChain plugInChain = (TokenPlugInChain) getPlugInChain();
		if (plugInChain != null) {
			lServer = (TokenServer) plugInChain.getServer();
		}
		return lServer;
		 */
	}

	/**
	 * Convenience method, just a wrapper for token server method
	 * <tt>createResponse</tt> to simplify token plug-in code.
	 * @param aInToken
	 * @return
	 */
	public Token createResponse(Token aInToken) {
		TokenServer lServer = getServer();
		if (lServer != null) {
			return lServer.createResponse(aInToken);
		} else {
			return null;
		}
	}

	/**
	 * Convenience method, just a wrapper for token server method
	 * <tt>createAccessDenied</tt> to simplify token plug-in code.
	 * @param aInToken
	 * @return
	 */
	public Token createAccessDenied(Token aInToken) {
		TokenServer lServer = getServer();
		if (lServer != null) {
			return lServer.createAccessDenied(aInToken);
		} else {
			return null;
		}
	}

	/**
	 * Convenience method, just a wrapper for token server method
	 * <tt>sendToken</tt> to simplify token plug-in code.
	 * @param aSource
	 * @param aTarget
	 * @param aToken
	 */
	public void sendToken(WebSocketConnector aSource, WebSocketConnector aTarget, Token aToken) {
		TokenServer lServer = getServer();
		if (lServer != null) {
			lServer.sendToken(aSource, aTarget, aToken);
		}
	}

	/**
	 * Convenience method, just a wrapper for token server method
	 * <tt>sendToken</tt> to simplify token plug-in code.
	 * @param aSource
	 * @param aTarget
	 * @param aToken
	 */
	/*
	public void sendToken(WebSocketConnector aTarget, Token aToken) {
	TokenServer lServer = getServer();
	if (lServer != null) {
	lServer.sendToken(aTarget, aToken);
	}
	}
	 */
	/**
	 * Convenience method, just a wrapper for token server method
	 * <tt>sendToken</tt> to simplify token plug-in code.
	 * @param aSource
	 * @param aTarget
	 * @param aToken
	 */
	public void broadcastToken(WebSocketConnector aSource, Token aToken) {
		TokenServer lServer = getServer();
		if (lServer != null) {
			lServer.broadcastToken(aSource, aToken);
		}
	}

	public void broadcastToken(WebSocketConnector aSource, Token aToken,
			BroadcastOptions aBroadcastOptions) {
		TokenServer lServer = getServer();
		if (lServer != null) {
			lServer.broadcastToken(aSource, aToken, aBroadcastOptions);
		}
	}

}
