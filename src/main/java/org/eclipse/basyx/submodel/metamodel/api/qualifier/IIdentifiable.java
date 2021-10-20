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

import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * An element that has a globally unique identifier.
 * 
 * @author rajashek, schnicke
 *
 */

public interface IIdentifiable  extends IReferable {
	/**
	 * Gets the administrative information of an identifiable element.
	 * 
	 * @return
	 */
	public IAdministrativeInformation getAdministration();
	
	/**
	 * Gets theglobally unique identification of the element.
	 * 
	 * @return
	 */
	public IIdentifier getIdentification();
}
