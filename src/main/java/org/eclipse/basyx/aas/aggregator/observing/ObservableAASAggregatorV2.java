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

package org.eclipse.basyx.aas.aggregator.observing;

import java.util.Collection;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.observer.Observable;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

/**
 *
 * Implementation of {@link IAASAggregator} that calls back registered
 * {@link IAASAggregatorObserver} when changes on AAS occur
 *
 * @author haque
 *
 */
public class ObservableAASAggregatorV2 extends Observable<IAASAggregatorObserverV2> implements IAASAggregator {

	private IAASAggregator aasAggregator;

	public ObservableAASAggregatorV2(IAASAggregator aggregator) {
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
	public void createAAS(AssetAdministrationShell aas) {
		aasAggregator.createAAS(aas);
		observers.stream().forEach(o -> o.aasCreated(aas));
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
