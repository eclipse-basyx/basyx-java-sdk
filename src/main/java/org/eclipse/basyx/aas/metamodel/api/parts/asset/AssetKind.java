/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.metamodel.api.parts.asset;

import org.eclipse.basyx.submodel.metamodel.enumhelper.StandardizedLiteralEnum;
import org.eclipse.basyx.submodel.metamodel.enumhelper.StandardizedLiteralEnumHelper;

/**
 * AssetKind enum as defined by DAAS document<br />
 * Enumeration for denoting whether an element is a type or an instance.
 * 
 * @author schnicke
 *
 */
public enum AssetKind implements StandardizedLiteralEnum {
	/**
	 * Hardware or software element which specifies the common attributes shared by
	 * all instances of the type
	 */
	TYPE("Type"),
	/**
	 * Concrete, clearly identifiable component of a certain type
	 */
	INSTANCE("Instance");

	private String standardizedLiteral;

	private AssetKind(String name) {
		this.standardizedLiteral = name;
	}

	@Override
	public String getStandardizedLiteral() {
		return standardizedLiteral;
	}

	@Override
	public String toString() {
		return standardizedLiteral;
	}

	public static AssetKind fromString(String str) {
		return StandardizedLiteralEnumHelper.fromLiteral(AssetKind.class, str);
	}
}
