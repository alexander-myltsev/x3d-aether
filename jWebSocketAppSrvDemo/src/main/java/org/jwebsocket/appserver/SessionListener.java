/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jwebsocket.appserver;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.apache.log4j.Logger;
import org.jwebsocket.logging.Logging;

/**
 * Web application lifecycle listener.
 * Here the http session is added or removed respectively from the
 * global WebSocketHttpSessionMerger.
 * @author aschulze
 */
public class SessionListener implements HttpSessionListener {

	private static Logger mLog = null;

	private void checkLogs() {
		if (mLog == null) {
			mLog = Logging.getLogger(SessionListener.class);
		}
	}

	@Override
	public void sessionCreated(HttpSessionEvent hse) {
		// when a new session is created by the servlet engine
		// add this session to the global WebSockethttpSessionMerger.
		WebSocketHttpSessionMerger.addHttpSession(hse.getSession());
		checkLogs();
		mLog.info("Created Http session: '" + hse.getSession().getId() + "'");
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent hse) {
		// when an existing session is destroyed by the servlet engine
		// remove this session from the global WebSockethttpSessionMerger.
		WebSocketHttpSessionMerger.removeHttpSession(hse.getSession());
		checkLogs();
		mLog.info("Destroyed Http session: '" + hse.getSession().getId() + "'");
	}
}
