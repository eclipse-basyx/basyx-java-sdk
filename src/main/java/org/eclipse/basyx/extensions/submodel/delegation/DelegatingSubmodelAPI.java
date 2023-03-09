/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.extensions.submodel.delegation;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.Optional;

import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IConstraint;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;

/**
 * Implementation variant for the SubmodelAPI which handles the Submodels
 * with delegated Property
 *
 * @author danish
 *
 */
public class DelegatingSubmodelAPI implements ISubmodelAPI {
	private ISubmodelAPI decoratedSubmodelAPI;
	private PropertyDelegationManager delegationProvider;

	public DelegatingSubmodelAPI(ISubmodelAPI decoratedSubmodelAPI, PropertyDelegationManager delegationManager) {
		this.delegationProvider = delegationManager;
		this.decoratedSubmodelAPI = decoratedSubmodelAPI;
		
		handleSubmodel(decoratedSubmodelAPI);
	}

	@Override
	public ISubmodel getSubmodel() {
		return decoratedSubmodelAPI.getSubmodel();
	}

	@Override
	public void addSubmodelElement(ISubmodelElement elem) {
		delegationProvider.handleSubmodelElement((SubmodelElement) elem);
		decoratedSubmodelAPI.addSubmodelElement(elem);
	}

	@Override
	public void addSubmodelElement(String idShortPath, ISubmodelElement elem) {
		delegationProvider.handleSubmodelElement((SubmodelElement) elem);
		decoratedSubmodelAPI.addSubmodelElement(idShortPath, elem);
	}

	@Override
	public ISubmodelElement getSubmodelElement(String idShortPath) {
		return decoratedSubmodelAPI.getSubmodelElement(idShortPath);
	}

	@Override
	public void deleteSubmodelElement(String idShortPath) {
		decoratedSubmodelAPI.deleteSubmodelElement(idShortPath);
	}

	@Override
	public Collection<IOperation> getOperations() {
		return decoratedSubmodelAPI.getOperations();
	}

	@Override
	public Collection<ISubmodelElement> getSubmodelElements() {
		return decoratedSubmodelAPI.getSubmodelElements();
	}

	@Override
	public void updateSubmodelElement(String idShortPath, Object newValue) {
		handleUpdateSubmodelElementValueRequest((SubmodelElement) this.decoratedSubmodelAPI.getSubmodelElement(idShortPath));
		decoratedSubmodelAPI.updateSubmodelElement(idShortPath, newValue);
	}

	@Override
	public Object getSubmodelElementValue(String idShortPath) {
		return decoratedSubmodelAPI.getSubmodelElementValue(idShortPath);
	}

	@Override
	public Object invokeOperation(String idShortPath, Object... params) {
		return decoratedSubmodelAPI.invokeOperation(idShortPath, params);
	}

	@Override
	public Object invokeAsync(String idShortPath, Object... params) {
		return decoratedSubmodelAPI.invokeAsync(idShortPath, params);
	}

	@Override
	public Object getOperationResult(String idShort, String requestId) {
		return decoratedSubmodelAPI.getOperationResult(idShort, requestId);
	}
	
	private void handleSubmodel(ISubmodelAPI decoratedSubmodelAPI) {
		ISubmodel submodel = decoratedSubmodelAPI.getSubmodel();
		delegationProvider.handleSubmodel(submodel);

		submodel.getSubmodelElements().values().stream().forEach(decoratedSubmodelAPI::addSubmodelElement);
	}
	
	private void handleUpdateSubmodelElementValueRequest(SubmodelElement submodelElement) {
		if (!(Property.isProperty(submodelElement)))
			return;

		throwAnExceptionIfDelegatedQualifierIsPresent(submodelElement);
	}

	private void throwAnExceptionIfDelegatedQualifierIsPresent(ISubmodelElement element) {
		Collection<IConstraint> qualifiers = element.getQualifiers();
		Optional<IConstraint> optionalConstraint = qualifiers.stream().filter(PropertyDelegationManager::isDelegationQualifier).findAny();

		if (!optionalConstraint.isEmpty())
			throw new MalformedRequestException("The update request on this SubmodelElement " + element.getIdShort() + " with delegated qualifier is not allowed");
	}

	@Override
	public Object getSubmodelElementFile(String idShortPath) {
		return decoratedSubmodelAPI.getSubmodelElementFile(idShortPath);
	}

	@Override
	public void uploadSubmodelElementFile(String idShortPath, FileInputStream fileStream) {
		decoratedSubmodelAPI.uploadSubmodelElementFile(idShortPath, fileStream);
	}
}
