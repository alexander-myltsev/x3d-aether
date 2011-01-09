//	---------------------------------------------------------------------------
//	jWebSocket - Copyright (c) 2010 jwebsocket.org
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
package org.jwebsocket.console;

import org.jwebsocket.config.JWebSocketCommonConstants;
import org.jwebsocket.config.JWebSocketServerConstants;
import org.jwebsocket.factory.JWebSocketFactory;
import org.jwebsocket.instance.JWebSocketInstance;
import org.jwebsocket.server.CustomServer;
import org.jwebsocket.server.TokenServer;

/**
 * @author puran
 * @version $Id: JWebSocketServer.java 443 2010-05-06 12:03:08Z fivefeetfurther $
 *
 */
public class JWebSocketServer {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		// the following 3 lines may not be removed due to GNU LGPL 3.0 license!
		System.out.println("jWebSocket Ver. " + JWebSocketServerConstants.VERSION_STR + " (" + System.getProperty("sun.arch.data.model") + "bit)");
		System.out.println(JWebSocketCommonConstants.COPYRIGHT);
		System.out.println(JWebSocketCommonConstants.LICENSE);
		System.out.println("Log files per default in jWebSocket.log if not overwritten in jWebSocket.xml.");

		JWebSocketFactory.start();

		// get the token server
		TokenServer lTS0 = (TokenServer) JWebSocketFactory.getServer("ts0");
		if (lTS0 != null) {
			// and add the sample listener to the server's listener chain
			lTS0.addListener(new JWebSocketTokenListenerSample());
		}

		// get the custom server
		CustomServer lCS0 = (CustomServer) JWebSocketFactory.getServer("cs0");
		if (lCS0 != null) {
			// and add the sample listener to the server's listener chain
			lCS0.addListener(new JWebSocketCustomListenerSample());
		}

		// remain here until shut down request
		while (JWebSocketInstance.getStatus() != JWebSocketInstance.SHUTTING_DOWN) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException ex) {
				// no handling required here
			}
		}

		JWebSocketFactory.stop();
	}
}
