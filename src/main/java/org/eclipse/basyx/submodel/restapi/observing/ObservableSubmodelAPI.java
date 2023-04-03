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

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
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

	@Override
	public File getSubmodelElementFile(String idShortPath) {
		return submodelAPI.getSubmodelElementFile(idShortPath);
	}

	@Override
	public void uploadSubmodelElementFile(String idShortPath, InputStream fileStream) {
		submodelAPI.uploadSubmodelElementFile(idShortPath, fileStream);
	}

}
