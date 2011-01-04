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

import org.jwebsocket.token.Token;

/**
 * Interface for shared objects in the SharedObjects Plug-In of jWebSocket
 * @author aschulze
 */
public interface ISharedObject {

	/**
	 * initializes the object with its default data. The initialization is 
	 * intentionally done in a separate method and not directly in the 
	 * constructor to allow a "reset-to-default" if desired. Hence, you 
	 * shouldn't put any instantiation code here but initialization code 
	 * only. This method is called by the createObject method of the 
	 * SharedObjects plug-in. The userId specifies the owner of the 
	 * sharedObject, this can be either a certain client or the server. 
	 * The owner as well as the server have special access rights to the object.	 
	 * @param aUserId
	 */
	void init(String aUserId);

	/**
	 * returns the simple class name of the Shared Object instance. It is not
	 * supported to hold multiple Shared Objects with the same id on the server,
	 * even if they have different classes.
	 * @return
	 */
	String getClassName();

	/**
	 * returns a token with the entire object or the requested part of it in
	 * its data field. This method is called by the readObject method of the
	 * SharedObject plug-in.
	 * @param aSubId
	 * @return
	 */
	Token read(String aSubId);

	/**
	 * updates the entire object or the requested part of it utilizing the data
	 * field of the passed token. This method is called by the writeObject
	 * method of the SharedObject plug-in.
	 * @param aSubId
	 * @param aToken
	 */
	void write(String aSubId, Token aToken);

	/**
	 * allows to call a certain method of the Token and to return a token with
	 * its result values if such. The token specifies all details (method:
	 * name of the method to call, args: array of arguments to the method as
	 * array of objects with the following fields {type: type of arguments,
	 * value: value of argument}).
	 * This is the a powerful function to implement arbitrary additional
	 * proprietary functionality for particular Shared Objects and provide
	 * them via the API to the client.
	 * @param aToken
	 */
	Token invoke(Token aToken);

	/**
	 * cleans up the object before it is destroyed. This method is called by
	 * the destroyObject method of the SharedObjects plug-in.
	 */
	void cleanup();

	/**
	 * locks the object for a certain user, nobody else than this user can
	 * access the object after that neither read nor write. If other clients
	 * try to access a locked object an corresponding error token is returned.
	 * The object gets automatically unlocked when the user logs off. The
	 * jWebSocket library provides the SecurityFactory class to maintain the
	 * locks.
	 * @param aSubId
	 * @param aUserId
	 */
	void lock(String aSubId, String aUserId);

	/**
	 * unlocks the object for a certain user. The user name must match the name
	 * which was passed to lock the object. Releases the lock for the object so
	 * that other user can access the object again according to their access
	 * rights.  The jWebSocket library provides the SecurityFactory class to
	 * maintain the locks. During the lock phase to update broadcasts should be
	 * sent to keep the clients consistent. However, if changes have been
	 * applied during the lock phase the object should broadcast these changes
	 * in its unlock method.
	 * @param aSubId
	 * @param aUserId
	 */
	void unlock(String aSubId, String aUserId);

	/**
	 * grants a certain right a certain or to all users to access the object
	 * (read, write, init, destroy). The jWebSocket library provides the
	 * SecurityFactory class to maintain the access rights to the objects.
	 * @param aSubId
	 * @param aUserId
	 * @param aRight
	 */
	void grant(String aSubId, String aUserId, int aRight);

	/**
	 * revokes a certain right from a certain or from all users to access the
	 * object (read, write, init, destroy). The jWebSocket library provides the
	 * SecurityFactory class to maintain the access rights to the objects.
	 * @param aSubId
	 * @param aUserId
	 * @param aRight
	 */
	void revoke(String aSubId, String aUserId, int aRight);

	/**
	 * registers a certain client at the broadcast list for this object, so
	 * that the client gets notified on changes to this object, provided that
	 * he has read access.
	 * @param aUserId
	 * @param aClientId
	 */
	void registerClient(String aUserId, String aClientId);

	/**
	 * unregisters a certain client from the broadcast list for this object,
	 * so that the client gets not notified on changes to this object anymore.
	 * @param aUserId
	 * @param aClientId
	 */
	void unregisterClient(String aUserId, String aClientId);
}
