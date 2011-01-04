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

/**
 *
 * @author aschulze
 */
public class JWebSocketEmbedded {

	public static void main(String[] args) {
		// instantiate an embedded jWebSocket Server
		JWebSocketSubSystemSample jWebSocketSubsystem = new JWebSocketSubSystemSample();
		// instantiate an embedded listener class and add it to the subsystem
		jWebSocketSubsystem.addListener(new JWebSocketTokenListenerSample());

		// start the subsystem
		jWebSocketSubsystem.start();
		// wait until shutdown requested
		jWebSocketSubsystem.run();
		// stop the subsystem
		jWebSocketSubsystem.stop();
	}
}
