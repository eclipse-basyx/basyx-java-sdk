/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
 * @author conradi
 *
 */
public class XMLHelper {
	public static final String TEXT = "#text";
	
	
	/**
	 * The XML Parser returns a Map if a requested XML-Element exists only once,<br>
	 * but a List of Maps if it exists multiple times. This function makes sure,<br>
	 * that it is always a List of Maps, by either casting or putting the Map into a List.
	 * 
	 * @param xmlObject a XML Object, that is either a Map or a List of Maps
	 * @return a List of Maps
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> getList(Object xmlObject) {
		if(xmlObject instanceof List<?>) {
			return (List<Map<String, Object>>) xmlObject;
		} else if (xmlObject instanceof Map<?, ?>) {
			List<Map<String, Object>> list = new ArrayList<>();
			list.add((Map<String, Object>) xmlObject);
			return list;
		}
		return new ArrayList<>();
	}
	
	
	/**
	 * If the content of a XML-Element is requested, the parser returns an Object or null, if it doesn't exist.<br>
	 * This function casts the Object into a String and replaces null with an empty String.
	 * 
	 * @param object a Object that is either a String or null
	 * @return the given String or an empty String
	 */
	public static String getString(Object object) {
		return object instanceof String ? ((String) object).trim() : "";
	}
	
	/**
	 * adapts AASX value type to local value types 
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
}
