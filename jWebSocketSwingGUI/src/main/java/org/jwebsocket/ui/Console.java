//	---------------------------------------------------------------------------
//	jWebSocket - Copyright (c) 2010 Innotrade GmbH
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
package org.jwebsocket.ui;

import org.jwebsocket.api.WebSocketClientEvent;
import org.jwebsocket.api.WebSocketClientTokenListener;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.client.token.BaseTokenClient;
import org.jwebsocket.token.Token;

/**
 *
 * @author aschulze
 */
public class Console implements WebSocketClientTokenListener {

	private BaseTokenClient mClient = null;
	
	public Console() {
		try {
			mClient = new BaseTokenClient();
			mClient.addListener(this);
		} catch (Exception ex) {
			System.out.println(ex.getClass().getSimpleName() + ": " + ex.getMessage());
		}
	}

	@Override
	public void processOpened(WebSocketClientEvent aEvent) {
	}

	@Override
	public void processPacket(WebSocketClientEvent aEvent, WebSocketPacket aPacket) {
	}

	@Override
	public void processToken(WebSocketClientEvent aEvent, Token aToken) {
	}

	@Override
	public void processClosed(WebSocketClientEvent aEvent) {
	}

	public static void main(String args[]) {


	}

}
