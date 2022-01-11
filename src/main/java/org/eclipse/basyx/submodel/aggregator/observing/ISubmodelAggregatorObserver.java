/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package org.eclipse.basyx.submodel.aggregator.observing;

import org.eclipse.basyx.submodel.observer.IObserver;

/**
 * Interface for an observer of {@link ObservableSubmodelAggregator}
 *
 * @author fischer, jungjan
 *
 */
public interface ISubmodelAggregatorObserver extends IObserver {

	/**
	 * Is called when an submodel is created
	 *
	 * @param submodelId
	 *            id of the created submodel
	 */
	public void submodelCreated(String submodelId);

	/**
	 * Is called when an submodel is updated
	 *
	 * @param submodelId
	 *            id of the updated submodel
	 */
	public void submodelUpdated(String submodelId);

	/**
	 * Is called when an submodel is deleted
	 *
	 * @param submodelId
	 *            id of the deleted submodel
	 */
	public void submodelDeleted(String submodelId);
}
