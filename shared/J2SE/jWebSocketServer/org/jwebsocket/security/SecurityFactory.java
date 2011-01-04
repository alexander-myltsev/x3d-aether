//	---------------------------------------------------------------------------
//	jWebSocket - Security Factory
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
package org.jwebsocket.security;

import java.util.List;

import org.jwebsocket.config.JWebSocketConfig;
import org.jwebsocket.config.xml.RightConfig;
import org.jwebsocket.config.xml.RoleConfig;
import org.jwebsocket.config.xml.UserConfig;

/**
 * implements the security capabilities of jWebSocket.
 * @author aschulze
 */
public class SecurityFactory {

	// private static Logger log = Logging.getLogger(SecurityFactory.class);
	private static Users users = new Users();
	/**
	 *
	 */
	public static String USER_ANONYMOUS = "guest";
	public static String USER_REG_USER = "user";
	public static String USER_ADMIN = "admin";
	public static String USER_LOCKED = "locked";

	/**
	 * initializes the security system with some default settings to allow to
	 * startup without a config file, this will be removed in the final release!
	 */
	public static void initDefault() {
		/*
		if (log.isDebugEnabled()) {
			log.debug("Initializing demo rights, roles and users...");
		}
		 */
		Rights rights = new Rights();
		// specify rights
		Right lRPC = new Right("org.jWebSocket.plugins.rpc.rpc", "Allow Remote Procedure Calls (RPC) to server");
		Right lRRPC = new Right("org.jWebSocket.plugins.rpc.rrpc", "Allow Reverse Remote Procedure Calls (RRPC) to other clients");
		rights.addRight(lRPC);
		rights.addRight(lRRPC);

		// specify roles and assign rights to roles
		// TODO: needs to be removed in final release!
		Role lGuestRole = new Role("guest", "Guests", lRPC, lRRPC);
		Role lRegRole = new Role("regUser", "Registered Users", lRPC, lRRPC);
		Role lAdminRole = new Role("admin", "Administrators", lRPC, lRRPC);

		// specify role sets for a simpler assignment to the users
		Roles lGuestRoles = new Roles(lGuestRole);
		Roles lRegRoles = new Roles(lGuestRole, lRegRole);
		Roles lAdminRoles = new Roles(lGuestRole, lRegRole, lAdminRole);

		User lGuestUser = new User(USER_ANONYMOUS, "Guest", "Guest", "guest", lGuestRoles);
		User lRegUser = new User(USER_REG_USER, "User", "User", "user", lRegRoles);
		User lAdminUser = new User(USER_ADMIN, "Admin", "Admin", "admin", lAdminRoles);
		// add a locked user for test purposes, e.g. to reject token in system filter
		User lLockedUser = new User(USER_LOCKED, "Locked", "Locked", "locked", lGuestRoles);
		lLockedUser.setStatus(User.ST_LOCKED);

		users.addUser(lGuestUser);
		users.addUser(lRegUser);
		users.addUser(lAdminUser);
		users.addUser(lLockedUser);

		// log.info("Default rights, roles and users initialized.");
	}

	/**
	 * initializes the security system with the settings from the
	 * jWebSocket.xml.
	 * @param aConfig
	 */
	public static void initFromConfig(JWebSocketConfig aConfig) {

		// build list of rights
		List<RightConfig> globalRights = aConfig.getGlobalRights();
		Rights rights = new Rights();
		for (RightConfig lRightConfig : globalRights) {
			Right lRight = new Right(
					lRightConfig.getNamespace() + "." + lRightConfig.getId(),
					lRightConfig.getDescription());
			rights.addRight(lRight);
		}

		// build list of roles
		List<RoleConfig> globalRoles = aConfig.getGlobalRoles();
		Roles roles = new Roles();
		for (RoleConfig lRoleConfig : globalRoles) {
			Rights lRights = new Rights();
			for (String lRightId : lRoleConfig.getRights()) {
				Right lRight = rights.get(lRightId);
				if (lRight != null) {
					lRights.addRight(lRight);
				}
			}
			Role lRole = new Role(
					lRoleConfig.getId(),
					lRoleConfig.getDescription(),
					lRights);
			roles.addRole(lRole);
		}

		// build list of users
		List<UserConfig> globalUsers = aConfig.getUsers();
		for (UserConfig lUserConfig : globalUsers) {
			Roles lRoles = new Roles();
			for (String lRoleId : lUserConfig.getRoles()) {
				Role lRole = roles.getRole(lRoleId);
				if (lRole != null) {
					lRoles.addRole(lRole);
				}
			}
			User lUser = new User(
					lUserConfig.getLoginname(),
					lUserConfig.getFirstname(),
					lUserConfig.getLastname(),
					lUserConfig.getPassword(),
					lRoles);

			users.addUser(lUser);
		}

		// log.info("Rights, roles and users successfully initialized.");
	}

	public static void init() {
		// System.out.println(
		//		"JWEBSOCKET_HOME variable not set, using default configuration...");
		// initialize the security factory with some default demo data
		// to show at least something even with no config
		// TODO: only temporary, will be removed in the final release!
		SecurityFactory.initDefault();
	}

	/**
	 * checks if a user identified by it login name has a certain right.
	 * @param aLoginname
	 * @param aRight
	 * @return
	 */
	public static boolean checkRight(String aLoginname, String aRight) {
		boolean lHasRight = false;
		// if user is not logged in use configured "anonymous" account
		if (aLoginname == null) {
			aLoginname = SecurityFactory.USER_ANONYMOUS;
		}
		User lUser = users.getUserByLoginName(aLoginname);
		// if the user is not found use the "anonymous" account
		// TODO: this process needs to be changed in the final release!
		if (lUser == null && !SecurityFactory.USER_ANONYMOUS.equals(aLoginname)) {
			aLoginname = SecurityFactory.USER_ANONYMOUS;
			lUser = users.getUserByLoginName(aLoginname);
		}
		if (lUser != null) {
			return lUser.hasRight(aRight);
		}
		return lHasRight;
	}
}
