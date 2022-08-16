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
public class ObservableSubmodelAggregatorV2 extends Observable<ISubmodelAggregatorObserverV2> implements ISubmodelAggregator {

	private ISubmodelAggregator submodelAggregator;

	public ObservableSubmodelAggregatorV2(ISubmodelAggregator aggregator) {
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
		observers.stream().forEach(observer -> observer.submodelCreated(getParentAASId(submodel), submodel, submodelAggregator.getRepositoryId()));
	}

	@Override
	public void createSubmodel(ISubmodelAPI submodelAPI) {
		submodelAggregator.createSubmodel(submodelAPI);
		observers.stream().forEach(observer -> observer.submodelCreated(getParentAASId(submodelAPI.getSubmodel()), submodelAPI.getSubmodel(), submodelAggregator.getRepositoryId()));
	}

	@Override
	public void updateSubmodel(Submodel submodel) throws ResourceNotFoundException {
		submodelAggregator.updateSubmodel(submodel);
		observers.stream().forEach(observer -> observer.submodelUpdated(getParentAASId(submodel), submodel, submodelAggregator.getRepositoryId()));
	}

	@Override
	public void deleteSubmodelByIdentifier(IIdentifier submodelIdentifier) {
		ISubmodel submodel = submodelAggregator.getSubmodel(submodelIdentifier);
		String parentAASId = getParentAASId(submodel);
		submodelAggregator.deleteSubmodelByIdentifier(submodelIdentifier);
		observers.stream().forEach(observer -> observer.submodelDeleted(parentAASId, submodel, submodelAggregator.getRepositoryId()));
	}

	@Override
	public void deleteSubmodelByIdShort(String submodelIdShort) {
		ISubmodel submodel = submodelAggregator.getSubmodelbyIdShort(submodelIdShort);
		String parentAASId = getParentAASId(submodel);
		submodelAggregator.deleteSubmodelByIdShort(submodelIdShort);
		observers.stream().forEach(observer -> observer.submodelDeleted(parentAASId, submodel, submodelAggregator.getRepositoryId()));
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
