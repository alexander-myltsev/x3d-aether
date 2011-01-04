//	---------------------------------------------------------------------------
//	jWebSocket - The jWebSocket System Plug-In
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
package org.jwebsocket.plugins.system;

import java.util.List;
import java.util.Random;
import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.log4j.Logger;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.config.JWebSocketCommonConstants;
import org.jwebsocket.config.JWebSocketServerConstants;
import org.jwebsocket.connectors.BaseConnector;
import org.jwebsocket.kit.BroadcastOptions;
import org.jwebsocket.kit.CloseReason;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.kit.PlugInResponse;
import org.jwebsocket.plugins.TokenPlugIn;
import org.jwebsocket.security.SecurityFactory;
import org.jwebsocket.token.Token;
import org.jwebsocket.util.Tools;

/**
 * implements the jWebSocket system tokens like login, logout, send,
 * broadcast etc...
 * @author aschulze
 */
public class SystemPlugIn extends TokenPlugIn {

	private static Logger log = Logging.getLogger(SystemPlugIn.class);
	// specify name space for system plug-in
	private static final String NS_SYSTEM_DEFAULT = JWebSocketServerConstants.NS_BASE + ".plugins.system";
	// specify token types processed by system plug-in
	private static final String TT_SEND = "send";
	private static final String TT_BROADCAST = "broadcast";
	private static final String TT_WELCOME = "welcome";
	private static final String TT_GOODBYE = "goodBye";
	private static final String TT_LOGIN = "login";
	private static final String TT_LOGOUT = "logout";
	private static final String TT_CLOSE = "close";
	private static final String TT_GETCLIENTS = "getClients";
	private static final String TT_PING = "ping";
	private static final String TT_ALLOC_CHANNEL = "alloc";
	private static final String TT_DEALLOC_CHANNEL = "dealloc";
	// specify shared connector variables
	private static final String VAR_GROUP = NS_SYSTEM_DEFAULT + ".group";

	/**
	 *
	 */
	public SystemPlugIn() {
		if (log.isDebugEnabled()) {
			log.debug("Instantiating system plug-in...");
		}
		// specify default name space for system plugin
		this.setNamespace(NS_SYSTEM_DEFAULT);
	}

	@Override
	public void processToken(PlugInResponse aResponse, WebSocketConnector aConnector, Token aToken) {
		String lType = aToken.getType();
		String lNS = aToken.getNS();

		if (lType != null && (lNS == null || lNS.equals(getNamespace()))) {
			if (lType.equals(TT_SEND)) {
				send(aConnector, aToken);
				aResponse.abortChain();
			} else if (lType.equals(TT_BROADCAST)) {
				broadcast(aConnector, aToken);
				aResponse.abortChain();
			} else if (lType.equals(TT_LOGIN)) {
				login(aConnector, aToken);
				aResponse.abortChain();
			} else if (lType.equals(TT_LOGOUT)) {
				logout(aConnector, aToken);
				aResponse.abortChain();
			} else if (lType.equals(TT_CLOSE)) {
				close(aConnector, aToken);
				aResponse.abortChain();
			} else if (lType.equals(TT_GETCLIENTS)) {
				getClients(aConnector, aToken);
				aResponse.abortChain();
			} else if (lType.equals(TT_PING)) {
				ping(aConnector, aToken);
			} else if (lType.equals(TT_ALLOC_CHANNEL)) {
				allocChannel(aConnector, aToken);
			} else if (lType.equals(TT_DEALLOC_CHANNEL)) {
				deallocChannel(aConnector, aToken);
			}
		}
	}

	@Override
	public void connectorStarted(WebSocketConnector aConnector) {
		// set session id first, so that it can be processed in the connectorStarted method
		Random rand = new Random(System.nanoTime());

		// TODO: if unique node id is passed check if already assigned in the network and reject connect if so!

		aConnector.getSession().setSessionId(Tools.getMD5(aConnector.generateUID() + "." + rand.nextInt()));
		// call super connectorStarted
		super.connectorStarted(aConnector);
		// and send the welcome message incl. the session id
		sendWelcome(aConnector);
		// if new connector is active broadcast this event to then network
		broadcastConnectEvent(aConnector);
	}

	@Override
	public void connectorStopped(WebSocketConnector aConnector, CloseReason aCloseReason) {
		super.connectorStopped(aConnector, aCloseReason);
		// notify other clients that client disconnected
		broadcastDisconnectEvent(aConnector);
	}

	private String getGroup(WebSocketConnector aConnector) {
		return aConnector.getString(VAR_GROUP);
	}

	private void setGroup(WebSocketConnector aConnector, String aGroup) {
		aConnector.setString(VAR_GROUP, aGroup);
	}

	private void removeGroup(WebSocketConnector aConnector) {
		aConnector.removeVar(VAR_GROUP);
	}

	/**
	 *
	 *
	 * @param aConnector
	 */
	public void broadcastConnectEvent(WebSocketConnector aConnector) {
		if (log.isDebugEnabled()) {
			log.debug("Broadcasting connect...");
		}
		// broadcast connect event to other clients of the jWebSocket network
		Token lConnect = new Token(Token.TT_EVENT);
		lConnect.put("name", "connect");
		// lConnect.put("usid", getSessionId(aConnector));
		lConnect.put("sourceId", aConnector.getId());
		// if a unique node id is specified for the client include that
		String lNodeId = aConnector.getNodeId();
		if (lNodeId != null) {
			lConnect.put("unid", lNodeId);
		}
		lConnect.put("clientCount", getConnectorCount());

		// broadcast to all except source
		broadcastToken(aConnector, lConnect);
	}

	/**
	 *
	 *
	 * @param aConnector
	 */
	public void broadcastDisconnectEvent(WebSocketConnector aConnector) {
		if (log.isDebugEnabled()) {
			log.debug("Broadcasting disconnect...");
		}
		// broadcast connect event to other clients of the jWebSocket network
		Token lDisconnect = new Token(Token.TT_EVENT);
		lDisconnect.put("name", "disconnect");
		// lDisconnect.put("usid", getSessionId(aConnector));
		lDisconnect.put("sourceId", aConnector.getId());
		// if a unique node id is specified for the client include that
		String lNodeId = aConnector.getNodeId();
		if (lNodeId != null) {
			lDisconnect.put("unid", lNodeId);
		}
		lDisconnect.put("clientCount", getConnectorCount());

		// broadcast to all except source
		broadcastToken(aConnector, lDisconnect);
	}

	private void sendWelcome(WebSocketConnector aConnector) {
		if (log.isDebugEnabled()) {
			log.debug("Sending welcome...");
		}
		// send "welcome" token to client
		Token lWelcome = new Token(TT_WELCOME);
		lWelcome.put("vendor", JWebSocketCommonConstants.VENDOR);
		lWelcome.put("version", JWebSocketServerConstants.VERSION_STR);
		// here the session id is MANDATORY! to pass to the client!
		lWelcome.put("usid", aConnector.getSession().getSessionId());
		lWelcome.put("sourceId", aConnector.getId());
		// if a unique node id is specified for the client include that
		String lNodeId = aConnector.getNodeId();
		if (lNodeId != null) {
			lWelcome.put("unid", lNodeId);
		}
		lWelcome.put("timeout", aConnector.getEngine().getConfiguration().getTimeout());

		sendToken(aConnector, aConnector, lWelcome);
	}

	/**
	 *
	 */
	private void broadcastLoginEvent(WebSocketConnector aConnector) {
		if (log.isDebugEnabled()) {
			log.debug("Broadcasting login event...");
		}
		// broadcast login event to other clients of the jWebSocket network
		Token lLogin = new Token(Token.TT_EVENT);
		lLogin.put("name", "login");
		lLogin.put("username", getUsername(aConnector));
		lLogin.put("clientCount", getConnectorCount());
		// lLogin.put("usid", getSessionId(aConnector));
		lLogin.put("sourceId", aConnector.getId());
		// if a unique node id is specified for the client include that
		String lNodeId = aConnector.getNodeId();
		if (lNodeId != null) {
			lLogin.put("unid", lNodeId);
		}
		// broadcast to all except source
		broadcastToken(aConnector, lLogin);
	}

	/**
	 *
	 */
	private void broadcastLogoutEvent(WebSocketConnector aConnector) {
		if (log.isDebugEnabled()) {
			log.debug("Broadcasting logout event...");
		}
		// broadcast login event to other clients of the jWebSocket network
		Token lLogout = new Token(Token.TT_EVENT);
		lLogout.put("name", "logout");
		lLogout.put("username", getUsername(aConnector));
		lLogout.put("clientCount", getConnectorCount());
		// lLogout.put("usid", getSessionId(aConnector));
		lLogout.put("sourceId", aConnector.getId());
		// if a unique node id is specified for the client include that
		String lNodeId = aConnector.getNodeId();
		if (lNodeId != null) {
			lLogout.put("unid", lNodeId);
		}
		// broadcast to all except source
		broadcastToken(aConnector, lLogout);
	}

	/**
	 *
	 * @param aConnector
	 * @param aCloseReason
	 */
	private void sendGoodBye(WebSocketConnector aConnector, CloseReason aCloseReason) {
		if (log.isDebugEnabled()) {
			log.debug("Sending good bye...");
		}
		// send "goodBye" token to client
		Token lGoodBye = new Token(TT_GOODBYE);
		lGoodBye.put("vendor", JWebSocketCommonConstants.VENDOR);
		lGoodBye.put("version", JWebSocketServerConstants.VERSION_STR);
		lGoodBye.put("sourceId", aConnector.getId());
		if (aCloseReason != null) {
			lGoodBye.put("reason", aCloseReason.toString().toLowerCase());
		}

		// don't send session-id on good bye, neither required nor desired
		sendToken(aConnector, aConnector, lGoodBye);
	}

	private void login(WebSocketConnector aConnector, Token aToken) {
		Token lResponse = createResponse(aToken);

		String lUsername = aToken.getString("username");
		// TODO: Add authentication and password check
		String lPassword = aToken.getString("password");
		String lGroup = aToken.getString("group");

		if (log.isDebugEnabled()) {
			log.debug("Processing 'login' (username='" + lUsername + "', group='" + lGroup + "') from '" + aConnector + "'...");
		}

		if (lUsername != null) {
			lResponse.put("username", lUsername);
			// lResponse.put("usid", getSessionId(aConnector));
			lResponse.put("sourceId", aConnector.getId());
			// set shared variables
			setUsername(aConnector, lUsername);
			setGroup(aConnector, lGroup);
		} else {
			lResponse.put("code", -1);
			lResponse.put("msg", "missing arguments for 'login' command");
		}

		// send response to client
		sendToken(aConnector, aConnector, lResponse);

		// if successfully logged in...
		if (lUsername != null) {
			// broadcast "login event" to other clients
			broadcastLoginEvent(aConnector);
		}
	}

	private void logout(WebSocketConnector aConnector, Token aToken) {
		Token lResponse = createResponse(aToken);

		if (log.isDebugEnabled()) {
			log.debug("Processing 'logout' (username='" + getUsername(aConnector) + "') from '" + aConnector + "'...");
		}

		if (getUsername(aConnector) != null) {
			// send good bye token as response to client
			sendGoodBye(aConnector, CloseReason.CLIENT);
			// and broadcast the logout event
			broadcastLogoutEvent(aConnector);
			// resetting the username is the only required signal for logout
			// lResponse.put("usid", getSessionId(aConnector));
			lResponse.put("sourceId", aConnector.getId());
			removeUsername(aConnector);
			removeGroup(aConnector);
		} else {
			lResponse.put("code", -1);
			lResponse.put("msg", "not logged in");
			sendToken(aConnector, aConnector, lResponse);
		}
	}

	private void send(WebSocketConnector aConnector, Token aToken) {
		// check if user is allowed to run 'send' command
		if (!SecurityFactory.checkRight(getUsername(aConnector), NS_SYSTEM_DEFAULT + ".send")) {
			sendToken(aConnector, aConnector, createAccessDenied(aToken));
			return;
		}

		Token lResponse = createResponse(aToken);

		WebSocketConnector lTargetConnector;
		String lTargetId = aToken.getString("unid");
		if (lTargetId != null) {
			lTargetConnector = getNode(lTargetId);
		} else {
			// get the target
			lTargetId = aToken.getString("targetId");
			lTargetConnector = getConnector(lTargetId);
		}

		/*
		if (getUsername(aConnector) != null)
		{
		 */
		if (lTargetConnector != null) {
			if (log.isDebugEnabled()) {
				log.debug("Processing 'send' (username='"
						+ getUsername(aConnector) + "') from '"
						+ aConnector + "' to " + lTargetId + "...");
			}
			aToken.put("sourceId", aConnector.getId());
			sendToken(aConnector, lTargetConnector, aToken);
		} else {
			log.warn("Target connector '" + lTargetId + "' not found.");
		}
		/*
		} else {
		lResponse.put("code", -1);
		lResponse.put("msg", "not logged in");
		sendToken(aConnector, aConnector, lResponse);
		}
		 */
	}

	private void broadcast(WebSocketConnector aConnector, Token aToken) {

		// check if user is allowed to run 'broadcast' command
		if (!SecurityFactory.checkRight(getUsername(aConnector), NS_SYSTEM_DEFAULT + ".broadcast")) {
			sendToken(aConnector, aConnector, createAccessDenied(aToken));
			return;
		}

		Token lResponse = createResponse(aToken);

		if (log.isDebugEnabled()) {
			log.debug("Processing 'broadcast' (username='"
					+ getUsername(aConnector) + "') from '"
					+ aConnector + "'...");
		}
		/*
		if (getUsername(aConnector) != null)
		{
		 */
		aToken.put("sourceId", aConnector.getId());
		// don't distribute session id here!
		aToken.remove("usid");
		String lSenderIncluded = aToken.getString("senderIncluded");
		String lResponseRequested = aToken.getString("responseRequested");
		boolean bSenderIncluded = (lSenderIncluded != null
				&& lSenderIncluded.equals("true"));
		boolean bResponseRequested = (lResponseRequested != null
				&& lResponseRequested.equals("true"));

		// broadcast the token
		broadcastToken(aConnector, aToken,
				new BroadcastOptions(bSenderIncluded, bResponseRequested));

		// check if response was requested
		if (bResponseRequested) {
			sendToken(aConnector, aConnector, lResponse);
		}
		/*
		} else {
		lResponse.put("code", -1);
		lResponse.put("msg", "not logged in");
		sendToken(aConnector, lResponse);
		}
		 */
	}

	private void close(WebSocketConnector aConnector, Token aToken) {
		int lTimeout = aToken.getInteger("timeout", 0);
		// if logged in...
		if (getUsername(aConnector) != null) {
			// only send a good bye message if timeout is > 0
			if (lTimeout > 0) {
				sendGoodBye(aConnector, CloseReason.CLIENT);
			}
			// broadcast the logout event.
			broadcastLogoutEvent(aConnector);
		}
		// reset the username, we're no longer logged in
		removeUsername(aConnector);

		if (log.isDebugEnabled()) {
			log.debug("Closing client "
					+ (lTimeout > 0 ? "with timeout " + lTimeout + "ms" : "immediately")
					+ "...");
		}

		// don't send a response here! We're about to close the connection!
		// broadcasts disconnect event to other clients
		aConnector.stopConnector(CloseReason.CLIENT);
	}

	/**
	 *
	 * @param aToken
	 */
	private void echo(WebSocketConnector aConnector, Token aToken) {
		Token lResponseToken = createResponse(aToken);

		String lData = aToken.getString("data");
		if (lData != null) {
			if (log.isDebugEnabled()) {
				log.debug("echo " + lData);
			}
		} else {
			lResponseToken.put("code", -1);
			lResponseToken.put("msg", "missing 'data' argument for 'echo' command");
		}
		sendToken(aConnector, aConnector, lResponseToken);
	}

	/**
	 *
	 * @param aConnector
	 * @param aToken
	 */
	public void ping(WebSocketConnector aConnector, Token aToken) {
		String lEcho = aToken.getString("echo");

		if (log.isDebugEnabled()) {
			log.debug("Processing 'Ping' (echo='" + lEcho + "') from '"
					+ aConnector + "'...");
		}

		if (lEcho.equalsIgnoreCase("true")) {
			Token lResponseToken = createResponse(aToken);
			// TODO: here could optionally send a time stamp
			// TODO: implement response time on client!
			// lResponseToken.put("","");
			sendToken(aConnector, aConnector, lResponseToken);
		}
	}

	/**
	 *
	 * @param aConnector
	 * @param aToken
	 */
	public void getClients(WebSocketConnector aConnector, Token aToken) {
		Token lResponseToken = createResponse(aToken);

		if (log.isDebugEnabled()) {
			log.debug("Processing 'getClients' from '"
					+ aConnector + "'...");
		}

		if (getUsername(aConnector) != null) {
			String lGroup = aToken.getString("group");
			Integer lMode = aToken.getInteger("mode", 0);
			FastMap lFilter = new FastMap();
			lFilter.put(BaseConnector.VAR_USERNAME, ".*");
			List<String> listOut = new FastList<String>();
			for (WebSocketConnector lConnector : getServer().selectConnectors(lFilter).values()) {
				listOut.add(getUsername(lConnector) + "@"
						+ lConnector.getRemotePort());
			}
			lResponseToken.put("clients", listOut);
			lResponseToken.put("count", listOut.size());
		} else {
			lResponseToken.put("code", -1);
			lResponseToken.put("msg", "not logged in");
		}

		sendToken(aConnector, aConnector, lResponseToken);
	}

	/**
	 * allocates a "non-interruptable" communication channel between two clients.
	 * @param aConnector
	 * @param aToken
	 */
	public void allocChannel(WebSocketConnector aConnector, Token aToken) {
		Token lResponseToken = createResponse(aToken);

		if (log.isDebugEnabled()) {
			log.debug("Processing 'allocChannel' from '"
					+ aConnector + "'...");
		}
	}

	/**
	 * deallocates a "non-interruptable" communication channel between two clients.
	 * @param aConnector
	 * @param aToken
	 */
	public void deallocChannel(WebSocketConnector aConnector, Token aToken) {
		Token lResponseToken = createResponse(aToken);

		if (log.isDebugEnabled()) {
			log.debug("Processing 'deallocChannel' from '"
					+ aConnector + "'...");
		}
	}
}
