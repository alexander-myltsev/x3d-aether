//	---------------------------------------------------------------------------
//	jWebSocket - Role Class
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
package org.jwebsocket.security;

/**
 * implements a role which contains a set of rights.
 * @author aschulze
 */
public class Role {

	private String key = null;
	private String description = null;
	private Rights rights = new Rights();

	/**
	 * creates a new empty Role object.
	 */
	public Role() {
	}

	/**
	 * creates a new Role object and initializes its key and description.
	 * @param aKey
	 * @param aDescription
	 */
	public Role(String aKey, String aDescription) {
		key = aKey;
		description = aDescription;
	}

	/**
	 * creates a new Role object and initializes its key, description and
	 * rights.
	 * @param aKey
	 * @param aDescription
	 * @param aRights
	 */
	public Role(String aKey, String aDescription, Right... aRights) {
		key = aKey;
		description = aDescription;
		if (aRights != null) {
			for (int i = 0; i < aRights.length; i++) {
				addRight(aRights[i]);
			}
		}
	}

	/**
	 * creates a new Role object and initializes its key, description and
	 * rights.
	 * @param aKey
	 * @param aDescription
	 * @param aRights
	 */
	public Role(String aKey, String aDescription, Rights aRights) {
		key = aKey;
		description = aDescription;
		rights = aRights;
	}

	/**
	 *
	 * @return
	 */
	public String getKey() {
		return key;
	}

	/**
	 *
	 * @param key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 *
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 *
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 *
	 * @param aRight
	 */
	public void addRight(Right aRight) {
		rights.addRight(aRight);
	}

	/**
	 *
	 * @param aRight
	 * @return
	 */
	public boolean hasRight(Right aRight) {
		return rights.hasRight(aRight);
	}

	/**
	 *
	 * @param aRight
	 * @return
	 */
	public boolean hasRight(String aRight) {
		return rights.hasRight(aRight);
	}
}
