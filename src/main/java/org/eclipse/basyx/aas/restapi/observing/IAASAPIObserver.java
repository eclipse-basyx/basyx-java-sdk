/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.restapi.observing;

import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.observer.IObserver;

/**
 * Observer for the AASAPI that triggers MQTT events for different
 * operations on the AAS.
 *
 * @author fried
 *
 */

public interface IAASAPIObserver extends IObserver {

	/**
	 * Is called when a submodel reference is added
	 * 
	 * @param submodel
	 *            the reference to the submodel
	 */
	public void submodelAdded(IReference submodel);

	/**
	 * Is called when a submodel reference is removed
	 * 
	 * @param id
	 *            the idShort of the removed element
	 */
	public void submodelRemoved(String id);
}
