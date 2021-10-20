/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.api.qualifier;

import java.util.Collection;

import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IEmbeddedDataSpecification;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;

/**
 * Element that can be extended by using data specification templates. A data
 * specification template defines the additional attributes an element may or
 * shall have. The data specifications used are explicitly specified with their
 * global id.
 *
 * @author rajashek, schnicke
 *
 */

public interface IHasDataSpecification {

	/**
	 * Global reference to the data specification template used by the element.
	 */
	public Collection<IReference> getDataSpecificationReferences();

	public Collection<IEmbeddedDataSpecification> getEmbeddedDataSpecifications();
}
