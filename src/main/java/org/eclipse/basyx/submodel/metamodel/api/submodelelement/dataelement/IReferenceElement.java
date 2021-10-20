/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement;

import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;

/**
 * A reference element is a data element that defines a logical reference to
 * another element within the same or another AAS or a reference to an external
 * object or entity.
 * 
 * @author rajashek, schnicke
 *
 */
public interface IReferenceElement extends ISubmodelElement {
	/**
	 * Gets the reference to any other referable element of the same or of any other
	 * AAS or a reference to an external object or entity.
	 * 
	 * @return
	 */
	@Override
	public IReference getValue();

	/**
	 * Sets the reference to any other referable element of the same or of any other
	 * AAS or a reference to an external object or entity
	 * 
	 * @param value
	 */
	public void setValue(IReference value);

}
