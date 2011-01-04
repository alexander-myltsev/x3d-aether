//	---------------------------------------------------------------------------
//	jWebSocket - Token In- and Outbound Stream
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
package org.jwebsocket.plugins.streaming;

import org.apache.log4j.Logger;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.server.TokenServer;
import org.jwebsocket.token.Token;

/**
 * implements a stream with a queue of token instances. In addition to the
 * <tt>BaseStream</tt> the <tt>TokenStream</tt> also maintains a reference
 * to a <tt>TokenServer</tt> instance. Unlike the <tt>BaseStream</tt> which has
 * no control or limitation regarding the objects in the Queue, the
 * <tt>TokenStream</tt> allow tokens in the queue only. To support the various
 * sub protocols the TokenStream does not send the queued tokens directly to
 * the client but via the <tt>TokenServer</tt>. The <tt>TokenServer</tt> knows
 * about the used sub protocols of the clients and can decide wether to format
 * them as JSON, CSV or XML. Thus application streams usually are descend from
 * <tt>TokenStream</tt>.
 * @author aschulze
 */
public class TokenStream extends BaseStream {

	private static Logger log = Logging.getLogger(TokenStream.class);
	private TokenServer server = null;

	/**
	 * creates a new instance of the TokenStream. In Addition to the
	 * <tt>BaseStream</tt> the <tt>TokenStream</tt> also maintains a reference
	 * to a <tt>TokenServer</tt> instance.
	 * @param aStreamID
	 * @param aServer
	 */
	public TokenStream(String aStreamID, TokenServer aServer) {
		super(aStreamID);
		server = aServer;
	}

	@Override
	protected void processConnector(WebSocketConnector aConnector, Object aObject) {
		try {
			getServer().sendToken(aConnector, (Token) aObject);
		} catch (Exception ex) {
			log.error("(processConnector) " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
		}
	}

	/**
	 * returns the referenced <tt>TokenServer</tt> instance.
	 * @return the server
	 */
	public TokenServer getServer() {
		return server;
	}

}
