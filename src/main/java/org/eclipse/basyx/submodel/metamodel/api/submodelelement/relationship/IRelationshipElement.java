/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.api.submodelelement.relationship;

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.RelationshipElementValue;

/**
 * A relationship element is used to define a relationship between two referable
 * elements.
 * 
 * @author rajashek, schnicke
 *
 */
public interface IRelationshipElement extends ISubmodelElement {
	
	@Override
	RelationshipElementValue getValue();

	/**
	 * Sets the relationship of the RelationshipElement submodel element
	 * 
	 * @param value
	 */
	void setValue(RelationshipElementValue value);
}
