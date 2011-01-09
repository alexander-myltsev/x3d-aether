package org.jwebsocket.plugins.streaming;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.server.TokenServer;
import org.jwebsocket.token.Token;

public class AetherStream extends TokenStream {
	/*
	public static void main(String[] args) {
		System.out.println("Hello. From AetherStream");
	}
	*/
	
	private static Logger log = Logging.getLogger(AetherStream.class);
	private Boolean isRunning = false;
	private AetherProcess aetherProcess = null;
	private Thread aetherThread = null;

	/**
	 *
	 *
	 * @param aStreamID
	 * @param aServer
	 */
	public AetherStream(String aStreamID, TokenServer aServer) {
		super(aStreamID, aServer);
		startStream(-1);
	}

	/**
	 *
	 */
	@Override
	public void startStream(long aTimeout) {
		super.startStream(aTimeout);

		if (log.isDebugEnabled()) {
			log.debug("Starting Aether stream...");
		}
		aetherProcess = new AetherProcess();
		aetherThread = new Thread(aetherProcess);
		aetherThread.start();
	}

	/**
	 *
	 */
	@Override
	public void stopStream(long aTimeout) {
		if (log.isDebugEnabled()) {
			log.debug("Stopping Aether stream...");
		}
		long lStarted = new Date().getTime();
		isRunning = false;
		try {
			aetherThread.join(aTimeout);
		} catch (Exception ex) {
			log.error(ex.getClass().getSimpleName() + ": " + ex.getMessage());
		}
		if (log.isDebugEnabled()) {
			long lDuration = new Date().getTime() - lStarted;
			if (aetherThread.isAlive()) {
				log.warn("aether stream did not stopped after " + lDuration + "ms.");
			} else {
				log.debug("aether stream stopped after " + lDuration + "ms.");
			}
		}

		super.stopStream(aTimeout);
	}

	private class AetherProcess implements Runnable {

		@Override
		public void run() {
			if (log.isDebugEnabled()) {
				log.debug("Running Aether stream...");
			}
			isRunning = true;
			float pos = -3.0f, delta = 0.03f;
			org.aether.x3d.SceneGenerator sceneGenerator = new org.aether.x3d.SceneGenerator(new Date().getTime()); 
			while (isRunning) {
				try {
					Thread.sleep(50);

					Token lToken = new Token("event");
					lToken.put("name", "stream");
					lToken.put("msg", new Date().getTime());
					lToken.put("scene", sceneGenerator.getScene(new Date().getTime()));
					lToken.put("pos", pos);
					pos += delta;
					if (pos > 3.0f || pos < -3.0f) delta = -delta;				
					
					put(lToken);
				} catch (InterruptedException ex) {
					log.error("(run) " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
				}
			}
			if (log.isDebugEnabled()) {
				log.debug("Aether stream stopped.");
			}
		}
	}
}