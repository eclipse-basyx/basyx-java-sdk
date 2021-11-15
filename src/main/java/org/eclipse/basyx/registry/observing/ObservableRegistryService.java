/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/

 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package org.eclipse.basyx.registry.observing;

import java.util.List;

import org.eclipse.basyx.core.observer.Observable;
import org.eclipse.basyx.registry.api.IRegistry;
import org.eclipse.basyx.registry.descriptor.AASDescriptor;
import org.eclipse.basyx.registry.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ProviderException;

/**
 *
 * Implementation of {@link IRegistry} that calls back registered
 * {@link IRegistryServiceObserver} when changes on Registry occur
 *
 * @author haque
 *
 */
public class ObservableRegistryService extends Observable<IRegistryServiceObserver> implements IRegistry {

	private IRegistry registry;

	public ObservableRegistryService(IRegistry registry) {
		this.registry = registry;
	}

	@Override
	public void register(AASDescriptor aasDescriptor) throws ProviderException {
		registry.register(aasDescriptor);
		observers.stream().forEach(o -> o.shellRegistered(aasDescriptor.getIdentifier().getId()));
	}

	@Override
	public void register(SubmodelDescriptor submodelDescriptor) throws ProviderException {
		registry.register(submodelDescriptor);
		observers.stream().forEach(o -> o.shellRegistered(submodelDescriptor.getIdentifier().getId()));
	}

	@Override
	public void updateShell(IIdentifier aasIdentifier, AASDescriptor aasDescriptor) throws ProviderException {
		registry.updateShell(aasIdentifier, aasDescriptor);
		observers.stream().forEach(o -> o.shellUpdated(aasDescriptor.getIdentifier().getId()));
	}

	@Override
	public void updateSubmodel(IIdentifier submodelIdentifier, SubmodelDescriptor submodelDescriptor) throws ProviderException {
		registry.updateSubmodel(submodelIdentifier, submodelDescriptor);
		observers.stream().forEach(o -> o.submodelUpdated(submodelDescriptor.getIdentifier().getId()));
	}

	@Override
	public void registerSubmodelForShell(IIdentifier aasIdentifier, SubmodelDescriptor submodelDescriptor) throws ProviderException {
		registry.registerSubmodelForShell(aasIdentifier, submodelDescriptor);
		observers.stream().forEach(o -> o.submodelRegistered(aasIdentifier, submodelDescriptor.getIdentifier()));
	}

	@Override
	public void updateSubmodelForShell(IIdentifier aasIdentifier, SubmodelDescriptor submodelDescriptor) throws ProviderException {
		registry.updateSubmodelForShell(aasIdentifier, submodelDescriptor);
		observers.stream().forEach(o -> o.submodelUpdated(aasIdentifier, submodelDescriptor.getIdentifier()));
	}

	@Override
	public void deleteShell(IIdentifier shellIdentifier) throws ProviderException {
		registry.deleteShell(shellIdentifier);
		observers.stream().forEach(o -> o.shellDeleted(shellIdentifier.getId()));
	}

	@Override
	public void deleteSubmodel(IIdentifier submodelIdentifier) throws ProviderException {
		registry.deleteSubmodel(submodelIdentifier);
		observers.stream().forEach(o -> o.submodelDeleted(submodelIdentifier));
	}

	@Override
	public void deleteSubmodelFromShell(IIdentifier aasIdentifier, IIdentifier submodelIdentifier) throws ProviderException {
		observers.stream().forEach(o -> o.shellSubmodelDeleted(aasIdentifier, submodelIdentifier));
	}

	@Override
	public AASDescriptor lookupShell(IIdentifier aasIdentifier) throws ProviderException {
		return registry.lookupShell(aasIdentifier);
	}

	@Override
	public List<AASDescriptor> lookupAllShells() throws ProviderException {
		return registry.lookupAllShells();
	}

	@Override
	public List<SubmodelDescriptor> lookupAllSubmodelsForShell(IIdentifier aasIdentifier) throws ProviderException {
		return registry.lookupAllSubmodelsForShell(aasIdentifier);
	}

	@Override
	public SubmodelDescriptor lookupSubmodelForShell(IIdentifier aasIdentifier, IIdentifier submodelIdentifier) throws ProviderException {
		return registry.lookupSubmodelForShell(aasIdentifier, submodelIdentifier);
	}

	@Override
	public SubmodelDescriptor lookupSubmodel(IIdentifier submodelIdentifier) throws ProviderException {
		return registry.lookupSubmodel(submodelIdentifier);
	}

	@Override
	public List<SubmodelDescriptor> lookupAllSubmodels() throws ProviderException {
		return registry.lookupAllSubmodels();
	}

}
