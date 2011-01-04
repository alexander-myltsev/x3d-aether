//	---------------------------------------------------------------------------
//	jWebSocket - jWebSocket Sample Plug-In
//	Copyright (c) 2010 jWebSocket.org, Alexander Schulze, Innotrade GmbH
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
package org.jwebsocket.plugins.sample;

import java.util.Date;
import java.util.List;
import java.util.Map;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.apache.log4j.Logger;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.api.WebSocketEngine;
import org.jwebsocket.config.JWebSocketServerConstants;
import org.jwebsocket.kit.CloseReason;
import org.jwebsocket.kit.PlugInResponse;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.plugins.TokenPlugIn;
import org.jwebsocket.token.Token;

/**
 *
 * @author aschulze
 */
public class SamplePlugIn extends TokenPlugIn {

	private static Logger mLog = Logging.getLogger(SamplePlugIn.class);
	// if namespace changed update client plug-in accordingly!
	private final static String NS_SAMPLE = JWebSocketServerConstants.NS_BASE + ".plugins.samples";
	private final static String SAMPLE_VAR = NS_SAMPLE + ".started";

	/**
	 *
	 */
	public SamplePlugIn() {
		if (mLog.isDebugEnabled()) {
			mLog.debug("Instantiating sample plug-in...");
		}
		// specify default name space for sample plugin
		this.setNamespace(NS_SAMPLE);
	}

	@Override
	public void connectorStarted(WebSocketConnector aConnector) {
		// this method is called every time when a client
		// connected to the server
		aConnector.setVar(SAMPLE_VAR, new Date().toString());
	}

	@Override
	public void connectorStopped(WebSocketConnector aConnector, CloseReason aCloseReason) {
		// this method is called every time when a client
		// disconnected from the server
	}

	@Override
	public void engineStarted(WebSocketEngine aEngine) {
		// this method is called when the engine has started
		super.engineStarted(aEngine);
	}

	@Override
	public void engineStopped(WebSocketEngine aEngine) {
		// this method is called when the engine has stopped
		super.engineStopped(aEngine);
	}

	@Override
	public void processToken(PlugInResponse aResponse, WebSocketConnector aConnector, Token aToken) {

		// get the type of the token
		// the type can be associated with a "command"
		String lType = aToken.getType();

		// get the namespace of the token
		// each plug-in should have its own unique namespace
		String lNS = aToken.getNS();

		// check if token has a type and a matching namespace
		if (lType != null && lNS != null && lNS.equals(getNamespace())) {

			// get the server time
			if ("requestServerTime".equals(lType)) {
				// create the response token
				// this includes the unique token-id
				Token lResponse = createResponse(aToken);

				// add the "time" and "started" field
				lResponse.put("time", new Date().toString());
				lResponse.put("started", aConnector.getVar(SAMPLE_VAR));

				// put an array into the token
				lResponse.put("array", new Object[]{1, 2, 3, 'a', 'b', 'c', "ABC", "XYZ", true, false});

				// put a map into the token
				Map lMap = new FastMap();
				lMap.put("MapItem1", 1);
				lMap.put("MapItem2", 2);
				lResponse.put("map", lMap);

				List lList = new FastList();
				lList.add("ListItem1");
				lList.add("ListItem2");
				lResponse.put("list", lList);

				// put a token into a token
				Token lToken = new Token();
				lToken.put("number", 1);
				lToken.put("string", "test");
				lToken.put("float", 1.23456);
				lToken.put("boolean", false);
				lToken.put("array", new Object[]{4, 5, 6, 'd', 'e', 'f', "DEF", "UVW", false, true});

				// insert subtoken, another level in object's hierarchy...
				Token lSubToken = new Token();
				lSubToken.put("number", 2);
				lSubToken.put("string", "demo");
				lSubToken.put("float", 2.34567);
				lSubToken.put("boolean", true);
				lSubToken.put("array", new Object[]{7, 8, 9, 'g', 'h', 'i', "GHI", "RST", true, false});
				lToken.put("subtoken", lSubToken);

				// put the token incl. its subtoken into the response
				lResponse.put("token", lToken);

				// send the response token back to the client
				sendToken(aConnector, aConnector, lResponse);

			} else if (lType.equals("processComplexObject")) {
				// simply echo the complex object
				sendToken(aConnector, aConnector, aToken);
			}
		}
	}
}
