//	---------------------------------------------------------------------------
//	jWebSocket - jWebSocket Filesystem Plug-In
//	Copyright (c) 2010 Alexander Schulze, Innotrade GmbH
//	---------------------------------------------------------------------------
//  THIS CODE IS FOR RESEARCH, EVALUATION AND TEST PURPOSES ONLY!
//  THIS CODE MAY BE SUBJECT TO CHANGES WITHOUT ANY NOTIFICATION!
//	THIS CODE IS NOT YET SECURE AND MAY NOT BE USED FOR PRODUCTION ENVIRONMENTS!
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
package org.jwebsocket.plugins.filesystem;

import java.io.File;
import java.io.IOException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.config.JWebSocketCommonConstants;
import org.jwebsocket.config.JWebSocketServerConstants;
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
public class FileSystemPlugIn extends TokenPlugIn {

	private static Logger log = Logging.getLogger(FileSystemPlugIn.class);
	// if namespace changed update client plug-in accordingly!
	private static final String NS_FILESYSTEM = JWebSocketServerConstants.NS_BASE + ".plugins.filesystem";
	// TODO: make these settings configurable
	private static String PRIVATE_DIR_KEY = "alias:privateDir";
	private static String PUBLIC_DIR_KEY = "alias:publicDir";
	private static String WEB_ROOT_KEY = "alias:webroot";
	private static String PRIVATE_DIR_DEF = "%JWEBSOCKET_HOME%/private/{username}/";
	private static String PUBLIC_DIR_DEF = "%JWEBSOCKET_HOME%/public/";
	private static String WEB_ROOT_DEF = "http://jwebsocket.org/";

	/**
	 *
	 */
	public FileSystemPlugIn() {
		if (log.isDebugEnabled()) {
			log.debug("Instantiating file system plug-in...");
		}
		// specify default name space for admin plugin
		this.setNamespace(NS_FILESYSTEM);
	}

	@Override
	public void processToken(PlugInResponse aResponse, WebSocketConnector aConnector, Token aToken) {
		String lType = aToken.getType();
		String lNS = aToken.getNS();

		if (lType != null && (lNS == null || lNS.equals(getNamespace()))) {
			// select from database
			if (lType.equals("save")) {
				save(aConnector, aToken);
			} else if (lType.equals("load")) {
				load(aConnector, aToken);
			}
		}
	}

	/**
	 * save a file
	 * @param aConnector
	 * @param aToken
	 */
	public void save(WebSocketConnector aConnector, Token aToken) {
		TokenServer lServer = getServer();
		String lMsg;

		if (log.isDebugEnabled()) {
			log.debug("Processing 'save'...");
		}

		// check if user is allowed to run 'save' command
		if (!SecurityFactory.checkRight(lServer.getUsername(aConnector), NS_FILESYSTEM + ".save")) {
			if (log.isDebugEnabled()) {
				log.debug("Returning 'Access denied'...");
			}
			lServer.sendToken(aConnector, lServer.createAccessDenied(aToken));
			return;
		}

		// instantiate response token
		Token lResponse = lServer.createResponse(aToken);

		// obtain required parameters for file load operation
		String lFilename = aToken.getString("filename");
		String lScope = aToken.getString("scope", JWebSocketCommonConstants.SCOPE_PRIVATE);

		// TODO: Replace optional variables in path like %JWEBSOCKET_HOME% by env var values!

		// scope may be "private" or "public"
		String lBaseDir;
		if (JWebSocketCommonConstants.SCOPE_PRIVATE.equals(lScope)) {
			String lUsername = getUsername(aConnector);
			lBaseDir = getSetting(PRIVATE_DIR_KEY, PRIVATE_DIR_DEF);
			if (lUsername != null) {
				lBaseDir = lBaseDir.replace("{username}", lUsername);
			} else {
				lMsg = "not authenticated to save private file";
				if (log.isDebugEnabled()) {
					log.debug(lMsg);
				}
				lResponse.put("code", -1);
				lResponse.put("msg", lMsg);
				// send error response to requester
				lServer.sendToken(aConnector, lResponse);
				return;
			}
		} else if (JWebSocketCommonConstants.SCOPE_PUBLIC.equals(lScope)) {
			lBaseDir = getSetting(PUBLIC_DIR_KEY, PUBLIC_DIR_DEF);
		} else {
			lMsg = "invalid scope";
			if (log.isDebugEnabled()) {
				log.debug(lMsg);
			}
			lResponse.put("code", -1);
			lResponse.put("msg", lMsg);
			// send error response to requester
			lServer.sendToken(aConnector, lResponse);
			return;
		}

		Boolean lNotify = aToken.getBoolean("notify", false);
		String lBase64 = aToken.getString("data");
		byte[] lBA = null;
		if (lBase64 != null) {
			lBA = Base64.decodeBase64(lBase64);
		}

		// complete the response token
		String lFullPath = lBaseDir + lFilename;
		File lFile = new File(lFullPath);
		try {
			// prevent two threads at a time writing to the same file
			synchronized (this) {
				// force create folder if not yet exists
				File lDir = new File(FilenameUtils.getFullPath(lFullPath));
				FileUtils.forceMkdir(lDir);
				FileUtils.writeByteArrayToFile(lFile, lBA);
			}
		} catch (IOException ex) {
			lResponse.put("code", -1);
			lResponse.put("msg", ex.getMessage());
		}

		// send response to requester
		lServer.sendToken(aConnector, lResponse);

		// send notification event to other affected clients
		// to allow to update their content (if desired)
		if (lNotify) {
			// create token of type "event"
			Token lEvent = new Token(Token.TT_EVENT);
			// include name space of this plug-in
			lEvent.setNS(NS_FILESYSTEM);
			lEvent.put("name", "filesaved");
			lEvent.put("filename", lFilename);
			lEvent.put("sourceId", aConnector.getId());
			lEvent.put("url", getSetting(WEB_ROOT_KEY, WEB_ROOT_DEF) + lFilename);
			// TODO: Limit notification to desired scope
			lServer.broadcastToken(lEvent);
		}
	}

	/**
	 * load a file
	 * @param aConnector
	 * @param aToken
	 */
	public void load(WebSocketConnector aConnector, Token aToken) {
		TokenServer lServer = getServer();
		String lMsg;

		if (log.isDebugEnabled()) {
			log.debug("Processing 'load'...");
		}

		// check if user is allowed to run 'load' command
		if (!SecurityFactory.checkRight(lServer.getUsername(aConnector), NS_FILESYSTEM + ".load")) {
			if (log.isDebugEnabled()) {
				log.debug("Returning 'Access denied'...");
			}
			lServer.sendToken(aConnector, lServer.createAccessDenied(aToken));
			return;
		}

		// obtain required parameters for file load operation
		String lFilename = aToken.getString("filename");
		String lScope = aToken.getString("scope", JWebSocketCommonConstants.SCOPE_PRIVATE);
		String lData = "";

		// instantiate response token
		Token lResponse = lServer.createResponse(aToken);

		String lBaseDir;
		if (JWebSocketCommonConstants.SCOPE_PRIVATE.equals(lScope)) {
			String lUsername = getUsername(aConnector);
			lBaseDir = getSetting(PRIVATE_DIR_KEY, PRIVATE_DIR_DEF);
			if (lUsername != null) {
				lBaseDir = lBaseDir.replace("{username}", lUsername);
			} else {
				lMsg = "not authenticated to load private file";
				if (log.isDebugEnabled()) {
					log.debug(lMsg);
				}
				lResponse.put("code", -1);
				lResponse.put("msg", lMsg);
				// send error response to requester
				lServer.sendToken(aConnector, lResponse);
				return;
			}
		} else if (JWebSocketCommonConstants.SCOPE_PUBLIC.equals(lScope)) {
			lBaseDir = getSetting(PUBLIC_DIR_KEY, PUBLIC_DIR_DEF);
		} else {
			lMsg = "invalid scope";
			if (log.isDebugEnabled()) {
				log.debug(lMsg);
			}
			lResponse.put("code", -1);
			lResponse.put("msg", lMsg);
			// send error response to requester
			lServer.sendToken(aConnector, lResponse);
			return;
		}

		// complete the response token
		File lFile = new File(lBaseDir + lFilename);
		byte[] lBA = null;
		try {
			lBA = FileUtils.readFileToByteArray(lFile);
			if (lBA != null && lBA.length > 0) {
				lData = new String(Base64.encodeBase64(lBA), "UTF-8");
			}
			lResponse.put("data", lData);
		} catch (IOException ex) {
			lResponse.put("code", -1);
			lResponse.put("msg", ex.getMessage());
		}

		// send response to requester
		lServer.sendToken(aConnector, lResponse);
	}
}
