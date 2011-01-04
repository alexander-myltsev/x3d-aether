//	---------------------------------------------------------------------------
//	jWebSocket - Shared Logging Support
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
package org.jwebsocket.logging;

import java.io.IOException;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.jwebsocket.config.LoggingConfig;

/**
 * Provides the common used jWebSocket logging support based on
 * Apache's log4j.
 * @author aschulze
 */
public class Logging {

	private static PatternLayout layout = null;
	private static Appender appender = null;
	private static Level logLevel = Level.DEBUG;
	private static String[] searchPaths = null;
	/**
	 * Log output is send to the console (stdout).
	 */
	public final static int CONSOLE = 0;
	/**
	 * Log output is send to a rolling file.
	 */
	public final static int ROLLING_FILE = 1;
	/**
	 * Log output is send to a single file.
	 */
	public final static int SINGLE_FILE = 2;
	/**
	 * Name of jWebSocket log file.
	 */
	private static String filename = "jWebSocket.log";
	/**
	 * Pattern for jWebSocket log file.
	 */
	private static String pattern = "%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p - %C{1}: %m%n";
	/**
	 * Buffersize if write cache for logs is activated (recommended)
	 * Buffersize = 0 means no write cache.
	 */
	private static int buffersize = 8096; // 8K is log4j default
	private static int logTarget = CONSOLE; // ROLLING_FILE;

	private static String getLogsFolderPath(String fileName, String[] aPaths) {

		// try to obtain JWEBSOCKET_HOME environment variable
		String lWebSocketHome = System.getenv("JWEBSOCKET_HOME");
		String lFileSep = System.getProperty("file.separator");
		String lWebSocketLogs = null;

		if (lWebSocketHome != null) {
			// append trailing slash if needed
			if (!lWebSocketHome.endsWith(lFileSep)) {
				lWebSocketHome += lFileSep;
			}
			// logs are located in %JWEBSOCKET_HOME%/logs
			lWebSocketLogs = lWebSocketHome + "logs" + lFileSep + fileName;
		}

		if (lWebSocketLogs == null) {
			// try to obtain CATALINA_HOME environment variable
			lWebSocketHome = System.getenv("CATALINA_HOME");
			if (lWebSocketHome != null) {
				// append trailing slash if needed
				if (!lWebSocketHome.endsWith(lFileSep)) {
					lWebSocketHome += lFileSep;
				}
				// logs are located in %CATALINA_HOME%/logs
				lWebSocketLogs = lWebSocketHome + "logs" + lFileSep + fileName;
			}
		}

		return lWebSocketLogs;
	}

	// TODO: Load the conversion pattern and the logging target from a configuration file (e.g. jWebSocket.xml)
	/**
	 * Initializes the Apache log4j system to produce the desired logging
	 * output.
	 * @param aLogLevel one of the values TRACE, DEBUG, INFO, WARN, ERROR or FATAL.
	 *
	 */
	private static void checkLogAppender() {
		if (layout == null) {
			layout = new PatternLayout();
			layout.setConversionPattern(pattern);
		}
		if (appender == null) {
			String logsPath = getLogsFolderPath(filename, searchPaths);
			if (ROLLING_FILE == logTarget && logsPath != null) {
				try {
					RollingFileAppender lRFA = new RollingFileAppender(layout,
							logsPath, true /* append, don't truncate */);
					lRFA.setBufferedIO(buffersize > 0);
					lRFA.setImmediateFlush(true);
					if (buffersize > 0) {
						lRFA.setBufferSize(buffersize);
					}
					lRFA.setEncoding("UTF-8");
					appender = lRFA;
				} catch (IOException ex) {
					appender = new ConsoleAppender(layout);
				}
			} else if (SINGLE_FILE == logTarget && logsPath != null) {
				try {
					FileAppender lFA = new FileAppender(layout, logsPath,
							true /* append, don't truncate */);
					lFA.setBufferedIO(buffersize > 0);
					lFA.setImmediateFlush(true);
					if (buffersize > 0) {
						lFA.setBufferSize(buffersize);
					}
					lFA.setEncoding("UTF-8");
					appender = lFA;
				} catch (IOException ex) {
					appender = new ConsoleAppender(layout);
				}
			} else {
				appender = new ConsoleAppender(layout);
				if (CONSOLE != logTarget) {
					System.out.println("JWEBSOCKET_HOME"
							+ " variable not set or invalid configuration,"
							+ " using console output for log file.");
				}
			}
		}

	}

	/**
	 * Initializes the jWebSocket logging system with the given log level.
	 * All subsequently instantiated class specific loggers will use this
	 * setting.
	 * @param aLogLevel
	 */
	public static void initLogs(String aLogLevel, String aLogTarget,
			String aFilename, String aPattern, Integer aBuffersize,
			String[] aSearchPaths) {
		searchPaths = aSearchPaths;
		if (aLogLevel != null) {
			logLevel = Level.toLevel(aLogLevel);
		}
		if (aLogTarget != null) {
			if ("console".equals(aLogTarget)) {
				logTarget = Logging.CONSOLE;
			} else if ("singlefile".equals(aLogTarget)) {
				logTarget = Logging.SINGLE_FILE;
			} else if ("rollingfile".equals(aLogTarget)) {
				logTarget = Logging.ROLLING_FILE;
			}
		}
		if (aFilename != null) {
			filename = aFilename;
		}
		if (aPattern != null) {
			pattern = aPattern;
		}
		if (aBuffersize != null) {
			buffersize = aBuffersize;
		}
		checkLogAppender();
	}

	public static void initLogs(LoggingConfig aLoggingConfig, String[] aSearchPaths) {
		if (aLoggingConfig != null) {
			initLogs(
					aLoggingConfig.getLevel(),
					aLoggingConfig.getAppender(),
					aLoggingConfig.getFilename(),
					aLoggingConfig.getPattern(),
					aLoggingConfig.getBufferSize(),
					aSearchPaths);
		}
	}

	/**
	 * closes the log file. Take care that no further lines are appended
	 * to the logs after it has been closed!
	 */
	public static void exitLogs() {
		if (appender != null) {
			// System.out.println("Closing logs...");
			// properly close log files if such
			appender.close();
			// System.out.println("Logs closed.");
		}
	}

	/**
	 * Returns a logger for a certain class by using the jWebSocket settings
	 * for logging and ignoring inherited log4j settings.
	 * @param aClass
	 * @return Logger the new logger for the given class.
	 */
	public static Logger getLogger(Class aClass) {
		checkLogAppender();
		Logger logger = Logger.getLogger(aClass);
		logger.addAppender(appender);
		// don't inherit global log4j settings, we intend to configure that
		// in our own jWebSocket.xml config file.
		logger.setAdditivity(false);
		logger.setLevel(logLevel);
		return logger;
	}
}
