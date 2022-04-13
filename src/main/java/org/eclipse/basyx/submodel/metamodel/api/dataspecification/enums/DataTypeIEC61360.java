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
package org.eclipse.basyx.submodel.metamodel.api.dataspecification.enums;

import org.eclipse.basyx.submodel.metamodel.enumhelper.StandardizedLiteralEnum;
import org.eclipse.basyx.submodel.metamodel.enumhelper.StandardizedLiteralEnumHelper;

/**
 * Possible value data types as defined in DAAS for IEC61360 data specification
 * templates
 * 
 * @author espen
 *
 */
public enum DataTypeIEC61360 implements StandardizedLiteralEnum {
	BOOLEAN("BOOLEAN"), DATE("DATE"), RATIONAL("RATIONAL"), RATIONAL_MEASURE("RATIONAL_MEASURE"), REAL_COUNT("REAL_COUNT"), REAL_CURRENCY("REAL_CURRENCY"), REAL_MEASURE("REAL_MEASURE"), STRING("STRING"), STRING_TRANSLATABLE(
			"STRING_TRANSLATABLE"), TIME("TIME"), TIME_STAMP("TIME_STAMP"), URL("URL");

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
