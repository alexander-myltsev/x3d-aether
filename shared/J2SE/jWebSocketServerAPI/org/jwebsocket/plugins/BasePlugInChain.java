//	---------------------------------------------------------------------------
//	jWebSocket - Plug in chain for incoming requests (per server)
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
import org.jwebsocket.api.WebSocketPlugIn;
import org.jwebsocket.api.WebSocketPlugInChain;
import java.util.List;

import javolution.util.FastList;

import org.apache.log4j.Logger;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.api.WebSocketEngine;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.api.WebSocketServer;
import org.jwebsocket.kit.CloseReason;
import org.jwebsocket.logging.Logging;

/**
 * Implements the basic chain of plug-ins which is triggered by a server
 * when data packets are received. Each data packet is pushed through the chain
 * and can be processed by the plug-ins.
 * @author aschulze
 */
public class BasePlugInChain implements WebSocketPlugInChain {

	private static Logger mLog = Logging.getLogger(BasePlugInChain.class);
	private List<WebSocketPlugIn> mPlugins = new FastList<WebSocketPlugIn>();
	private WebSocketServer mServer = null;

	/**
	 *
	 * @param aServer
	 */
	public BasePlugInChain(WebSocketServer aServer) {
		mServer = aServer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void engineStarted(WebSocketEngine aEngine) {
		if (mLog.isDebugEnabled()) {
			mLog.debug("Notifying plug-ins of server '" + getServer().getId() + "' that engine '" + aEngine.getId() + "' started...");
		}
		try {
			for (WebSocketPlugIn lPlugIn : getPlugIns()) {
				try {
					lPlugIn.engineStarted(aEngine);
				} catch (Exception ex) {
					mLog.error("Engine '" + aEngine.getId() + "' started (1): " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
				}
			}
		} catch (Exception ex) {
			mLog.error("Engine '" + aEngine.getId() + "' started (2): " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void engineStopped(WebSocketEngine aEngine) {
		if (mLog.isDebugEnabled()) {
			mLog.debug("Notifying plug-ins of server '" + getServer().getId() 
					+ "' that engine '" + aEngine.getId()
					+ "' stopped...");
		}
		try {
			for (WebSocketPlugIn lPlugIn : getPlugIns()) {
				try {
					/*
					if (mLog.isDebugEnabled()) {
						mLog.debug("Notifying plug-in '" 
								+ lPlugIn.getClass().getSimpleName()
								+ "' of server '" + getServer().getId()
								+ "' that engine '" + aEngine.getId()
								+ "' stopped...");
					}
					 */
					lPlugIn.engineStopped(aEngine);
				} catch (Exception ex) {
					mLog.error("Engine '" + aEngine.getId() 
							+ "' stopped (1): " + ex.getClass().getSimpleName()
							+ ": " + ex.getMessage());
				}
			}
		} catch (Exception ex) {
			mLog.error("Engine '" + aEngine.getId() 
					+ "' stopped (2): " + ex.getClass().getSimpleName()
					+ ": " + ex.getMessage());
		}
	}

	/**
	 * @param aConnector
	 */
	@Override
	public void connectorStarted(WebSocketConnector aConnector) {
		if (mLog.isDebugEnabled()) {
			mLog.debug("Notifying plug-ins that connector '" + aConnector.getId() + "' started...");
		}
		try {
			for (WebSocketPlugIn lPlugIn : getPlugIns()) {
				try {
					// log.debug("Notifying plug-in " + plugIn + " that connector started...");
					lPlugIn.connectorStarted(aConnector);
				} catch (Exception ex) {
					mLog.error("Connector '" + aConnector.getId() + "' started (1): " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
				}
			}
		} catch (Exception ex) {
			mLog.error("Connector '" + aConnector.getId() + "' started (2): " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
		}
	}

	/**
	 *
	 * @param aConnector
	 * @return
	 */
	@Override
	public PlugInResponse processPacket(WebSocketConnector aConnector, WebSocketPacket aDataPacket) {
		if (mLog.isDebugEnabled()) {
			mLog.debug("Processing packet for plug-ins on connector '" + aConnector.getId() + "'...");
		}
		PlugInResponse lPluginResponse = new PlugInResponse();
		for (WebSocketPlugIn lPlugIn : getPlugIns()) {
			try {
				/*
				if (log.isDebugEnabled()) {
				log.debug("Processing packet for plug-in " + plugIn + "...");
				}
				 */
				lPlugIn.processPacket(lPluginResponse, aConnector, aDataPacket);
			} catch (Exception ex) {
				mLog.error("Processing packet on connector '" + aConnector.getId() + "': " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
			}
			if (lPluginResponse.isChainAborted()) {
				break;
			}
		}
		return lPluginResponse;
	}

	/**
	 *
	 * @param aConnector
	 * @param aCloseReason
	 */
	@Override
	public void connectorStopped(WebSocketConnector aConnector, CloseReason aCloseReason) {
		if (mLog.isDebugEnabled()) {
			mLog.debug("Notifying plug-ins that connector '" + aConnector.getId() + "' stopped (" + aCloseReason.name() + ")...");
		}
		for (WebSocketPlugIn lPlugIn : getPlugIns()) {
			try {
				// log.debug("Notifying plug-in " + plugIn + " that connector stopped...");
				lPlugIn.connectorStopped(aConnector, aCloseReason);
			} catch (Exception ex) {
				mLog.error("Connector '" + aConnector.getId() + "' stopped: " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
			}
		}
	}

	/**
	 *
	 * @return
	 */
	@Override
	public List<WebSocketPlugIn> getPlugIns() {
		return mPlugins;
	}

	/**
	 *
	 * @param aPlugIn
	 */
	@Override
	public void addPlugIn(WebSocketPlugIn aPlugIn) {
		mPlugins.add(aPlugIn);
		aPlugIn.setPlugInChain(this);
	}

	/**
	 *
	 * @param aPlugIn
	 */
	@Override
	public void removePlugIn(WebSocketPlugIn aPlugIn) {
		mPlugins.remove(aPlugIn);
		aPlugIn.setPlugInChain(null);
	}

	/**
	 * @return the server
	 */
	@Override
	public WebSocketServer getServer() {
		return mServer;
	}
}
