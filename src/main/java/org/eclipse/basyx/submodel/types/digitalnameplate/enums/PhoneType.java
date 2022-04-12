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
package org.eclipse.basyx.submodel.types.digitalnameplate.enums;

import org.eclipse.basyx.submodel.metamodel.enumhelper.StandardizedLiteralEnum;
import org.eclipse.basyx.submodel.metamodel.enumhelper.StandardizedLiteralEnumHelper;

/**
 * characterization of a telephone according to its location or usage
 * as described in the AAS Digital Nameplate template
 * @author haque
 *
 */
public enum PhoneType implements StandardizedLiteralEnum {
	
	/**
	 * (office, 0173-1#07-AAS754#001)
	 */
	OFFICE("1"),
	
	/**
	 * (office mobile, 0173-1#07-AAS755#001)
	 */
	OFFICEMOBILE("2"),
	
	/**
	 * (secretary, 0173-1#07-AAS756#001)
	 */
	SECRETARY("3"),
	
	/**
	 * (substitute, 0173-1#07-AAS757#001)
	 */
	SUBSTITUTE("4"),
	
	/**
	 * (home, 0173-1#07-AAS758#001)
	 */
	HOME("5"),
	
	/**
	 * (private mobile, 0173-1#07-AAS759#001)
	 */
	PRIVATEMOBILE("6");;

	private String standardizedLiteral;

	private PhoneType(String standardizedLiteral) {
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

	public static PhoneType fromString(String str) {
		return StandardizedLiteralEnumHelper.fromLiteral(PhoneType.class, str);
	}
}
