//	---------------------------------------------------------------------------
//	jWebSocket - Right Class
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
 * implements a right as part of a FastMap of rights for a certain role.
 * @author aschulze
 */
public class Right {

	private String key = null;
	private String description = null;

	/**
	 * creates a new default right with a key and a description.
	 * @param aKey
	 * @param aDescription
	 */
	public Right(String aKey, String aDescription) {
		key = aKey;
		description = aDescription;
	}

	/**
	 * returns the key of the right. The key is the unique identifier of the
	 * right and should contain the entire name space 
	 * e.g. <tt>org.jwebsocket.plugins.chat.broadcast</tt>.
	 * The key is case-sensitve.
	 * @return
	 */
	public String getKey() {
		return key;
	}

	/**
	 * specifies the key of the right. The key is the unique identifier of the
	 * right and should contain the entire name space
	 * e.g. <tt>org.jwebsocket.plugins.chat.broadcast</tt>.
	 * The key is case-sensitve.
	 * @param key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * returns the description of the right.
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * specifies the description of the right.
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}
