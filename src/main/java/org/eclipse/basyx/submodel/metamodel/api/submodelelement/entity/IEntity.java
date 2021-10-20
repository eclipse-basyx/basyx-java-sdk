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

import java.util.Collection;

import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;

/**
 * An entity is a submodel element that is used to model entities.
 * 
 * @author schnicke
 *
 */
public interface IEntity extends ISubmodelElement {
	/**
	 * Gets statements applicable to the entity by a set of submodel elements,
	 * typically with a qualified value.
	 * 
	 * @return
	 */
	Collection<ISubmodelElement> getStatements();

	/**
	 * Gets EntityType describing whether the entity is a comanaged entity or a
	 * self-managed entity.
	 * 
	 * @return
	 */
	EntityType getEntityType();

	/**
	 * Gets the reference to the asset the entity is representing.
	 * 
	 * @return
	 */
	IReference getAsset();
}
