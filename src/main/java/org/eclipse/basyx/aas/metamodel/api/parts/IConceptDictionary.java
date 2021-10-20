/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.metamodel.api.parts;

import java.util.Collection;

import org.eclipse.basyx.submodel.metamodel.api.parts.IConceptDescription;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IReferable;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;

/**
 * A dictionary contains elements that can be reused. <br />
 * <br />
 * The concept dictionary contains concept descriptions. <br />
 * <br />
 * Typically a concept description dictionary of an AAS contains only concept
 * descriptions of elements used within submodels of the AAS.
 * 
 * @author rajashek, schnicke
 *
 */

public interface IConceptDictionary extends IReferable {
	/**
	 * Returns the external concept descriptions that defines a concept.
	 * 
	 * @return
	 */
	public Collection<IReference> getConceptDescriptionReferences();

	/**
	 * Returns the internal concept descriptions that defines a concept.
	 * 
	 * @return
	 */
	public Collection<IConceptDescription> getConceptDescriptions();
}
