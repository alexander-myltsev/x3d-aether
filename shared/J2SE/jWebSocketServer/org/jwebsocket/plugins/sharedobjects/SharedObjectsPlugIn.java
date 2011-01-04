//	---------------------------------------------------------------------------
//	jWebSocket - Shared Objects Plug-In
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
package org.jwebsocket.plugins.sharedobjects;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javolution.util.FastList;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONStringer;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.config.JWebSocketServerConstants;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.kit.PlugInResponse;
import org.jwebsocket.plugins.TokenPlugIn;
import org.jwebsocket.token.Token;

/**
 * Pending...
 * @author aschulze
 */
public class SharedObjectsPlugIn extends TokenPlugIn {

	private static Logger log = Logging.getLogger(SharedObjectsPlugIn.class);
	// if namespace is changed update client plug-in accordingly!
	private String NS_SHARED_OBJECTS = JWebSocketServerConstants.NS_BASE + ".plugins.sharedObjs";
	private SharedObjects sharedObjects = new SharedObjects();
	// if data types are changed update client plug-in accordingly!
	private List DATA_TYPES = new FastList(Arrays.asList(new String[]{
				"number", "string", "boolean", "object",
				"set", "list", "map", "table"}));

	/**
	 *
	 */
	public SharedObjectsPlugIn() {
		// specify default name space
		this.setNamespace(NS_SHARED_OBJECTS);
	}

	private boolean isDataTypeValid(String aDataType, WebSocketConnector aConnector, Token aResponse) {
		boolean isValid = ((aDataType != null && DATA_TYPES.contains(aDataType)));
		if (!isValid) {
			aResponse.put("code", -1);
			aResponse.put("msg", "invalid datatype '" + aDataType + "'");
			getServer().sendToken(aConnector, aResponse);
		}
		return isValid;
	}

	private boolean doesContain(String aID, WebSocketConnector aConnector, Token aResponse) {
		boolean isValid = ((aID != null && sharedObjects.contains(aID)));
		if (!isValid) {
			aResponse.put("code", -1);
			aResponse.put("msg", "object '" + aID + "' not found");
			getServer().sendToken(aConnector, aResponse);
		}
		return isValid;
	}

	private boolean alreadyExists(String aID, WebSocketConnector aConnector, Token aResponse) {
		boolean isValid = ((aID != null && sharedObjects.contains(aID)));
		if (isValid) {
			aResponse.put("code", -1);
			aResponse.put("msg", "object '" + aID + "' already exists");
			getServer().sendToken(aConnector, aResponse);
		}
		return isValid;
	}

	private Object string2Object(String aDataType, String aValue) {
		Object lRes = null;

		// number
		if (aDataType.equals("number")) {
			try {
				lRes = Double.parseDouble(aValue);
			} catch (NumberFormatException ex) {
			}

			// string
		} else if (aDataType.equals("string")) {
			lRes = aValue;
		}

		return lRes;
	}

	private String object2String(String aDataType, Object aObject) {
		// all supported objects should provide the toString() method
		return aObject.toString();
	}

	@Override
	public void processToken(PlugInResponse aResponse, WebSocketConnector aConnector, Token aToken) {
		String lType = aToken.getType();
		String lNS = aToken.getNS();
		String lID = aToken.getString("id");
		String lDataType = aToken.getString("datatype");
		String lValue = aToken.getString("value");

	if (lType != null && (lNS == null || lNS.equals(NS_SHARED_OBJECTS))) {

			Token lResponse = getServer().createResponse(aToken);

			// create
			if (lType.equals("create")) {
				if (log.isDebugEnabled()) {
					log.debug("Processing 'create'...");
				}
				if (!isDataTypeValid(lDataType, aConnector, lResponse)) {
					return;
				}
				if (alreadyExists(lID, aConnector, lResponse)) {
					return;
				}
				sharedObjects.put(lID, string2Object(lDataType, lValue));

				Token lBCT = new Token(lNS, "event");
				lBCT.put("name", "created");
				lBCT.put("id", lID);
				lBCT.put("datatype", lDataType);
				lBCT.put("value", lValue);
				getServer().broadcastToken(aConnector, lBCT);

				// destroy
			} else if (lType.equals("destroy")) {
				if (log.isDebugEnabled()) {
					log.debug("Processing 'destroy'...");
				}
				if (!doesContain(lID, aConnector, lResponse)) {
					return;
				}
				sharedObjects.remove(lID);

				Token lBCT = new Token(lNS, "event");
				lBCT.put("name", "destroyed");
				lBCT.put("id", lID);
				getServer().broadcastToken(aConnector, lBCT);

				// get
			} else if (lType.equals("get")) {
				if (log.isDebugEnabled()) {
					log.debug("Processing 'get'...");
				}
				if (!doesContain(lID, aConnector, lResponse)) {
					return;
				}
				Object lObj = sharedObjects.get(lID);
				lResponse.put("id", lID);
				lResponse.put("result", lObj.toString());

				// put
			} else if (lType.equals("update")) {
				if (log.isDebugEnabled()) {
					log.debug("Processing 'update'...");
				}
				if (!isDataTypeValid(lDataType, aConnector, lResponse)) {
					return;
				}
				sharedObjects.put(lID, string2Object(lDataType, lValue));
				Token lBCT = new Token(lNS, "event");
				lBCT.put("name", "updated");
				lBCT.put("id", lID);
				lBCT.put("datatype", lDataType);
				lBCT.put("value", lValue);
				getServer().broadcastToken(aConnector, lBCT);

				// init
			} else if (lType.equals("init")) {
				if (log.isDebugEnabled()) {
					log.debug("Processing 'init'...");
				}
				Token lBCT = new Token(lNS, "event");
				lBCT.put("name", "init");

				String lData = null;
				try {
					JSONStringer jsonStringer = new JSONStringer();
					// start main object
					jsonStringer.object();
					// iterate through all items (fields) of the token
					Iterator<String> lIterator = sharedObjects.getKeys().iterator();
					while (lIterator.hasNext()) {
						String lKey = lIterator.next();
						Object lVal = sharedObjects.get(lKey);
						if (lVal instanceof Collection) {
							jsonStringer.key(lKey).array();
							for (Object item : (Collection) lVal) {
								jsonStringer.value(item);
							}
							jsonStringer.endArray();
						} else {
							jsonStringer.key(lKey).value(lVal);
						}
					}
					// end main object
					jsonStringer.endObject();
					lData = jsonStringer.toString();
				} catch (JSONException ex) {
					log.error(ex.getClass().getSimpleName() + ": " + ex.getMessage());
				}
				lBCT.put("value", lData);
				getServer().sendToken(aConnector, lBCT);

			} else {
				log.warn("Invalid command " + lType + " received...");
				lResponse.put("code", -1);
				lResponse.put("msg", "invalid type '" + lType + "'");
			}

			getServer().sendToken(aConnector, lResponse);
		}

	}
}
