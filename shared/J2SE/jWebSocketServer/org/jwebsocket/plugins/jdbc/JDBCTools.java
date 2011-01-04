/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jwebsocket.plugins.jdbc;

import java.sql.ResultSetMetaData;

/**
 *
 * @author aschulze
 */
public class JDBCTools {

	public static String extractSimpleClass(String aClassName) {
		if (aClassName.equals("[B")) {
			return ("Blob");
		}
		int lLastDotPos = aClassName.lastIndexOf('.');
		if (lLastDotPos >= 0) {
			aClassName = aClassName.substring(lLastDotPos + 1);
		}
		return (aClassName);
	}

	public static String getJSONType(String aJavaType, ResultSetMetaData aMetaData) {
		String lResStr = aJavaType.toLowerCase();
		if (lResStr != null) {
			if (lResStr.equals("bigdecimal")
					|| lResStr.equals("long")
					|| lResStr.equals("int")
					|| lResStr.equals("byte")
					|| lResStr.equals("short")
					|| lResStr.equals("float")
					|| lResStr.equals("double")) {
				lResStr = "number";
			}
		}
		return lResStr;
	}
}
