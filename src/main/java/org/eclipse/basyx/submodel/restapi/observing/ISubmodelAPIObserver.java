/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/
package org.eclipse.basyx.submodel.restapi.observing;

import org.eclipse.basyx.core.observer.IObserver;

/**
 * Interface for an observer of {@link ObservableSubmodelAPI}
 * 
 * @author conradi
 *
 */
public interface ISubmodelAPIObserver extends IObserver {

	/**
	 * Is called when a SubmodelElement is added
	 * 
	 * @param idShortPath the idShortPath of the added element
	 * @param newValue the value of the new element
	 */
	public void elementAdded(String idShortPath, Object newValue);
	
	/**
	 * Is called when a SubmodelElement is deleted
	 * 
	 * @param idShortPath the idShortPath of the deleted element
	 */
	public void elementDeleted(String idShortPath);
	
	/**
	 * Is called when a SubmodelElement is updated
	 * 
	 * @param idShortPath the idShortPath of the updated element
	 * @param newValue the new value of the updated element
	 */
	public void elementUpdated(String idShortPath, Object newValue);
	
}
