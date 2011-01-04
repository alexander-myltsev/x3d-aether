//	---------------------------------------------------------------------------
//	jWebSocket - SharedLists
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

import java.util.List;
import java.util.Map;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.jwebsocket.token.Token;

/**
 *
 * @author aschulze
 */
public class SharedLists {

	private Map<String, FastList> mLists = new FastMap<String, FastList>();

	public void create(Token aResponse, String aId) {
		FastList lList = mLists.get(aId);
		if (lList != null) {
			mLists.put(aId, new FastList());
		}
	}

	public void clear(Token aResponse, String aId) {
		List lList = mLists.get(aId);
		if (lList != null) {
			mLists.clear();
		}
	}

	public Object get(Token aResponse, String aId, int aIndex) {
		List lList = mLists.get(aId);
		if (lList != null) {
			return lList.get(aIndex);
		}
		return null;
	}

	public void add(Token aResponse, String aId, Object aObject) {
		List lList = mLists.get(aId);
		if (lList != null) {
			lList.add(aObject);
		}
	}

	public void remove(Token aResponse, String aId, int aIndex) {
		List lList = mLists.get(aId);
		if (lList != null) {
			lList.remove(aIndex);
		}
	}

	public void destroy(Token aResponse, String aId) {
		mLists.remove(aId);
	}
}
