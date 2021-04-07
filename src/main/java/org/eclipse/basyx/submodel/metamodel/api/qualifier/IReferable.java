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
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;

/**
 * An element that is referable by its idShort. This id is not globally unique.
 * This id is unique within the name space of the element.
 * 
 * @author rajashek, schnicke
 *
 */
public interface IReferable {
	/**
	 * Gets the identifying string of the element within its name space.
	 * 
	 * @return
	 */
	public String getIdShort();

	/**
	 * Gets the category of the referable. The category is a value that gives
	 * further meta information w.r.t. to the class of the element. It affects the
	 * expected existence of attributes and the applicability of constraints.
	 * 
	 * @return
	 */
	public String getCategory();

	/**
	 * Gets the description or comments on the element.<br>
	 * <br>
	 * The description can be provided in several languages.
	 * 
	 * @return
	 */
	public LangStrings getDescription();

	/**
	 * Gets the parent of the referable.
	 * 
	 * @return
	 */
	public IReference getParent();

	public IReference getReference();
}
