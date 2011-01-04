//	---------------------------------------------------------------------------
//	five-feet-further RIAServer-Framework - DBExecResult.java
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
//	Dieses Programm ist freie Software. Sie k�nnen es unter den Bedingungen der
//	GNU Lesser General Public License, wie von der Free Software Foundation
//	ver�ffentlicht, weitergeben und/oder modifizieren, entweder gem�� Version 3
//	der Lizenz oder (nach Ihrer Option) jeder sp�teren Version.
//	Die Ver�ffentlichung dieses Programms erfolgt in der Hoffnung, da� es Ihnen
//	von Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE, sogar ohne die
//	implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT F�R EINEN
//	BESTIMMTEN ZWECK. Details finden Sie in der GNU Lesser General Public License.
//	Sie sollten ein Exemplar der GNU Lesser General Public License zusammen mit diesem
//	Programm erhalten haben. Falls nicht, siehe <http://www.gnu.org/licenses/lgpl.html>.
//	---------------------------------------------------------------------------
package org.jwebsocket.plugins.jdbc;

/**
 * Provides a record with the result of a SQL manipulation command like 
 * update, insert delete.
 * Provides constants for the result types.
 * @since 1.0
 * @author Alexander Schulze
 */
public class DBExecResult {

	/**
	 * The type of the result is unknown, 
	 * usually this result should not be returned to the caller.
	 * @since 1.0
	 */
	public final static int RT_UNKNOWN = -1;
	/**
	 * The record contains a result for a previous request. The object field 
	 * optionally can contain additional data.
	 * @since 1.0
	 */
	public final static int RT_RESULT = 0;
	/**
	 * The operation has successfully finished.
	 * @since 1.0
	 */
	public final static int RT_SUCCESS = 1;
	/**
	 * The operation caused a warning.
	 * @since 1.0
	 */
	public final static int RT_WARNING = 2;
	/**
	 * The operation caused an error.
	 * @since 1.0
	 */
	public final static int RT_ERROR = 3;
	/**
	 * The operation caused an exception, but the server is still ok.
	 * @since 1.0
	 */
	public final static int RT_EXCEPTION = 4;
	/**
	 * The operation caused an exception, but the server has a problem that 
	 * requires maintenance by the server admin.
	 * @since 1.0
	 */
	public final static int RT_FATAL = 5;
	/**
	 * The number of rows, that has been affected by the command.
	 * @since 1.0
	 */
	public int affectedRows = 0;
	/**
	 * A reference to any arbitrary object, 
	 * created by a method and to be returned to the caller.
	 * @since 1.0
	 */
	public Object result = null;
	/**
	 * The type of the result, one of the <code><i>RT_XXX</i></code> constants.
	 * @since 1.0
	 */
	public int type = RT_UNKNOWN;
	/**
	 * A (locale-)key to uniquely define the result, independantly 
	 * from the language.
	 * @since 1.0
	 */
	public String key = null;
	/**
	 * Message to the user, dependant on the currently selected language.
	 * @since 1.0
	 */
	public String message = null;
}
