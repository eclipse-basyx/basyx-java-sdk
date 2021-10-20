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
 * Possible level types as defined in DAAS for IEC61360 data specification templates
 * 
 * @author espen
 *
 */
public enum LevelType implements StandardizedLiteralEnum {
	MIN("Min"), MAX("Max"), NOM("Nom"), TYP("Typ");
	
	private String standardizedLiteral;

	private LevelType(String standardizedLiteral) {
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

	public static LevelType fromString(String str) {
		return StandardizedLiteralEnumHelper.fromLiteral(LevelType.class, str);
	}
}
