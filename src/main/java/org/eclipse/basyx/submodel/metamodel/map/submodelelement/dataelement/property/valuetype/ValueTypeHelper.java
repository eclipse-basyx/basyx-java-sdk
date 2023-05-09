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
package org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Period;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

/**
 * Provides utility functions for
 * {@link org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType
 * PropertyValueTypeDef} <br>
 * * Creating a PropertyValueTypeDef from name <br>
 * * Creating a PropertyValueTypeDef for an object
 * 
 * @author schnicke
 *
 */
public class ValueTypeHelper {
	private static Map<String, ValueType> typeMap = new LinkedHashMap<>();
	private static Logger logger = LoggerFactory.getLogger(ValueTypeHelper.class);

	// insert all types into a Map to allow getting a PropertyValueType based on a
	// String
	static {
		for (ValueType t : ValueType.values()) {
			typeMap.put(t.toString(), t);
		}
	}

	// Strings required for meta-model conformant valueType format
	private static final String TYPE_NAME = "name";
	private static final String TYPE_OBJECT = "dataObjectType";

	/**
	 * Map the name of a PropertyValueTypeDef to a PropertyValueTypeDef
	 * 
	 * @param name
	 * @return
	 */
	public static ValueType fromName(String name) {
		if (!name.toLowerCase().equals(name)) {
			logger.warn("Type " + name + " does not consider standard. Trying to use it in lowercase!");
		}
		if (typeMap.containsKey(name.toLowerCase())) {
			return typeMap.get(name.toLowerCase());
		} else {
			throw new RuntimeException("Unknown type name " + name + "; can not handle this PropertyValueType");
		}
	}

	/**
	 * Creates the PropertyValueTypeDef for an arbitrary object
	 * 
	 * @param obj
	 * @return
	 */
	public static ValueType getType(Object obj) {
		ValueType objectType;

		if (obj == null) {
			objectType = ValueType.None;
		} else {
			Class<?> c = obj.getClass();
			if (c == byte.class || c == Byte.class) {
				objectType = ValueType.Int8;
			} else if (c == short.class || c == Short.class) {
				objectType = ValueType.Int16;
			} else if (c == int.class || c == Integer.class) {
				objectType = ValueType.Int32;
			} else if (c == long.class || c == Long.class) {
				objectType = ValueType.Int64;
			} else if (c == BigInteger.class) {
				objectType = handleBigInteger((BigInteger) obj);
			} else if (c == void.class || c == Void.class) {
				objectType = ValueType.None;
			} else if (c == boolean.class || c == Boolean.class) {
				objectType = ValueType.Boolean;
			} else if (c == float.class || c == Float.class) {
				objectType = ValueType.Float;
			} else if (c == double.class || c == Double.class) {
				objectType = ValueType.Double;
			} else if (c == String.class) {
				objectType = ValueType.String;
			} else if (c == Duration.class) {
				objectType = ValueType.Duration;
			} else if (c == Period.class) {
				objectType = ValueType.YearMonthDuration;
			} else if (c == QName.class) {
				objectType = ValueType.QName;
			} else if (obj instanceof XMLGregorianCalendar) {
				objectType = ValueType.DateTime;
			} else {
				throw new RuntimeException("Cannot map object " + obj + " to any PropertyValueTypeDef");
			}
		}
		return objectType;
	}

	private static ValueType handleBigInteger(BigInteger integer) {
		if (integer.compareTo(new BigInteger("0")) > 0) {
			return ValueType.PositiveInteger;
		} else if (integer.compareTo(new BigInteger("0")) < 0) {
			return ValueType.NegativeInteger;
		} else {
			return ValueType.Integer;
		}
	}

	/**
	 * Map the PropertyValueType to Java type
	 * 
	 */
	@SuppressWarnings("deprecation")
	public static Object getJavaObject(Object value, ValueType objType) {

		if (value == null) {
			return null;
		}

		if (!(value instanceof String)) {
			value = value.toString();
		}

		Object target = null;
		if (objType != null) {
			switch (objType) {
			case Int8:
				if (((String) value).isEmpty()) {
					target = new Byte("NaN");
				} else {
					target = new Byte((String) value);
				}
				break;
			case Int16:
			case UInt8:
				if (((String) value).isEmpty()) {
					target = new Short("NaN");
				} else {
					target = new Short((String) value);
				}
				break;
			case Int32:
			case UInt16:
				if (((String) value).isEmpty()) {
					target = new Integer("NaN");
				} else {
					target = new Integer((String) value);
				}
				break;
			case Int64:
			case UInt32:
				if (((String) value).isEmpty()) {
					target = new Long("NaN");
				} else {
					target = new Long((String) value);
				}
				break;
			case UInt64:
			case Integer:
			case PositiveInteger:
			case NonPositiveInteger:
			case NonNegativeInteger:
			case NegativeInteger:
				if (((String) value).isEmpty()) {
					target = new BigInteger("NaN");
				} else {
					target = new BigInteger((String) value);
				}
				break;
			case Double:
				if (((String) value).isEmpty()) {
					target = new Double("NaN");
				} else {
					target = new Double((String) value);
				}
				break;
			case Float:
				if (((String) value).isEmpty()) {
					target = new Float("NaN");
				} else {
					target = new Float((String) value);
				}
				break;
			case Boolean:
				target = new Boolean((String) value);
				break;
			case AnySimpleType:
			case String:
			case LangString:
			case AnyURI:
			case Base64Binary:
			case HexBinary:
			case NOTATION:
			case ENTITY:
			case ID:
			case IDREF:
				target = value;
				break;
			case Duration:
			case DayTimeDuration:
				target = Duration.parse((String) value);
				break;
			case YearMonthDuration:
				target = Period.parse((String) value);
				break;
			case Date:
			case DateTime:
			case DateTimeStamp:
			case GDay:
			case GMonth:
			case GMonthDay:
			case GYear:
			case GYearMonth:
				try {
					target = DatatypeFactory.newInstance().newXMLGregorianCalendar((String) value);
					break;
				} catch (DatatypeConfigurationException e) {
					e.printStackTrace();
					throw new RuntimeException("Could not create DatatypeFactory for XMLGregorianCaldner handling");
				}
			case QName:
				target = QName.valueOf((String) value);
				break;
			default:
				target = value;
				break;
			}
			return target;
		} else {
			return null;
		}

	}

	/**
	 * Convert an object which has special types (Duration, period, Qname, Date) to
	 * String object Used by Property.set() or ConnectedProperty.set(), prepare for
	 * the serialization
	 * 
	 * @param value
	 *            - the target object
	 * @return
	 */
	public static Object prepareForSerialization(Object value) {
		if (value != null) {
			Class<?> c = value.getClass();
			if (c == Duration.class || c == Period.class || c == QName.class || value instanceof XMLGregorianCalendar) {
				return value.toString();
			}
		}
		return value;

	}

	@SuppressWarnings("unchecked")
	public static ValueType readTypeDef(Object vTypeMap) {
		if (vTypeMap instanceof String) {
			// From xml/json-schema point of view, this should be only a string.
			return fromName((String) vTypeMap);
		} else if (vTypeMap instanceof Map<?, ?>) {
			// Reading still supported, but should be a simple string
			Map<String, Object> map = (Map<String, Object>) vTypeMap;
			Map<String, Object> dot = (Map<String, Object>) map.get(TYPE_OBJECT);

			return fromName(dot.get(TYPE_NAME).toString());
		}
		return null;
	}
}
