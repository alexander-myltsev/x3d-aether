//	---------------------------------------------------------------------------
//	jWebSocket - Demo how to communicate between Servlets and WebSockets
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
package org.jwebsocket.appserver;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javolution.util.FastMap;
import org.jwebsocket.server.TokenServer;
import org.jwebsocket.token.Token;

/**
 * demonstrates how to communicate between servlets and web sockets.
 * @author aschulze
 */
public class ServletBridge extends HttpServlet {

	// reference to the token server
	private static TokenServer server = null;

	/**
	 * @return the server
	 */
	public static TokenServer getServer() {
		return server;
	}

	/**
	 *
	 * @param aServer
	 */
	public static void setServer(TokenServer aServer) {
		server = aServer;
	}

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/plain;charset=UTF-8");
		PrintWriter out = response.getWriter();

		try {
			if (server != null) {
				// convert request arguments to token
				FastMap<String, String[]> lParms = new FastMap(request.getParameterMap());
				Token lToken = new Token();
				for (String lParm : lParms.keySet()) {
					String[] lValues = lParms.get(lParm);
					if (lValues != null && lValues.length > 0) {
						lToken.put(lParm, lValues[0]);
					}
				}
				ServletConnector lConn = new ServletConnector(request, response);
				server.getPlugInChain().processToken(lConn, lToken);
				out.println(lConn.getPlainResponse());
			} else {
				out.println("ERROR:\nNo WebSocketServer assigned to Servlet!");
			}
		} finally {
			out.close();
		}
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>
}
