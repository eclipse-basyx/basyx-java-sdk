/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind;

import org.eclipse.basyx.submodel.metamodel.enumhelper.StandardizedLiteralEnum;
import org.eclipse.basyx.submodel.metamodel.enumhelper.StandardizedLiteralEnumHelper;

/**
 * ModelingKind enum as defined by DAAS document<br />
 * Enumeration for denoting whether an element is a template or an instance.
 * 
 * @author schnicke
 *
 */
public enum ModelingKind implements StandardizedLiteralEnum {

	/**
	 * Software element which specifies the common attributes shared by all
	 * instances of the template.
	 */
	INSTANCE("Instance"),
	/**
	 * Concrete, clearly identifiable component of a certain template.
	 */
	TEMPLATE("Template");

	private String standardizedLiteral;

	private ModelingKind(String standardizedLiteral) {
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

	public static ModelingKind fromString(String str) {
		return StandardizedLiteralEnumHelper.fromLiteral(ModelingKind.class, str);
	}
}
