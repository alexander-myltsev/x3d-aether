//	---------------------------------------------------------------------------
//	jWebSocket - Copyright (c) 2010 jwebsocket.org
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
package org.jwebsocket.config.xml;

import java.util.Collections;
import java.util.List;

import org.jwebsocket.config.Config;
import org.jwebsocket.kit.WebSocketRuntimeException;

/**
 * @author puran
 * @version $Id: UserConfig.java 596 2010-06-22 17:09:54Z fivefeetfurther $
 * 
 */
public final class UserConfig implements Config {

	private final String loginname;
	private final String firstname;
	private final String lastname;
	private final String password;
	private final String description;
	private final int status;
	private final List<String> roles;

	/**
	 * Default user config constructor
	 * @param loginname the login name
	 * @param firstname the first name 
	 * @param lastname the last name
	 * @param password the password
	 * @param description the descritpion 
	 * @param status the user status
	 * @param roles the user roles
	 */
	public UserConfig(String loginname, String firstname, String lastname,
			String password, String description, int status, List<String> roles) {
		this.loginname = loginname;
		this.firstname = firstname;
		this.lastname = lastname;
		this.password = password;
		this.description = description;
		this.status = status;
		this.roles = roles;
		//validate user config
		validate();
	}

	/**
	 * @return the loginname
	 */
	public String getLoginname() {
		return loginname;
	}

	/**
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * @return the lastname
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	
	/**
	 * @return the list of roles
	 */
	public List<String> getRoles() {
		return Collections.unmodifiableList(roles);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validate() {
		if ((loginname != null && loginname.length() > 0)
				&& (firstname != null && firstname.length() > 0)
				&& (lastname != null && lastname.length() > 0)
				&& (description != null && description.length() > 0)
				&& (status > 0)) {
			return;
		}
		throw new WebSocketRuntimeException(
				"Missing one of the user configuration, please check your configuration file");
	}
}
