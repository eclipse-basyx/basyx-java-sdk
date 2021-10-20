/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
