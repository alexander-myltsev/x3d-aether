//	---------------------------------------------------------------------------
//	jWebSocket - RPCPlugIn Plug-In
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
package org.jwebsocket.plugins.rpc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Map.Entry;
import javolution.util.FastMap;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.api.WebSocketEngine;
import org.jwebsocket.config.JWebSocketConfig;
import org.jwebsocket.config.JWebSocketServerConstants;
import org.jwebsocket.factory.JWebSocketJarClassLoader;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.kit.PlugInResponse;
import org.jwebsocket.plugins.TokenPlugIn;
import org.jwebsocket.security.SecurityFactory;
import org.jwebsocket.token.Token;

/**
 * This plug-in provides all the functionality for remote procedure calls
 * (RPC) for client-to-server (C2S) apps, and reverse remote procedure calls
 * (RRPC) for server-to-client (S2C) or client-to-client apps (C2C).
 * @author aschulze
 */
public class RPCPlugIn extends TokenPlugIn {

	private static Logger mLog = Logging.getLogger(RPCPlugIn.class);
	private Map<String, Object> mGrantedProcs = new FastMap<String, Object>();
	// private DemoRPCServer mRpcServer = null;
	// if namespace changed update client plug-in accordingly!
	private String NS_RPC_DEFAULT = JWebSocketServerConstants.NS_BASE + ".plugins.rpc";
	// private Map<String, String> mJars = new FastMap<String, String>();
	// TODO: use RpcCallable instead of Object here!
	private Map<String, Object> mClasses = new FastMap<String, Object>();

	// TODO: RRPC calls do not yet allow quotes in arguments
	// TODO: We need simple unique IDs to address a certain target, session id not suitable here.
	// TODO: Show target(able) clients in a drop down box
	// TODO: RPC demo does not show other clients logging in
	/**
	 *
	 */
	public RPCPlugIn() {
		if (mLog.isDebugEnabled()) {
			mLog.debug("Instantiating rpc plug-in...");
		}
		// specify default name space
		this.setNamespace(NS_RPC_DEFAULT);

		// currently this is the only supported RPCPlugIn server
		// mRpcServer = new DemoRPCServer();

	}

	@Override
	public void engineStarted(WebSocketEngine aEngine) {

		// TODO: move JWebSocketJarClassLoader into ServerAPI module ?
		JWebSocketJarClassLoader lClassLoader = new JWebSocketJarClassLoader();
		Class lClass = null;

		// load map of RPC libraries first
		// also load map of granted procs
		Map<String, String> lSettings = getSettings();
		String lKey;
		String lValue;
		for (Entry<String, String> lSetting : lSettings.entrySet()) {
			lKey = lSetting.getKey();
			lValue = lSetting.getValue();
			if (lKey.startsWith("class:")) {
				String lClassName = lKey.substring(6);
				try {
					if (mLog.isDebugEnabled()) {
						mLog.debug("Trying to load class '" + lClassName + "' from classpath...");
					}
					lClass = Class.forName(lClassName);
				} catch (Exception ex) {
					mLog.error(ex.getClass().getSimpleName()
							+ " loading class from classpath: "
							+ ex.getMessage()
							+ ", hence trying to load from jar.");
				}
				// if class could not be loaded from classpath...
				if (lClass == null) {
					String lJarFilePath = null;
					try {
						lJarFilePath = JWebSocketConfig.getLibraryFolderPath(lValue);
						if (mLog.isDebugEnabled()) {
							mLog.debug("Trying to load class '" + lClassName + "' from jar '" + lJarFilePath + "'...");
						}
						lClassLoader.addFile(lJarFilePath);
						lClass = lClassLoader.loadClass(lClassName);
						if (mLog.isDebugEnabled()) {
							mLog.debug("Class '" + lClassName + "' successfully loaded from '" + lJarFilePath + "'.");
						}
					} catch (Exception ex) {
						mLog.error(ex.getClass().getSimpleName() + " loading jar '" + lJarFilePath + "': " + ex.getMessage());
					}
				}
				// could the class be loaded?
				if (lClass != null) {
					try {
						Object lInstance = lClass.newInstance();
						mClasses.put(lClassName, lInstance);
					} catch (Exception ex) {
						mLog.error(ex.getClass().getSimpleName() + " creating '" + lClassName + "' instance : " + ex.getMessage());
					}
				}
			}
		}
		for (Entry<String, String> lSetting : lSettings.entrySet()) {
			lKey = lSetting.getKey();
			lValue = lSetting.getValue();
			if (lKey.startsWith("roles:")) {
				lKey = lKey.substring(6);
				mGrantedProcs.put(lKey, lValue);
			}
		}
		if (mLog.isDebugEnabled()) {
			mLog.debug("Available RPC classes: " + mClasses.toString());
			mLog.debug("Granted RPC methods: " + mGrantedProcs.toString());
		}
	}

	/*
	@Override
	public void connectorStarted(WebSocketConnector aConnector) {
	}
	 */
	@Override
	public void processToken(PlugInResponse aResponse, WebSocketConnector aConnector, Token aToken) {
		String lType = aToken.getType();
		String lNS = aToken.getNS();

		if (lType != null && (lNS == null || lNS.equals(getNamespace()))) {
			// remote procedure call
			if (lType.equals("rpc")) {
				rpc(aConnector, aToken);
				// reverse remote procedure call
			} else if (lType.equals("rrpc")) {
				rrpc(aConnector, aToken);
			}
		}
	}

	/**
	 * remote procedure call (RPC)
	 * @param aConnector 
	 * @param aToken
	 */
	public void rpc(WebSocketConnector aConnector, Token aToken) {
		// check if user is allowed to run 'rpc' command
		if (!SecurityFactory.checkRight(getUsername(aConnector), NS_RPC_DEFAULT + ".rpc")) {
			sendToken(aConnector, aConnector, createAccessDenied(aToken));
			return;
		}

		Token lResponseToken = createResponse(aToken);

		String lClassName = aToken.getString("classname");
		String lMethod = aToken.getString("method");
		Object lArgs = aToken.get("args");
		// TODO: Tokens should always be a map of maps
		if (lArgs instanceof JSONObject) {
			lArgs = new Token((JSONObject) lArgs);
		}

		String lMsg = null;

		if (mLog.isDebugEnabled()) {
			mLog.debug("Processing RPC to class '" + lClassName + "', method '" + lMethod + "', args: '" + lArgs + "'...");
		}

		String lKey = lClassName + "." + lMethod;
		if (mGrantedProcs.containsKey(lKey)) {
			// class is ignored until security restrictions are finished.
			try {
				// TODO: use RpcCallable here!
				Object lInstance = mClasses.get(lClassName);
				if (lInstance != null) {
					Object lObj = call(lInstance, lMethod, lArgs);
					lResponseToken.put("result", lObj);
				} else {
					lMsg = "Class '" + lClassName + "' not found or not properly loaded.";
				}
			} catch (NoSuchMethodException ex) {
				lMsg = "NoSuchMethodException calling '" + lMethod + "' for class " + lClassName + ": " + ex.getMessage();
			} catch (IllegalAccessException ex) {
				lMsg = "IllegalAccessException calling '" + lMethod + "' for class " + lClassName + ": " + ex.getMessage();
			} catch (InvocationTargetException ex) {
				lMsg = "InvocationTargetException calling '" + lMethod + "' for class " + lClassName + ": " + ex.getMessage();
			}
		} else {
			lMsg = "Call to " + lKey + " is not granted!";
		}
		if (lMsg != null) {
			lResponseToken.put("code", -1);
			lResponseToken.put("msg", lMsg);
		}

		/* just for testing purposes of multi-threaded rpc's
		try {
		Thread.sleep(3000);
		} catch (InterruptedException ex) {
		}
		 */

		sendToken(aConnector, aConnector, lResponseToken);
	}

	/**
	 * reverse remote procedure call (RRPC)
	 * @param aConnector
	 * @param aToken
	 */
	public void rrpc(WebSocketConnector aConnector, Token aToken) {
		// check if user is allowed to run 'rrpc' command
		if (!SecurityFactory.checkRight(getUsername(aConnector), NS_RPC_DEFAULT + ".rrpc")) {
			sendToken(aConnector, aConnector, createAccessDenied(aToken));
			return;
		}

		String lNS = aToken.getNS();

		// get the target
		String lTargetId = aToken.getString("targetId");
		// get the remote classname
		String lClassname = aToken.getString("classname");
		// get the remote method name
		String lMethod = aToken.getString("method");
		// get the remote arguments
		String lArgs = aToken.getString("args");

		// TODO: find solutions for hardcoded engine id
		WebSocketConnector lTargetConnector =
				getServer().getConnector("tcp0", lTargetId);

		if (mLog.isDebugEnabled()) {
			mLog.debug("Processing 'rrpc'...");
		}
		if (lTargetConnector != null) {
			Token lRRPC = new Token(lNS, "rrpc");
			lRRPC.put("classname", lClassname);
			lRRPC.put("method", lMethod);
			lRRPC.put("args", lArgs);
			lRRPC.put("sourceId", aConnector.getRemotePort());

			sendToken(aConnector, lTargetConnector, lRRPC);
		} else {
			Token lResponse = createResponse(aToken);
			lResponse.put("code", -1);
			lResponse.put("error", "Target " + lTargetId + " not found.");
			sendToken(aConnector, aConnector, lResponse);
		}
	}

	/**
	 *
	 * @param aClassName
	 * @param aURL
	 * @return
	 */
	public static Class loadClass(String aClassName, String aURL) {
		Class lClass = null;
		try {
			URLClassLoader lUCL = new URLClassLoader(new URL[]{new URL(aURL)});
			// load class using previously defined class loader
			lClass = Class.forName(aClassName, true, lUCL);
			if (mLog.isDebugEnabled()) {
				mLog.debug("Class '" + lClass.getName() + "' loaded!");
			}
		} catch (ClassNotFoundException ex) {
			mLog.error("Class not found exception: " + ex.getMessage());
		} catch (MalformedURLException ex) {
			mLog.error("MalformesURL exception: " + ex.getMessage());
		}
		return lClass;
	}

	/**
	 *
	 * @param aClassName
	 * @return
	 */
	public static Class loadClass(String aClassName) {
		// return loadClass(aClassName, "file:/" + JWebSocketServerConstants.CLASS_OUT_PATH);
		return null;
	}

	/**
	 *
	 * @param aClass
	 * @param aArgs
	 * @return
	 */
	public static Object createInstance(Class aClass, Object[] aArgs) {
		Object lObj = null;
		try {
			Class[] lCA = new Class[aArgs != null ? aArgs.length : 0];
			for (int i = 0; i < lCA.length; i++) {
				lCA[i] = aArgs[i].getClass();
			}
			Constructor lConstructor = aClass.getConstructor(lCA);
			lObj = lConstructor.newInstance(aArgs);
			if (mLog.isDebugEnabled()) {
				mLog.debug("Object '" + aClass.getName() + "' instantiated!");
			}
		} catch (Exception ex) {
			mLog.error("Exception instantiating class " + aClass.getName() + ": " + ex.getMessage());
		}
		return lObj;
	}

	/**
	 *
	 * @param aClass
	 * @return
	 */
	public static Object createInstance(Class aClass) {
		return createInstance(aClass, null);
	}

	/**
	 *
	 * @param aClassName
	 * @return
	 */
	public static Object createInstance(String aClassName) {
		Class lClass = loadClass(aClassName);
		return createInstance(lClass, null);
	}

	/**
	 *
	 * @param aClassName
	 * @param aArgs
	 * @return
	 */
	public static Object createInstance(String aClassName, Object[] aArgs) {
		Class lClass = loadClass(aClassName);
		return createInstance(lClass, aArgs);
	}

	/**
	 *
	 * @param aInstance
	 * @param aName
	 * @param aArgs
	 * @return
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static Object call(Object aInstance, String aName, Object aArgs)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Object lObj = null;

		Class lClass = aInstance.getClass();
		Class[] lCA;
		if (aArgs != null) {
			lCA = new Class[]{aArgs.getClass()};
		} else {
			lCA = new Class[0];
		}
		Method lMethod = lClass.getMethod(aName, lCA);
		Object lArg = aArgs;
		lObj = lMethod.invoke(aInstance, lArg);

		return lObj;
	}

	/*
	public static Object call(Object aInstance, String aName, Object... aArgs) {
	Object lObj = null;
	try {
	Class lClass = aInstance.getClass();
	Method lMethod = lClass.getMethod(aName, new Class[]{Object.class});
	Object lArg = aArgs;
	lObj = lMethod.invoke(aInstance, lArg);
	} catch (NoSuchMethodException ex) {
	log.debug("No such method exception calling '" + aName + "' for class " + aInstance.getClass().getName() + ": " + ex.getMessage());
	} catch (Exception ex) {
	log.debug("Exception calling '" + aName + "' for class " + aInstance.getClass().getName() + ": " + ex.getMessage());
	}
	return lObj;
	}
	 */
}
