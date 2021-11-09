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
	 * Is called when an Shell is registered
	 *
	 * @param aasId
	 *            id of the registered Shell
	 */
	public void shellRegistered(String aasId);

	/**
	 * Is called when an Shell is updated
	 *
	 * @param shellId
	 *            id of the updated Shell
	 */
	public void shellUpdated(String shellId);

	/**
	 * Is called when a submodel is updated
	 *
	 * @param submodelId
	 *            id of the updated submodel
	 */
	public void submodelUpdated(String submodelId);

	/**
	 * Is called when a submodel is registered
	 *
	 * @param shellIdentifier
	 *            identifier of the parent Shell
	 * @param submodelIdentifier
	 *            identifier of the registered submodel
	 */
	public void submodelRegistered(IIdentifier shellIdentifier, IIdentifier submodelIdentifier);

	/**
	 * Is called when a submodel is updated
	 *
	 * @param shellIdentifier
	 *            identifier of the parent Shell
	 * @param submodelIdentifier
	 *            identifier of the updated submodel
	 */
	public void submodelUpdated(IIdentifier shellIdentifier, IIdentifier submodelIdentifier);

	/**
	 * Is called when a Shell is deleted
	 *
	 * @param shellId
	 *            id of the deleted Shell
	 */
	public void shellDeleted(String shellId);

	/**
	 * Is called when a submodel is deleted
	 *
	 * @param shellIdentifier
	 *            identifier of the parent Shell
	 * @param submodelIdentifier
	 *            identifier of the deleted Submodel
	 */
	public void shellSubmodelDeleted(IIdentifier shellIdentifier, IIdentifier submodelIdentifier);

	/**
	 * Is called when a submodel is deleted
	 *
	 * @param submodelIdentifier
	 *            identifier of the deleted Submodel
	 */
	public void submodelDeleted(IIdentifier submodelIdentifier);
}
