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
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;

/**
 * Implementation of {@link ISubmodelAPI} that calls back registered
 * {@link ISubmodelAPIObserverV2} when changes on SubmodelElements occur
 * 
 * @author conradi, siebert
 *
 */
public class ObservableSubmodelAPIV2 extends Observable<ISubmodelAPIObserverV2> implements ISubmodelAPI {

	ISubmodelAPI submodelAPI;
	private String aasServerId = "aas-server";

	/**
	 * Constructs an observable ISubmodelAPI wrapping an existing ISubmodelAPI
	 * 
	 * @param observerdAPI
	 * @param aasServerId
	 */
	public ObservableSubmodelAPIV2(ISubmodelAPI observerdAPI, String aasServerId) {
		submodelAPI = observerdAPI;
		this.aasServerId = aasServerId;		
	}
	
	@Override
	public ISubmodel getSubmodel() {
		return submodelAPI.getSubmodel();
	}

	@Override
	public void addSubmodelElement(ISubmodelElement elem) {
		String idShortPath = elem.getIdShort();

		addOrReplaceSubmodelElement(idShortPath, elem);
	}

	private void addOrReplaceSubmodelElement(String idShortPath, ISubmodelElement elem) {
		// There is no replace possibility in the ISubmodelAPI for submodel elements.
		// Thus, a check for existence is performed to determine if an existing submodel
		// element is overwritten.
		try {
			submodelAPI.getSubmodelElement(idShortPath);
			submodelAPI.addSubmodelElement(idShortPath, elem);
			observers.stream().forEach(o -> o.elementUpdated(idShortPath, elem, getParentAASId(getSubmodel()), getSubmodel().getIdentification().getId(), this.aasServerId));
		} catch (ResourceNotFoundException e) {
			submodelAPI.addSubmodelElement(idShortPath, elem);
			observers.stream().forEach(o -> o.elementAdded(idShortPath, elem, getParentAASId(getSubmodel()), getSubmodel().getIdentification().getId(), this.aasServerId));
		}
	}

	@Override
	public void addSubmodelElement(String idShortPath, ISubmodelElement elem) {
		addOrReplaceSubmodelElement(idShortPath, elem);
	}

	@Override
	public ISubmodelElement getSubmodelElement(String idShortPath) {
		return submodelAPI.getSubmodelElement(idShortPath);
	}

	@Override
	public void deleteSubmodelElement(String idShortPath) {
		ISubmodelElement submodelElement = submodelAPI.getSubmodelElement(idShortPath);
		submodelAPI.deleteSubmodelElement(idShortPath);
		observers.stream().forEach(o -> o.elementDeleted(idShortPath, submodelElement, getParentAASId(getSubmodel()), getSubmodel().getIdentification().getId(), this.aasServerId));
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

		ISubmodelElement elem = submodelAPI.getSubmodelElement(idShortPath);

		Object valueToSend;
		if (ObserableSubmodelAPIV2Helper.shouldSendEmptyValue(elem)) {
			valueToSend = null;
		} else {
			valueToSend = newValue;
		}

		observers.stream().forEach(o -> o.elementValue(idShortPath, valueToSend, getParentAASId(getSubmodel()), getSubmodel().getIdentification().getId(), this.aasServerId));
	}
	

	@Override
	public Object getSubmodelElementValue(String idShortPath) {
		return submodelAPI.getSubmodelElementValue(idShortPath);
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
