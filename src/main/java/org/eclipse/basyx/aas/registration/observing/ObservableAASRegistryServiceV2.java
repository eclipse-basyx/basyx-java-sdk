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

import java.util.List;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.observer.Observable;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;

/**
 *
 * Implementation of {@link IAASRegistry} that calls back registered
 * {@link IAASRegistryServiceObserver} when changes on Registry occur
 *
 * @author haque
 *
 */
public class ObservableAASRegistryServiceV2 extends Observable<IAASRegistryServiceObserverV2> implements IAASRegistry {

	private IAASRegistry aasRegistry;

	public ObservableAASRegistryServiceV2(IAASRegistry registry) {
		this.aasRegistry = registry;
	}

	@Override
	public void register(AASDescriptor deviceAASDescriptor) throws ProviderException {
		try {
			aasRegistry.lookupAAS(deviceAASDescriptor.getIdentifier());
			aasRegistry.register(deviceAASDescriptor);
			observers.stream().forEach(o -> o.aasUpdated(deviceAASDescriptor, aasRegistry.getRegistryId()));
		} catch(ResourceNotFoundException e) {
			aasRegistry.register(deviceAASDescriptor);
			observers.stream().forEach(o -> o.aasRegistered(deviceAASDescriptor, aasRegistry.getRegistryId()));
		}
	}

	@Override
	public void register(IIdentifier aas, SubmodelDescriptor smDescriptor) throws ProviderException {
		
		try {
			aasRegistry.lookupSubmodel(aas, smDescriptor.getIdentifier());
			aasRegistry.register(aas, smDescriptor);
			observers.stream().forEach(o -> o.submodelUpdated(aas, smDescriptor, aasRegistry.getRegistryId()));
		} catch(ResourceNotFoundException e) {			
			aasRegistry.register(aas, smDescriptor);
			observers.stream().forEach(o -> o.submodelRegistered(aas, smDescriptor, aasRegistry.getRegistryId()));
		}
	}

	@Override
	public void delete(IIdentifier aasId) throws ProviderException {
		AASDescriptor aasDescriptor = lookupAAS(aasId);
		aasRegistry.delete(aasId);
		observers.stream().forEach(o -> o.aasDeleted(aasDescriptor, aasRegistry.getRegistryId()));
	}

	@Override
	public void delete(IIdentifier aasId, IIdentifier smId) throws ProviderException {
		SubmodelDescriptor smDescriptor = lookupSubmodel(aasId, smId);
		aasRegistry.delete(aasId, smId);
		observers.stream().forEach(o -> o.submodelDeleted(aasId, smDescriptor, aasRegistry.getRegistryId()));
	}

	@Override
	public AASDescriptor lookupAAS(IIdentifier aasId) throws ProviderException {
		return aasRegistry.lookupAAS(aasId);
	}

	@Override
	public List<AASDescriptor> lookupAll() throws ProviderException {
		return aasRegistry.lookupAll();
	}

	@Override
	public List<SubmodelDescriptor> lookupSubmodels(IIdentifier aasId) throws ProviderException {
		return aasRegistry.lookupSubmodels(aasId);
	}

	@Override
	public SubmodelDescriptor lookupSubmodel(IIdentifier aasId, IIdentifier smId) throws ProviderException {
		return aasRegistry.lookupSubmodel(aasId, smId);
	}

}
