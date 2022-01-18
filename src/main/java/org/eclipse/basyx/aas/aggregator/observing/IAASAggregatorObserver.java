/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package org.eclipse.basyx.aas.aggregator.observing;

import org.eclipse.basyx.submodel.observer.IObserver;

/**
 * Interface for an observer of {@link ObservableAASAggregator}
 * @author haque
 *
 */
public interface IAASAggregatorObserver extends IObserver {
	
	/**
	 * Is called when an AAS is created
	 * @param aasId id of the created AAS
	 */
	public void aasCreated(String aasId);
	
	/**
	 * Is called when an AAS is updated
	 * @param aasId id of the updated AAS
	 */
	public void aasUpdated(String aasId);
	
	/**
	 * Is called when an AAS is deleted
	 * @param aasId id of the deleted AAS
	 */
	public void aasDeleted(String aasId);
}
