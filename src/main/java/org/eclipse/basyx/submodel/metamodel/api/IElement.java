/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.api;


/**
 * Base interface for all AAS elements
 * 
 * @author kuhn
 *
 */
public interface IElement {

	/**
	 * Return the unique ID that identifies an VAB element
	 * 
	 * @return unique ID
	 */
	public String getIdShort();
}
