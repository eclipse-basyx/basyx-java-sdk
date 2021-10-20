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

import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;

/**
 * Element that can have a semantic definition.
 * 
 * @author rajashek
 *
 */
public interface IHasSemantics {

	/**
	 * Gets the identifier of the semantic definition of the element. It is called
	 * semantic id of the element. <br />
	 * <br />
	 * The semantic id may either reference an external global id or it may
	 * reference a referable model element of kind=Template that defines the
	 * semantics of the element.
	 * 
	 * @return
	 */
	public IReference getSemanticId();
}
