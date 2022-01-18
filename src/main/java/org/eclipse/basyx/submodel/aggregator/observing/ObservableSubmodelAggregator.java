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

import java.util.Collection;
import java.util.List;

import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.observer.Observable;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;

/**
 *
 * Implementation of {@link ISubmodelAggregator} that calls back registered
 * {@link ISubmodelAggregatorObserver} when changes on Submodel occur
 *
 * @author fischer, jungjan, fried
 *
 */
public class ObservableSubmodelAggregator extends Observable<ISubmodelAggregatorObserver> implements ISubmodelAggregator {

	private ISubmodelAggregator submodelAggregator;

	public ObservableSubmodelAggregator(ISubmodelAggregator aggregator) {
		this.submodelAggregator = aggregator;
	}

	@Override
	public Collection<ISubmodel> getSubmodelList() {
		return submodelAggregator.getSubmodelList();
	}

	@Override
	public ISubmodel getSubmodel(IIdentifier submodelIdentifier) throws ResourceNotFoundException {
		return submodelAggregator.getSubmodel(submodelIdentifier);
	}

	@Override
	public ISubmodel getSubmodelbyIdShort(String submodelIdShort) throws ResourceNotFoundException {
		return submodelAggregator.getSubmodelbyIdShort(submodelIdShort);
	}

	@Override
	public ISubmodelAPI getSubmodelAPIById(IIdentifier submodelIdentifier) throws ResourceNotFoundException {
		return submodelAggregator.getSubmodelAPIById(submodelIdentifier);
	}

	@Override
	public ISubmodelAPI getSubmodelAPIByIdShort(String submodelIdShort) throws ResourceNotFoundException {
		return submodelAggregator.getSubmodelAPIByIdShort(submodelIdShort);
	}

	@Override
	public void createSubmodel(Submodel submodel) {
		submodelAggregator.createSubmodel(submodel);
		observers.stream().forEach(observer -> observer.submodelCreated(getParentAASId(submodel), submodel.getIdentification().getId()));
	}

	@Override
	public void createSubmodel(ISubmodelAPI submodelAPI) {
		submodelAggregator.createSubmodel(submodelAPI);
		observers.stream().forEach(observer -> observer.submodelCreated(getParentAASId(submodelAPI.getSubmodel()), submodelAPI.getSubmodel().getIdentification().getId()));
	}

	@Override
	public void updateSubmodel(Submodel submodel) throws ResourceNotFoundException {
		submodelAggregator.updateSubmodel(submodel);
		observers.stream().forEach(observer -> observer.submodelUpdated(getParentAASId(submodel), submodel.getIdentification().getId()));
	}

	@Override
	public void deleteSubmodelByIdentifier(IIdentifier submodelIdentifier) {
		String parentAASId = getParentAASId(submodelAggregator.getSubmodel(submodelIdentifier));
		submodelAggregator.deleteSubmodelByIdentifier(submodelIdentifier);
		observers.stream().forEach(observer -> observer.submodelDeleted(parentAASId, submodelIdentifier.getId()));
	}

	@Override
	public void deleteSubmodelByIdShort(String submodelIdShort) {
		ISubmodel submodel = submodelAggregator.getSubmodelbyIdShort(submodelIdShort);
		String parentAASId = getParentAASId(submodel);
		submodelAggregator.deleteSubmodelByIdShort(submodelIdShort);
		observers.stream().forEach(observer -> observer.submodelDeleted(parentAASId, submodel.getIdentification().getId()));
	}

	private String getParentAASId(ISubmodel submodel) {
		IReference parentReference = submodel.getParent();
		if (parentReference == null) {
			return null;
		}
		List<IKey> keys = parentReference.getKeys();
		if (keys != null && keys.size() > 0) {
			return keys.get(0).getValue();
		}
		return null;
	}
}
