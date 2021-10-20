/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.api.identifier;

/**
 * Used to uniquely identify an entity by using an identifier.
 * 
 * @author rajashek, schnicke
 *
 */
public interface IIdentifier {
	/**
	 * Gets the type of the Identifier, e.g. IRI, IRDI etc.
	 */
	public IdentifierType getIdType();

	/**
	 * Gets the identifier of the element. Its type is defined in idType.
	 */
	public String getId();
}
