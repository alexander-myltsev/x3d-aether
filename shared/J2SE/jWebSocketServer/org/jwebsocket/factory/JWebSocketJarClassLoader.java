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
package org.jwebsocket.factory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * ClassLoader that loads the classes from the jars. Engine, Servers, Plugins
 * all configured via jWebSocket.xml file is loaded using this class.
 * 
 * @author puran
 * @version $Id: JWebSocketJarClassLoader.java 388 2010-04-29 19:15:54Z mailtopuran $
 */
public class JWebSocketJarClassLoader extends URLClassLoader {

	/**
	 * constructor
	 */
	public JWebSocketJarClassLoader() {
		super(new URL[]{});
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param path
	 * @throws MalformedURLException
	 */
	public void addFile(String aPath) throws MalformedURLException {
		// fixed by Alex 2010-06-07 to allow class loading from any .jar
		// file at run-time w/o manipulating the manifest or classpath.
		File file = new File(aPath);
		URL url = file.toURI().toURL();
		addURL(url);
		/*
		String urlPath = "jar:file://" + path + "!/";
		addURL(new URL(urlPath));
		 */
	}
}
