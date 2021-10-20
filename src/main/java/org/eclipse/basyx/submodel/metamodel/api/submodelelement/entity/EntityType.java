/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.api.submodelelement.entity;

import org.eclipse.basyx.submodel.metamodel.enumhelper.StandardizedLiteralEnum;
import org.eclipse.basyx.submodel.metamodel.enumhelper.StandardizedLiteralEnumHelper;

/**
 * Enumeration for denoting whether an entity is a self-managed entity or a
 * comanaged entity.
 * 
 * @author schnicke
 *
 */
public enum EntityType implements StandardizedLiteralEnum {
	/**
	 * For co-managed entities there is no separate AAS. Co-managed entities need to
	 * be part of a self-managed entity.
	 */
	COMANAGEDENTITY("CoManagedEntity"),
	
	/**
	 * Self-Managed Entities have their own AAS but can be part of the bill of
	 * material of a composite self-managed entity. The asset of an I4.0 Component
	 * is a self-managed entity per definition.
	 */
	SELFMANAGEDENTITY("SelfManagedEntity");
	
	private String standardizedLiteral;

	private EntityType(String standardizedLiteral) {
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

	public static EntityType fromString(String str) {
		return StandardizedLiteralEnumHelper.fromLiteral(EntityType.class, str);
	}
}
