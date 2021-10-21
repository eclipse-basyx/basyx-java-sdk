/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
	Int8("byte"), Int16("short"), Int32("int"), Int64("long"),
	UInt8("unsignedByte"), UInt16("unsignedShort"), UInt32("unsignedInt"), UInt64("unsignedLong"),
	String("string"), LangString("langString"),
	AnyURI("anyuri"), Base64Binary("base64Binary"), HexBinary("hexBinary"), NOTATION("notation"), ENTITY("entity"), ID("id"), IDREF("idref"),
	Integer("integer"), NonPositiveInteger("nonPositiveInteger"), NonNegativeInteger("nonNegativeInteger"), PositiveInteger("positiveInteger"), NegativeInteger("negativeInteger"),
	Double("double"), Float("float"), Boolean("boolean"),
	Duration("duration"), DayTimeDuration("dayTimeDuration"), YearMonthDuration("yearMonthDuration"),
	DateTime("dateTime"), DateTimeStamp("dateTimeStamp"), GDay("gDay"), GMonth("gMonth"), GMonthDay("gMonthDay"), GYear("gYear"), GYearMonth("gYearMonth"),
	QName("qName"),
	None("none"), AnyType("anyType"), AnySimpleType("anySimpleType");

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
