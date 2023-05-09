/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
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

import org.eclipse.basyx.submodel.metamodel.enumhelper.StandardizedLiteralEnum;
import org.eclipse.basyx.submodel.metamodel.enumhelper.StandardizedLiteralEnumHelper;

/**
 * Helper enum to handle anySimpleTypeDef as defined in DAAS document <br>
 * Represents the type of a data entry <br>
 * TODO: Extend this to support rest of types (cf. p. 58)
 * 
 * @author schnicke
 *
 */
public enum ValueType implements StandardizedLiteralEnum {
	Int8("byte"), Int16("short"), Int32("int"), Int64("long"), UInt8("unsignedByte"), UInt16("unsignedShort"), UInt32("unsignedInt"), UInt64("unsignedLong"), String("string"), LangString("langString"), AnyURI("anyURI"), Base64Binary(
			"base64Binary"), HexBinary("hexBinary"), NOTATION("notation"), ENTITY("entity"), ID("id"), IDREF("idref"), Integer("integer"), NonPositiveInteger("nonPositiveInteger"), NonNegativeInteger("nonNegativeInteger"), PositiveInteger(
			"positiveInteger"), NegativeInteger("negativeInteger"), Decimal("decimal"), Double("double"), Float("float"), Boolean("boolean"), Duration("duration"), DayTimeDuration("dayTimeDuration"), YearMonthDuration("yearMonthDuration"), Date(
			"date"), DateTime("dateTime"), DateTimeStamp(
			"dateTimeStamp"), GDay("gDay"), GMonth("gMonth"), GMonthDay("gMonthDay"), GYear("gYear"), GYearMonth("gYearMonth"), QName("qName"), None("none"), AnyType("anyType"), AnySimpleType("anySimpleType"),
	;

	private String standardizedLiteral;

	private ValueType(String standardizedLiteral) {
		this.standardizedLiteral = standardizedLiteral;
	}

	@Override
	public String getStandardizedLiteral() {
		return standardizedLiteral;
	}

	@Override
	public String toString() {
		return standardizedLiteral;
	}

	public static ValueType fromString(String str) {
		return StandardizedLiteralEnumHelper.fromLiteral(ValueType.class, str);
	}

}
