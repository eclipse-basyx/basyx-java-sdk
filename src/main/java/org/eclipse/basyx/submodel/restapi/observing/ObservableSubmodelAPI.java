/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/
package org.eclipse.basyx.submodel.restapi.observing;

import java.util.Collection;

import org.eclipse.basyx.aas.observer.Observable;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;

/**
 * Implementation of {@link ISubmodelAPI} that calls back registered {@link ISubmodelAPIObserver}
 * when changes on SubmodelElements occur
 * 
 * @author conradi
 *
 */
public class ObservableSubmodelAPI extends Observable<ISubmodelAPIObserver> implements ISubmodelAPI {

	ISubmodelAPI submodelAPI;

	public ObservableSubmodelAPI(ISubmodelAPI observerdAPI) {
		submodelAPI = observerdAPI;
	}
	
	@Override
	public ISubmodel getSubmodel() {
		return submodelAPI.getSubmodel();
	}

	@Override
	public void addSubmodelElement(ISubmodelElement elem) {
		submodelAPI.addSubmodelElement(elem);
		observers.stream().forEach(o -> o.elementAdded(elem.getIdShort(), elem.getValue()));
	}

	@Override
	public void addSubmodelElement(String idShortPath, ISubmodelElement elem) {
		submodelAPI.addSubmodelElement(idShortPath, elem);
		observers.stream().forEach(o -> o.elementAdded(idShortPath, elem.getValue()));
	}

	@Override
	public ISubmodelElement getSubmodelElement(String idShortPath) {
		return submodelAPI.getSubmodelElement(idShortPath);
	}

	@Override
	public void deleteSubmodelElement(String idShortPath) {
		submodelAPI.deleteSubmodelElement(idShortPath);
		observers.stream().forEach(o -> o.elementDeleted(idShortPath));
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
		observers.stream().forEach(o -> o.elementUpdated(idShortPath, newValue));
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

}
