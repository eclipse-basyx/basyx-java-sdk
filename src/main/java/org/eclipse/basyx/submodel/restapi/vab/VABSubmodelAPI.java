/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.restapi.vab;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.restapi.MultiSubmodelElementProvider;
import org.eclipse.basyx.submodel.restapi.OperationProvider;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

/**
 * Implements the Submodel API by mapping it to VAB paths
 * 
 * @author schnicke
 *
 */
public class VABSubmodelAPI implements ISubmodelAPI {

	// The VAB model provider containing the model this API implementation is based
	// on
	private IModelProvider modelProvider;

	/**
	 * Creates a VABSubmodelAPI that wraps an IModelProvider
	 * 
	 * @param modelProvider
	 *            providing the Submodel
	 */
	public VABSubmodelAPI(IModelProvider modelProvider) {
		super();
		this.modelProvider = modelProvider;
	}

	/**
	 * Creates an IModelProvider for handling accesses to the elements within the
	 * submodel
	 * 
	 * @return returns the SubmodelElementProvider pointing to the contained
	 *         submodelelements
	 */
	private MultiSubmodelElementProvider getElementProvider() {
		IModelProvider elementProxy = new VABElementProxy(Submodel.SUBMODELELEMENT, modelProvider);
		return new MultiSubmodelElementProvider(elementProxy);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ISubmodel getSubmodel() {
		// For access on the container property root, return the whole model
		Map<String, Object> map = (Map<String, Object>) modelProvider.getValue("");

		// Only return a copy of the Submodel
		Map<String, Object> smCopy = new HashMap<>();
		smCopy.putAll(map);
		return Submodel.createAsFacade(smCopy);
	}

	@Override
	public void addSubmodelElement(ISubmodelElement elem) {
		getElementProvider().createValue(MultiSubmodelElementProvider.ELEMENTS + "/" + elem.getIdShort(), elem);
	}

	@Override
	public void addSubmodelElement(String idShortPath, ISubmodelElement elem) {
		getElementProvider().createValue(MultiSubmodelElementProvider.ELEMENTS + "/" + idShortPath, elem);
	}

	@Override
	public void deleteSubmodelElement(String idShortPath) {
		getElementProvider().deleteValue(MultiSubmodelElementProvider.ELEMENTS + "/" + idShortPath);
	}


	@Override
	public Collection<IOperation> getOperations() {
		return getSubmodelElements().stream().filter(e -> e instanceof IOperation).map(e -> (IOperation) e).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<ISubmodelElement> getSubmodelElements() {
		Collection<Map<String, Object>> elements = (Collection<Map<String, Object>>) getElementProvider()
				.getValue(MultiSubmodelElementProvider.ELEMENTS);
		return elements.stream().map(SubmodelElement::createAsFacade).collect(Collectors.toList());
	}

	@Override
	public void updateSubmodelElement(String idShortPath, Object newValue) {
		getElementProvider().setValue(buildValuePathForProperty(idShortPath), newValue);
	}

	@Override
	public Object getSubmodelElementValue(String idShortPath) {
		return getElementProvider().getValue(buildValuePathForProperty(idShortPath));
	}

	@SuppressWarnings("unchecked")
	@Override
	public ISubmodelElement getSubmodelElement(String idShortPath) {
		return SubmodelElement.createAsFacade((Map<String, Object>) getElementProvider().getValue(MultiSubmodelElementProvider.ELEMENTS + "/" + idShortPath));
	}

	@Override
	public Object invokeOperation(String idShortPath, Object... params) {
		return getElementProvider().invokeOperation(MultiSubmodelElementProvider.ELEMENTS + "/" + idShortPath, params);
	}
	
	
	@Override
	public Object invokeAsync(String idShortPath, Object... params) {
		return getElementProvider().invokeOperation(MultiSubmodelElementProvider.ELEMENTS + "/" + idShortPath +"/" + Operation.INVOKE + OperationProvider.ASYNC, params);
	}

	private String buildValuePathForProperty(String idShortPath) {
		return MultiSubmodelElementProvider.ELEMENTS + "/" + idShortPath + "/" + Property.VALUE;
	}

	
	@Override
	public Object getOperationResult(String idShortPath, String requestId) {
		return getElementProvider().getValue(MultiSubmodelElementProvider.ELEMENTS + "/" + idShortPath + "/" + OperationProvider.INVOCATION_LIST + "/" + requestId);
	}


}
