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

import org.eclipse.basyx.submodel.metamodel.enumhelper.StandardizedLiteralEnum;
import org.eclipse.basyx.submodel.metamodel.enumhelper.StandardizedLiteralEnumHelper;

/**
 * KeyElements, ReferableElements, IdentifiableElements as defined in DAAS
 * document <br/>
 * <br />
 * Since there's no enum inheritance in Java, all enums are merged into a single
 * class
 * 
 * @author schnicke
 *
 */
public enum KeyElements implements StandardizedLiteralEnum {
	/**
	 * Enum values of KeyElements
	 */
	GLOBALREFERENCE("GlobalReference"),
	FRAGMENTREFERENCE("FragmentReference"),
	
	/**
	 * Enum values of ReferableElements
	 */
	ACCESSPERMISSIONRULE("AccessPermissionRule"),
	ANNOTATEDRELATIONSHIPELEMENT("AnnotatedRelationshipElement"),
	BASICEVENT("BasicEvent"),
	BLOB("Blob"),
	CAPABILITY("Capability"),
	CONCEPTDICTIONARY("ConceptDictionary"),
	DATAELEMENT("DataElement"),
	FILE("File"),
	ENTITY("Entity"),
	EVENT("Event"),
	MULTILANGUAGEPROPERTY("MultiLanguageProperty"),
	OPERATION("Operation"),
	PROPERTY("Property"),
	RANGE("Range"),
	REFERENCEELEMENT("ReferenceElement"),
	RELATIONSHIPELEMENT("RelationshipElement"),
	SUBMODELELEMENT("SubmodelElement"),
	SUBMODELELEMENTCOLLECTION("SubmodelElementCollection"),
	VIEW("View"),
	
	/**
	 * Enum values of IdentifiableElements
	 */
	ASSET("Asset"),
	ASSETADMINISTRATIONSHELL("AssetAdministrationShell"),
	CONCEPTDESCRIPTION("ConceptDescription"),
	SUBMODEL("Submodel");
	

	private String standardizedLiteral;

	private KeyElements(String standardizedLiteral) {
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

	public static KeyElements fromString(String str) {
		return StandardizedLiteralEnumHelper.fromLiteral(KeyElements.class, str);
	}
}
