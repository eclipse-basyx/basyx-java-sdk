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
package org.eclipse.basyx.submodel.restapi.observing;

import java.util.Collection;
import java.util.List;

import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.observer.Observable;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;

/**
 * Implementation of {@link ISubmodelAPI} that calls back registered
 * {@link ISubmodelAPIObserver} when changes on SubmodelElements occur
 * 
 * @author conradi
 *
 */
public class ObservableSubmodelAPIV2 extends Observable<ISubmodelAPIObserverV2> implements ISubmodelAPI {

	ISubmodelAPI submodelAPI;

	public ObservableSubmodelAPIV2(ISubmodelAPI observerdAPI) {
		submodelAPI = observerdAPI;
	}

	@Override
	public ISubmodel getSubmodel() {
		return submodelAPI.getSubmodel();
	}

	@Override
	public void addSubmodelElement(ISubmodelElement elem) {
		submodelAPI.addSubmodelElement(elem);
		observers.stream().forEach(o -> o.elementAdded(elem.getIdShort(), elem, getParentAASId(getSubmodel()), getSubmodel().getIdentification().getId(), submodelAPI.getRepositoryId()));
	}

	@Override
	public void addSubmodelElement(String idShortPath, ISubmodelElement elem) {
		submodelAPI.addSubmodelElement(idShortPath, elem);
		observers.stream().forEach(o -> o.elementAdded(idShortPath, elem, getParentAASId(getSubmodel()), getSubmodel().getIdentification().getId(), submodelAPI.getRepositoryId()));
	}

	@Override
	public ISubmodelElement getSubmodelElement(String idShortPath) {
		return submodelAPI.getSubmodelElement(idShortPath);
	}

	@Override
	public void deleteSubmodelElement(String idShortPath) {
		ISubmodelElement submodelElement = submodelAPI.getSubmodelElement(idShortPath);
		submodelAPI.deleteSubmodelElement(idShortPath);
		observers.stream().forEach(o -> o.elementDeleted(idShortPath, submodelElement, getParentAASId(getSubmodel()), getSubmodel().getIdentification().getId(), submodelAPI.getRepositoryId()));
	}

	@Override
	public Collection<IOperation> getOperations() {
		return submodelAPI.getOperations();
	}

	@Override
	public Collection<ISubmodelElement> getSubmodelElements() {
		return submodelAPI.getSubmodelElements();
	}

	@Override
	public void updateSubmodelElement(String idShortPath, Object newValue) {
		submodelAPI.updateSubmodelElement(idShortPath, newValue);
		ISubmodelElement submodelElement = getSubmodelElement(idShortPath);
		observers.stream().forEach(o -> o.elementUpdated(idShortPath, submodelElement, getParentAASId(getSubmodel()), getSubmodel().getIdentification().getId(), submodelAPI.getRepositoryId()));
	}

	@Override
	public Object getSubmodelElementValue(String idShortPath) {
		Object value = submodelAPI.getSubmodelElementValue(idShortPath);
		observers.stream().forEach(o -> o.elementValue(idShortPath, value, getParentAASId(getSubmodel()), getSubmodel().getIdentification().getId(), submodelAPI.getRepositoryId()));
		
		return value;
	}

	@Override
	public Object invokeOperation(String idShortPath, Object... params) {
		return submodelAPI.invokeOperation(idShortPath, params);
	}

	@Override
	public Object invokeAsync(String idShortPath, Object... params) {
		return submodelAPI.invokeAsync(idShortPath, params);
	}

	@Override
	public Object getOperationResult(String idShort, String requestId) {
		return submodelAPI.getOperationResult(idShort, requestId);
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