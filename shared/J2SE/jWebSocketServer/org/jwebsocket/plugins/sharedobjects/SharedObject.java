//	---------------------------------------------------------------------------
//	jWebSocket - Shared Object
//	(C) 2010 jWebSocket.org, Alexander Schulze, Innotrade GmbH
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

package org.jwebsocket.plugins.sharedobjects;

import java.util.Date;

/**
 *
 * @author aschulze
 */
public class SharedObject {

	public static int MODE_READ_WRITE = 0;
	public static int MODE_READ_ONLY = 1;

	public static int LOCK_STATE_RELEASED = 0;
	public static int LOCK_STATE_LOCKED = 1;

	private Object object = null;
	private int mode = 0; // see above
	private int lockstate = 0; // see above
	private Date lock_timestamp = null;

	/**
	 * @return the object
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * @param object the object to set
	 */
	public void setObject(Object object) {
		this.object = object;
	}

	/**
	 * @return the mode
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}

	/**
	 * @return the lockstate
	 */
	public int getLockstate() {
		return lockstate;
	}

	/**
	 * @param lockstate the lockstate to set
	 */
	public void setLockstate(int lockstate) {
		this.lockstate = lockstate;
	}

	/**
	 * @return the lock_timestamp
	 */
	public Date getLock_timestamp() {
		return lock_timestamp;
	}

	/**
	 * @param lock_timestamp the lock_timestamp to set
	 */
	public void setLock_timestamp(Date lock_timestamp) {
		this.lock_timestamp = lock_timestamp;
	}

}
