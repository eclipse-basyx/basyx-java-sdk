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

import java.util.List;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.observer.Observable;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ProviderException;

/**
*
* Implementation of {@link IAASRegistry} that calls back registered {@link IAASRegistryServiceObserver}
* when changes on Registry occur
*
* @author haque
*
*/
public class ObservableAASRegistryService extends Observable<IAASRegistryServiceObserver> implements IAASRegistry {

	private IAASRegistry aasRegistry;

	public ObservableAASRegistryService(IAASRegistry registry) {
		this.aasRegistry = registry;
	}

	@Override
	public void register(AASDescriptor deviceAASDescriptor) throws ProviderException {
		aasRegistry.register(deviceAASDescriptor);
		observers.stream().forEach(o -> o.aasRegistered(deviceAASDescriptor.getIdentifier().getId()));
	}

	@Override
	public void register(IIdentifier aas, SubmodelDescriptor smDescriptor) throws ProviderException {
		aasRegistry.register(aas, smDescriptor);
		observers.stream().forEach(o -> o.submodelRegistered(aas, smDescriptor.getIdentifier()));
	}

	@Override
	public void delete(IIdentifier aasId) throws ProviderException {
		aasRegistry.delete(aasId);
		observers.stream().forEach(o -> o.aasDeleted(aasId.getId()));
	}

	@Override
	public void delete(IIdentifier aasId, IIdentifier smId) throws ProviderException {
		observers.stream().forEach(o -> o.submodelDeleted(aasId, smId));
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
