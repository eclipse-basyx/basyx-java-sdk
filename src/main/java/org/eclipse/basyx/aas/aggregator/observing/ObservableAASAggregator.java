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

import java.util.Collection;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.observer.Observable;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

/**
 *
 * Implementation of {@link IAASAggregator} that calls back registered {@link IAASAggregatorObserver}
 * when changes on AAS occur
 *
 * @author haque
 *
 */
public class ObservableAASAggregator extends Observable<IAASAggregatorObserver> implements IAASAggregator {

	private IAASAggregator aasAggregator;
	
	public ObservableAASAggregator(IAASAggregator aggregator) {
		this.aasAggregator = aggregator;
	}
	
	@Override
	public Collection<IAssetAdministrationShell> getAASList() {
		return aasAggregator.getAASList();
	}

	@Override
	public IAssetAdministrationShell getAAS(IIdentifier aasId) throws ResourceNotFoundException {
		return aasAggregator.getAAS(aasId);
	}

	@Override
	public IModelProvider getAASProvider(IIdentifier aasId) throws ResourceNotFoundException {
		return aasAggregator.getAASProvider(aasId);
	}

	@Override
	public void createShell(AssetAdministrationShell aas) {
		aasAggregator.createShell(aas);
		observers.stream().forEach(o -> o.aasCreated(aas.getIdentification().getId()));
	}

	@Override
	public void updateAAS(AssetAdministrationShell aas) throws ResourceNotFoundException {
		aasAggregator.updateAAS(aas);
		observers.stream().forEach(o -> o.aasUpdated(aas.getIdentification().getId()));
	}

	@Override
	public void deleteAAS(IIdentifier aasId) {
		aasAggregator.deleteAAS(aasId);
		observers.stream().forEach(o -> o.aasDeleted(aasId.getId()));
	}
}
