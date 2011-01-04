//	---------------------------------------------------------------------------
//	five-feet-further RIAServer-Framework - DBQueryResult.java
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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

/**
 * Provides a record with the resultset of a SQL select command.
 * Contains the resultset as well as the metadata, and optional exception and
 * message.
 * @since 1.0
 * @author Alexander Schulze
 */
public class DBQueryResult {

	public Statement sql = null;
	public ResultSet resultSet = null;
	public ResultSetMetaData metaData = null;
	public String exception = null;
	public String message = null;
}
