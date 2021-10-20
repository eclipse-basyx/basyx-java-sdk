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


/**
 * Administrative metainformation for an element like version information.
 * 
 * @author rajashek, schnicke
 *
 */
public interface IAdministrativeInformation extends IHasDataSpecification {
	/**
	 * Gets the version of the element
	 * 
	 * @return
	 */
	public String getVersion();
	
	/**
	 * Gets the revision of the element
	 * 
	 * @return
	 */
	public String getRevision();
}
