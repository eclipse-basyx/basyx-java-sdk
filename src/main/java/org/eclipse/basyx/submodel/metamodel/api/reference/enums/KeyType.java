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
package org.eclipse.basyx.submodel.metamodel.api.reference.enums;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.enumhelper.StandardizedLiteralEnum;
import org.eclipse.basyx.submodel.metamodel.enumhelper.StandardizedLiteralEnumHelper;

/**
 * KeyType, LocalKeyType, IdentifierType as defined in DAAS document <br>
 * <br>
 * Since there's no enum inheritance in Java, all enums are merged into a single
 * class
 * 
 * @author schnicke
 *
 */
public enum KeyType implements StandardizedLiteralEnum {
	/**
	 * Enum values of IdentifierType
	 */
	CUSTOM(IdentifierType.CUSTOM.toString()), IRDI(IdentifierType.IRDI.toString()), IRI(IdentifierType.IRI.toString()),

	/**
	 * Enum values of LocalKeyType
	 */
	IDSHORT("IdShort"), FRAGMENTID("FragmentId");

	private String standardizedLiteral;

	private KeyType(String standardizedLiteral) {
		this.standardizedLiteral = standardizedLiteral;
	}

	@Override
	public String getStandardizedLiteral() {
		return this.standardizedLiteral;
	}

	@Override
	public String toString() {
		return standardizedLiteral;
	}

	public static KeyType fromIdentifierType(IdentifierType type) {
		return fromString(type.toString());
	}

	public static KeyType fromString(String str) {
		return StandardizedLiteralEnumHelper.fromLiteral(KeyType.class, str);
	}
}
