/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.api.reference.enums;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.enumhelper.StandardizedLiteralEnum;
import org.eclipse.basyx.submodel.metamodel.enumhelper.StandardizedLiteralEnumHelper;

/**
 * KeyType, LocalKeyType, IdentifierType as defined in DAAS document <br/>
 * <br />
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
