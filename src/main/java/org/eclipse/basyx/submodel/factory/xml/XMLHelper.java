/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.submodel.factory.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueTypeHelper;

import com.google.common.base.Strings;

/**
 * A Helper class containing tools for the XML converter
 * 
 * @author conradi, jungjan
 *
 */
public class XMLHelper {
	public static final String TEXT = "#text";

	/**
	 * The XML Parser returns a Map if a requested XML-Element exists only once,<br>
	 * but a List of Maps if it exists multiple times. This function makes sure,<br>
	 * that it is always a List of Maps, by either casting or putting the Map into a
	 * List.
	 * 
	 * @param xmlObject
	 *            a XML Object, that is either a Map or a List of Maps
	 * @return a List of Maps
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> getList(Object xmlObject) {
		if (xmlObject instanceof List<?>) {
			return (List<Map<String, Object>>) xmlObject;
		} else if (xmlObject instanceof Map<?, ?>) {
			List<Map<String, Object>> list = new ArrayList<>();
			list.add((Map<String, Object>) xmlObject);
			return list;
		}
		return new ArrayList<>();
	}

	/**
	 * If the content of a XML-Element is requested, the parser returns an Object or
	 * null, if it doesn't exist.<br>
	 * This function casts the Object into a String and replaces null with an empty
	 * String.
	 * 
	 * @param object
	 *            a Object that is either a String or null
	 * @return the given String or an empty String
	 */
	public static String getString(Object object) {
		ValueType valueType;
		try {
			valueType = ValueTypeHelper.getType(object);
		} catch (Exception ex) {
			valueType = ValueType.None;
		}
		if (ValueType.None != valueType)
			return object.toString().trim();
		return "";
	}

	/**
	 * adapts AASX value type to local value types
	 * 
	 * @param valueType
	 * @return
	 */
	public static ValueType convertAASXValueTypeToLocal(String valueType) {
		// Enables parsing external aasx-files with empty type
		if (Strings.isNullOrEmpty(valueType)) {
			return ValueType.AnySimpleType;
		}

		// Enables parsing external aasx-files with anyURI instead of anyuri
		else if (valueType.equals("anyURI")) {
			return ValueType.AnyURI;
		}

		// Enables parsing external aasx-files with date instead of dateTime
		else if (valueType.equals("date")) {
			return ValueType.DateTime;
		}

		// Enables parsing external aasx-files with decimal instead of double
		else if (valueType.equals("decimal")) {
			return ValueType.Double;
		}

		else {
			return ValueTypeHelper.fromName(valueType);
		}
	}

	public static Object convertAASXValueToLocal(String xmlStringValue, ValueType valueType) {
		switch (valueType) {
		case Boolean:
			return verifiedBoolean(xmlStringValue);
		case Double:
			return verifiedDecimal(xmlStringValue);
		case Integer:
			return verifiedInt(xmlStringValue);
		default:
			return xmlStringValue;
		}
	}

	private static Boolean verifiedBoolean(String booleanString) {
		if (isStringNullOrEmpty(booleanString)) {
			return null;
		}
		if (!isLegalBooleanString(booleanString))
			throw new IllegalArgumentException(String.format("The passed value '" + booleanString + "' is not compatible with type Boolean", booleanString));
		return Boolean.parseBoolean(booleanString);
	}

	private static Double verifiedDecimal(String decimalString) {
		if (isStringNullOrEmpty(decimalString)) {
			return null;
		}
		try {
			return Double.parseDouble(decimalString);
		} catch (NumberFormatException e) {
			throw new NumberFormatException(String.format("The passed value '" + decimalString + "' is no compatible decimal type", decimalString));
		}
	}

	private static Integer verifiedInt(String intString) {
		if (isStringNullOrEmpty(intString)) {
			return null;
		}
		try {
			return Integer.parseInt(intString);
		} catch (NumberFormatException e) {
			throw new NumberFormatException(String.format("The passed value '" + intString + "' is not compatible with type Integer", intString));
		}
	}

	private static boolean isLegalBooleanString(String booleanString) {
		return booleanString.equalsIgnoreCase("true") || booleanString.equalsIgnoreCase("false") || booleanString.equalsIgnoreCase("1") || booleanString.equalsIgnoreCase("0");
	}

	private static boolean isStringNullOrEmpty(String decimalString) {
		return decimalString.isEmpty() || decimalString == null;
	}
}
