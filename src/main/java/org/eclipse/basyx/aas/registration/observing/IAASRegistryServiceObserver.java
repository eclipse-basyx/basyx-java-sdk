/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/

 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package org.eclipse.basyx.aas.registration.observing;

import org.eclipse.basyx.aas.observer.IObserver;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * Interface for an observer of {@link ObservableAASRegistryService}
 *
 * @author haque
 *
 */
public interface IAASRegistryServiceObserver extends IObserver {

	/**
	 * Is called when an AAS is registered
	 *
	 * @param aasId
	 *            id of the registered AAS
	 */
	public void aasRegistered(String aasId);

	/**
	 * Is called when an AAS is updated
	 *
	 * @param aasId
	 *            id of the updated AAS
	 */
	public void aasUpdated(String aasId);

	/**
	 * Is called when a submodel is registered
	 *
	 * @param aasIdentifier
	 *            identifier of the parent AAS
	 * @param submodelIdentifier
	 *            identifier of the registered submodel
	 */
	public void submodelRegistered(IIdentifier aasIdentifier, IIdentifier submodelIdentifier);

	/**
	 * Is called when an AAS is deleted
	 *
	 * @param aasId
	 *            id of the deleted AAS
	 */
	public void aasDeleted(String aasId);

	/**
	 * Is called when a submodel is deleted
	 *
	 * @param aasIdentifier
	 *            identifier of the parent AAS
	 * @param submodelIdentifier
	 *            identifier of the deleted Submodel
	 */
	public void submodelDeleted(IIdentifier aasIdentifier, IIdentifier submodelIdentifier);

}
