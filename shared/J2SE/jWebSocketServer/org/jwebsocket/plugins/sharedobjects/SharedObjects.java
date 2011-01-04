//	---------------------------------------------------------------------------
//	jWebSocket - Shared Objects
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
package org.jwebsocket.plugins.sharedobjects;

import java.util.Set;
import javolution.util.FastMap;
import org.apache.log4j.Logger;
import org.jwebsocket.logging.Logging;

/**
 *
 * @author aschulze
 */
public class SharedObjects {

	private static Logger log = Logging.getLogger(SharedObjects.class);
	private FastMap<String, Object> objects = new FastMap<String, Object>();

	public Object put(String aKey, Object aObject) {
		return objects.put(aKey, aObject);
	}

	public Object remove(String aKey) {
		return objects.remove(aKey);
	}

	public Object get(String aKey) {
		return objects.get(aKey);
	}

	public boolean contains(String aKey) {
		return objects.containsKey(aKey);
	}

	public Set<String>getKeys() {
		return objects.keySet();
	}

}
