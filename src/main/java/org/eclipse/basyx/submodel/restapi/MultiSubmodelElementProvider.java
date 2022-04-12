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
package org.eclipse.basyx.submodel.restapi;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

/**
 * Provider that handles container properties. Container properties can contain other submodel elements.
 *
 * @author espen, conradi
 *
 */
public class MultiSubmodelElementProvider implements IModelProvider {
	// Constants for API-Access
	public static final String ELEMENTS = "submodelElements";
	public static final String VALUE = "value";

	// The VAB model provider containing the submodelElements this SubmodelElementProvider is based on
	// Assumed to be a map that maps idShorts to the submodel elements
	private IModelProvider modelProvider;

	/**
	 * Constructor based on a model provider that contains the container property
	 */
	public MultiSubmodelElementProvider(IModelProvider provider) {
		this.modelProvider = provider;
	}

	/**
	 * The elements are stored in a map {@literal =>} convert them to a list
	 */
	@SuppressWarnings("unchecked")
	protected Collection<Map<String, Object>> getElementsList() {
		Object elements = modelProvider.getValue("");
		Map<String, Map<String, Object>> all = (Map<String, Map<String, Object>>) elements;

		// Feed all ELements through their Providers, in case someting needs to be done to them (e.g. smElemCollections)
		return all.entrySet().stream().map(e -> (Map<String, Object>) getSingleElement(ELEMENTS + "/" + e.getKey())).collect(Collectors.toList());
	}

	/**
	 * Single elements can be directly accessed in maps => return a proxy
	 */
	private IModelProvider getElementProxy(String[] pathElements) {
		String idShort = pathElements[1];
		return new VABElementProxy(idShort, modelProvider);
	}

	private Object getSingleElement(String path) {
		// Build new proxy pointing at sub-property of a submodelelement and forward the
		// remaininig part of the path to an appropriate provider
		String[] pathElements = VABPathTools.splitPath(path);
		String qualifier = pathElements[0];
		IModelProvider elementProxy = getElementProxy(pathElements);

		if (qualifier.equals(ELEMENTS)) {
			String subPath = VABPathTools.buildPath(pathElements, 2);
			return new SubmodelElementProvider(elementProxy).getValue(subPath);
		} else {
			throw new MalformedRequestException("Given path '" + path + "' does not start with /submodelElements");
		}
	}

	@Override
	public Object getValue(String path) throws ProviderException {
		String[] pathElements = VABPathTools.splitPath(path);
		String qualifier = pathElements[0];
		
		if(!qualifier.equals(ELEMENTS)) {
			// No other qualifier in a submodel element container can be directly accessed
			throw new MalformedRequestException("Given path '" + path + "' does not start with /submodelElements");
		}
		
		if (pathElements.length == 1) {
			// returns all elements
			return getElementsList();
		} else {
			// The path requests a single Element
			return getSingleElement(path);
		}
	}

	@Override
	public void setValue(String path, Object newValue) throws ProviderException {
		String[] pathElements = VABPathTools.splitPath(path);
		String qualifier = pathElements[0];
		if (pathElements.length < 2 || !qualifier.equals(ELEMENTS)) {
			// only possible to set values in a data elements, currently
			throw new MalformedRequestException("Given path '" + path + "' is invalid for set");
		}

		IModelProvider elementProxy = getElementProxy(pathElements);
		String subPath = VABPathTools.buildPath(pathElements, 2);
		
		new SubmodelElementProvider(elementProxy).setValue(subPath, newValue);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void createValue(String path, Object newEntity) throws ProviderException {
		String[] pathElements = VABPathTools.splitPath(path);
		String qualifier = pathElements[0];
		String subPath = VABPathTools.buildPath(pathElements, 2);
		

		if (!qualifier.equals(ELEMENTS)) {
			throw new MalformedRequestException("Given path '" + path + "' does not start with /submodelElements");
		}
		
		// Check if the passed element is a SubmodelElementCollection. If yes, the value
		// of the "value" key needs to be handled
		if (SubmodelElementCollection.isSubmodelElementCollection((Map<String, Object>) newEntity)) {
			SubmodelElementCollection smCollection = SubmodelElementCollection.createAsFacade((Map<String, Object>) newEntity);
			newEntity = SubmodelElementMapCollectionConverter.mapToSmECollection(smCollection);
		}

		if (pathElements.length == 2) {
			// It is allowed to overwrite existing properties inside of a submodel
			try {
				modelProvider.setValue(pathElements[1], newEntity);
			} catch (ResourceNotFoundException e) {
				modelProvider.createValue(pathElements[1], newEntity);
			}
		} else {
			IModelProvider elementProxy = getElementProxy(pathElements);
			new SubmodelElementProvider(elementProxy).createValue(subPath, newEntity);
		}
	}

	@Override
	public void deleteValue(String path) throws ProviderException {
		String[] pathElements = VABPathTools.splitPath(path);
		String qualifier = pathElements[0];
		String subPath;
		IModelProvider elementProvider;
		
		if (!qualifier.equals(ELEMENTS)) {
			throw new MalformedRequestException("Given path '" + path + "' does not start with /submodelElements");
		}

		// If the first Element is a Collection, use its Provider
		if(pathElements.length > 2) {
			IModelProvider elementProxy = getElementProxy(pathElements);
			elementProvider = new SubmodelElementProvider(elementProxy); 
			subPath = VABPathTools.buildPath(pathElements, 2);
		} else {
			elementProvider = modelProvider;
			subPath = VABPathTools.buildPath(pathElements, 1);
		}

		// Delete a specific submodel element
		elementProvider.deleteValue(subPath);
	}

	@Override
	public void deleteValue(String path, Object obj) {
		throw new MalformedRequestException("Delete with a passed argument not allowed");
	}

	@Override
	public Object invokeOperation(String path, Object... parameters) throws ProviderException {
		String[] pathElements = VABPathTools.splitPath(path);
		String subPath = VABPathTools.buildPath(pathElements, 2);

		String qualifier = pathElements[0];
		if (!qualifier.equals(ELEMENTS)) {
			throw new MalformedRequestException("Given path '" + path + "' does not start with /submodelElements");
		}
		
		IModelProvider elementProxy = getElementProxy(pathElements);
		return new SubmodelElementProvider(elementProxy).invokeOperation(subPath, parameters);
	}
}
