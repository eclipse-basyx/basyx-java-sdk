/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

* 
* SPDX-License-Identifier: MIT
******************************************************************************/

package org.eclipse.basyx.aas.registration.observing;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.observer.IObserver;

/**
 * Interface for an observer of {@link ObservableAASRegistryService}
 * 
 * @author haque
 *
 */
public interface IAASRegistryServiceObserverV2 extends IObserver {

	/**
	 * Is called when an AAS is registered
	 * 
	 * @param aasDescriptor
	 *            descriptor of the registered AAS
	 */
	public void aasRegistered(AASDescriptor aasDescriptor, String registryId);

	/**
	 * Is called when a submodel is registered
	 * 
	 * @param smDescriptor
	 *            descriptor of the registered submodel
	 */
	public void submodelRegistered(SubmodelDescriptor smDescriptor, String registryId);

	/**
	 * Is called when an AAS is deleted
	 * 
	 * @param aasDescriptor
	 *            descriptor of the deleted AAS
	 */
	public void aasDeleted(AASDescriptor aasDescriptor, String registryId);

	/**
	 * Is called when a submodel is deleted
	 * 
	 * @param aasId
	 *            id of the parent AAS
	 * @param smId
	 *            id of the deleted Submodel
	 */
	public void submodelDeleted(IIdentifier aasId, IIdentifier smId, String registryId);

}
