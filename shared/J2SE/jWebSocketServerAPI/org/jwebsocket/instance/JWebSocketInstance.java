/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jwebsocket.instance;

/**
 *
 * @author aschulze
 */
public class JWebSocketInstance {

	/**
	 *
	 */
	public final static int STOPPED = 0;
	/**
	 *
	 */
	public final static int STARTING = 1;
	/**
	 *
	 */
	public final static int STARTED = 2;
	/**
	 *
	 */
	public final static int STOPPING = 3;
	/**
	 *
	 */
	public final static int SHUTTING_DOWN = 4;

	private static volatile int mStatus = STOPPED;

	/**
	 * @return the mStatus
	 */
	public static int getStatus() {
		return mStatus;
	}

	/**
	 *
	 * @param aStatus
	 */
	public static void setStatus(int aStatus) {
		mStatus = aStatus;
	}

}
