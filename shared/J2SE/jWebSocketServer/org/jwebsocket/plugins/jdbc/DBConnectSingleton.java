//	---------------------------------------------------------------------------
//	five-feet-further RIAServer-Framework - DBConnectSingleton.java
//	Copyright (c) 2003-2009 Alexander Schulze, Innotrade GmbH
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
//	Dieses Programm ist freie Software. Sie können es unter den Bedingungen der
//	GNU Lesser General Public License, wie von der Free Software Foundation
//	veröffentlicht, weitergeben und/oder modifizieren, entweder gemäß Version 3
//	der Lizenz oder (nach Ihrer Option) jeder späteren Version.
//	Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, daß es Ihnen
//	von Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE, sogar ohne die
//	implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN
//	BESTIMMTEN ZWECK. Details finden Sie in der GNU Lesser General Public License.
//	Sie sollten ein Exemplar der GNU Lesser General Public License zusammen mit diesem
//	Programm erhalten haben. Falls nicht, siehe <http://www.gnu.org/licenses/lgpl.html>.
//	---------------------------------------------------------------------------
package org.jwebsocket.plugins.jdbc;

import org.jwebsocket.logging.Logging;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import javolution.util.FastMap;
import org.apache.log4j.Logger;

/**
 * Database connection maintenance.
 * This class handles the different connections to one or more databases.
 * Here also different users with different rights are handled.
 * @since 1.0
 * @author Alexander Schulze
 */
public class DBConnectSingleton {

	private static Logger log = Logging.getLogger(DBConnectSingleton.class);
	private static Map connections = new FastMap();
	public static String lastRecentException = null;
	/**
	 * Name of the database user for system access
	 * @since 1.0
	 */
	public final static String USR_SYSTEM = "SYS";
	/**
	 * Name of the database user for usual application access
	 * @since 1.0
	 */
	public final static String USR_APPLICATION = "APP";
	/**
	 * Name of the database user for demo access only
	 * @since 1.0
	 */
	public final static String USR_DEMO = "DEMO";

	// TODO: use plug-in settings for DB access here!

	// DB_DRIVER
	public static String DB_DRIVER = "com.mysql.jdbc.Driver";
	public static String DB_URL = "jdbc:mysql://localhost:3306/ria-db";
	// USR_SYSTEM
	public static String DB_SYS_USER_ID = "fffSys";
	public static String DB_SYS_USER_PW = "sys_password";
	// USR_DEMO
	public static String DB_DEMO_USER_ID = "fffDemo";
	public static String DB_DEMO_USER_PW = "demo_password";
	// USR_APPLICATION
	public static String DB_APP_USER_ID = "fffApp";
	public static String DB_APP_USER_PW = "app_password";

	/**
	 * Returns a connection for the given database user. If a connection not
	 * already has been established or is broken a new one is created.
	 * @param aUsr Name of the database user
	 * @since 1.0
	 */
	private static Connection getConnection(String aUsr, Boolean aAutoConnect) {

		String lUsername;
		String lPassword;

		if (aUsr.equals(USR_SYSTEM)) {
			lUsername = DB_SYS_USER_ID;
			lPassword = DB_SYS_USER_PW;
		} else if (aUsr.equals(USR_DEMO)) {
			lUsername = DB_DEMO_USER_ID;
			lPassword = DB_DEMO_USER_PW;
		} else if (aUsr.equals(USR_APPLICATION)) {
			lUsername = DB_APP_USER_ID;
			lPassword = DB_APP_USER_PW;
		} else {
			return null;
		}

		Connection lConnection = (Connection) connections.get(aUsr);

		if (aAutoConnect) {
			try {
				// Prüfen, ob eine Verbindung besteht, und ggf. neu aufbauen.
				if (lConnection == null || lConnection.isClosed()) {
					try {
						if (log.isDebugEnabled()) {
							log.debug("Verbindungsaufbau (" + aUsr + ") zur Datenbank...");
						}
						Class.forName(DB_DRIVER);
						lConnection = DriverManager.getConnection(
								DB_URL,
								lUsername,
								lPassword);
						if (log.isInfoEnabled()) {
							log.info("Verbindungsaufbau (" + aUsr + ") erfolgreich.");
						}
						connections.put(aUsr, lConnection);
						return lConnection;
					} catch (SQLException ex) {
						lastRecentException =
								ex.getClass().getName() + " "
								+ "bei Verbindungsaufbau (" + aUsr + "), "
								+ "Details: " + ex.getSQLState();
						log.error(lastRecentException);
					}
				}
			} catch (Exception ex) {
				lastRecentException =
						ex.getClass().getName() + " "
						+ "bei Connect zur Datenbank: " + ex.getMessage();
				log.error(lastRecentException);
			}
		}

		return lConnection;
	}

	public static Connection checkConnection(String aUsr) {
		return getConnection(aUsr, false);
	}

	public static Connection getConnection(String aUsr) {
		return getConnection(aUsr, true);
	}

	/**
	 * Closes the connection for the given database user. If a connection not
	 * already has been established or is broken nothing happens.
	 * @param aUsr Name of the database user
	 * @since 1.0
	 */
	public static void closeConnection(String aUsr) {

		try {
			if (log.isDebugEnabled()) {
				log.debug("Verbindung zur Datenbank (" + aUsr + ") wird getrennt...");
			}
			Connection lConnection = (Connection) connections.get(aUsr);

			if (lConnection != null && !lConnection.isClosed()) {
				lConnection.close();
				if (log.isInfoEnabled()) {
					log.info("Datenbankverbindung (" + aUsr + ") erfolgreich getrennt.");
				}
			} else {
				if (log.isInfoEnabled()) {
					log.info("Es besteht keine Verbindung (" + aUsr + ") zur Datenbank.");
				}
			}
		} catch (SQLException ex) {
			lastRecentException =
					ex.getClass().getName() + " "
					+ "bei Disconnect von Datenbank (" + aUsr + "): " + ex.getMessage();
			log.error(lastRecentException);
		}
	}

	/**
	 * Performs a update command for the given database user.
	 * @param aUsr Name of the database user
	 * @param aSQL SQL statement to be performed on the database.
	 * @since 1.0
	 */
	public static int execSQL(String aUsr, String aSQL) throws Exception {
		Connection lConn = DBConnectSingleton.getConnection(aUsr);
		if (lConn != null) {
			Statement sql = lConn.createStatement();
			return sql.executeUpdate(aSQL);
		}
		return -1;
	}

	/**
	 * Performs a prepared update command with optional arguments 
	 * for the given database user.
	 * @param aUsr Name of the database user
	 * @param aSQL SQL statement to be performed on the database.
	 * @param aParms Array of additional arguments to the prepared statement.
	 * @since 1.0
	 */
	public static int execSQL(String aUsr, String aSQL, Object[] aParms)
			throws SQLException {
		Connection lConn = DBConnectSingleton.getConnection(aUsr);
		if (lConn != null) {
			PreparedStatement sql = lConn.prepareStatement(aSQL);
			int lParmIdx = 0;
			while (lParmIdx < aParms.length) {
				Object lParm = aParms[lParmIdx];
				// Die Zählung der Parameter beginnt mit 1, nicht mit 0!
				lParmIdx++;
				sql.setObject(lParmIdx, lParm);
			}
			return sql.executeUpdate();
		}
		return -1;
	}

	/**
	 * Performs a query command for the given database user.
	 * @param aUsr Name of the database user
	 * @param aSQL SQL statement to be performed on the database.
	 * @since 1.0
	 */
	public static DBQueryResult querySQL(String aUsr, String aSQL) throws Exception {
		Connection lConn = DBConnectSingleton.getConnection(aUsr);
		DBQueryResult lQRes = new DBQueryResult();
		if (lConn != null) {
			lQRes.sql = lConn.createStatement();
			lQRes.resultSet = lQRes.sql.executeQuery(aSQL);
			lQRes.metaData = lQRes.resultSet.getMetaData();
		}
		return lQRes;
	}

	public static void closeQuery(DBQueryResult aQueryRes) {
		try {
			if (aQueryRes != null
					&& aQueryRes.sql != null) {
				aQueryRes.sql.close();
			}
		} catch (Exception ex) {
			log.error(
					ex.getClass().getName()
					+ " beim Schließen einer Query : " + ex.getMessage());
		}
	}
}
