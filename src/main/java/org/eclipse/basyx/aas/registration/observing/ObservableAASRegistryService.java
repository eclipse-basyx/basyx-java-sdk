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
 * Implementation of {@link IAASRegistry} that calls back registered
 * {@link IAASRegistryServiceObserver} when changes on Registry occur
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
	public void register(AASDescriptor aasDescriptor) throws ProviderException {
		aasRegistry.register(aasDescriptor);
		observers.stream().forEach(o -> o.aasRegistered(aasDescriptor.getIdentifier().getId()));
	}

	@Override
	public void update(IIdentifier aasIdentifier, AASDescriptor aasDescriptor) throws ProviderException {
		aasRegistry.update(aasIdentifier, aasDescriptor);
		observers.stream().forEach(o -> o.aasUpdated(aasDescriptor.getIdentifier().getId()));
	}

	@Override
	public void register(IIdentifier aasIdentifier, SubmodelDescriptor submodelDescriptor) throws ProviderException {
		aasRegistry.register(aasIdentifier, submodelDescriptor);
		observers.stream().forEach(o -> o.submodelRegistered(aasIdentifier, submodelDescriptor.getIdentifier()));
	}

	@Override
	public void delete(IIdentifier aasIdentifier) throws ProviderException {
		aasRegistry.delete(aasIdentifier);
		observers.stream().forEach(o -> o.aasDeleted(aasIdentifier.getId()));
	}

	@Override
	public void delete(IIdentifier aasIdentifier, IIdentifier submodelIdentifier) throws ProviderException {
		observers.stream().forEach(o -> o.submodelDeleted(aasIdentifier, submodelIdentifier));
	}

	@Override
	public AASDescriptor lookupAAS(IIdentifier aasIdentifier) throws ProviderException {
		return aasRegistry.lookupAAS(aasIdentifier);
	}

	@Override
	public List<AASDescriptor> lookupAll() throws ProviderException {
		return aasRegistry.lookupAll();
	}

	@Override
	public List<SubmodelDescriptor> lookupSubmodels(IIdentifier aasIdentifier) throws ProviderException {
		return aasRegistry.lookupSubmodels(aasIdentifier);
	}

	@Override
	public SubmodelDescriptor lookupSubmodel(IIdentifier aasIdentifier, IIdentifier submodelIdentifier) throws ProviderException {
		return aasRegistry.lookupSubmodel(aasIdentifier, submodelIdentifier);
	}
}
