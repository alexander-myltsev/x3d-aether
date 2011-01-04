/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jwebsocket.appserver;

import javax.servlet.http.HttpSession;
import javolution.util.FastMap;
import org.jwebsocket.kit.WebSocketSession;

/**
 * This class combines both the sessions of the servlet container
 * and the websocket engine.
 * @author aschulze
 */
public class WebSocketHttpSessionMerger {

	private static FastMap<String, HttpSession> httpSessions = new FastMap<String, HttpSession>();
	private static FastMap<String, WebSocketSession> wsSessions = new FastMap<String, WebSocketSession>();
	private static FastMap<String, String> assignments = new FastMap<String, String>();

	public static void addHttpSession(HttpSession aServletSession) {
		httpSessions.put(aServletSession.getId(), aServletSession);
	}

	public static void addWebSocketSession(WebSocketSession aWebSocketSession) {
		wsSessions.put(aWebSocketSession.getSessionId(), aWebSocketSession);
	}

	public static void removeHttpSession(HttpSession aServletSession) {
		httpSessions.remove(aServletSession.getId());
	}

	public static void removeWebSocketSession(WebSocketSession aWebSocketSession) {
		wsSessions.remove(aWebSocketSession.getSessionId());
	}

	public static String getHttpSessionsCSV() {
		String lRes = "";
		for (HttpSession lSession : httpSessions.values()) {
			lRes += lSession.getId() + ",";
		}
		if (lRes.length() > 0) {
			lRes = lRes.substring(0, lRes.length() - 1);
		}
		return lRes;
	}

	public static String getWebSocketSessionsCSV() {
		String lRes = "";
		for (WebSocketSession lSession : wsSessions.values()) {
			lRes += lSession.getSessionId() + ",";
		}
		if (lRes.length() > 0) {
			lRes = lRes.substring(0, lRes.length() - 1);
		}
		return lRes;
	}
}
