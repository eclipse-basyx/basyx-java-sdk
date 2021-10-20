/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.api.dataspecification.enums;

import org.eclipse.basyx.submodel.metamodel.enumhelper.StandardizedLiteralEnum;
import org.eclipse.basyx.submodel.metamodel.enumhelper.StandardizedLiteralEnumHelper;

/**
 * Possible value data types as defined in DAAS for IEC61360 data specification templates
 * 
 * @author espen
 *
 */
public enum DataTypeIEC61360 implements StandardizedLiteralEnum {
	BOOLEAN("BOOLEAN"), DATE("DATE"), RATIONAL("RATIONAL"), RATIONAL_MEASURE("RATIONAL_MEASURE"),
	REAL_COUNT("REAL_COUNT"), REAL_CURRENCY("REAL_CURRENCY"), REAL_MEASURE("REAL_MEASURE"), STRING("STRING"),
	STRING_TRANSLATABLE("STRING_TRANSLATABLE"),
	TIME("TIME"), TIME_STAMP("TIME_STAMP"), URL("URL");

	private String standardizedLiteral;

	private DataTypeIEC61360(String standardizedLiteral) {
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

	public static DataTypeIEC61360 fromString(String str) {
		return StandardizedLiteralEnumHelper.fromLiteral(DataTypeIEC61360.class, str);
	}
}
