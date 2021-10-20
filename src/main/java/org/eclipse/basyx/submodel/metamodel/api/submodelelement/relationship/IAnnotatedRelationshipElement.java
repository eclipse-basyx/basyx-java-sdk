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

import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.AnnotatedRelationshipElementValue;

/**
 * An annotated relationship element is a relationship element that can be
 * annotated with additional data elements.
 * 
 * @author schnicke
 *
 */
public interface IAnnotatedRelationshipElement extends IRelationshipElement {
	@Override
	AnnotatedRelationshipElementValue getValue();

	void setValue(AnnotatedRelationshipElementValue value);
}
