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

import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

/**
 * Handles access to SubmodelElementCollections.
 *
 * @author espen, conradi
 */
public class SubmodelElementCollectionProvider implements IModelProvider {

	private IModelProvider proxy;

	public SubmodelElementCollectionProvider(IModelProvider proxy) {
		this.proxy = proxy;
	}

	/**
	 * Get a single smElement for a given idShort and return a provider for it
	 */
	protected IModelProvider getElementProvider(String idShort) {

		// The "value" before the id is needed by the providers lower down in order to
		// handle collections correctly
		// The paths then look like e.g.
		// "submodelElements/collectionID/value/propertyID"
		IModelProvider defaultProvider = new VABElementProxy(VABPathTools.concatenatePaths(MultiSubmodelElementProvider.VALUE, idShort), proxy);

		// Wrap the property with idShort into a SubmodelElementProvider and return that
		// provider
		return new SubmodelElementProvider(defaultProvider);
	}

	@Override
	public Object getValue(String path) throws ProviderException {
		path = VABPathTools.stripSlashes(path);
		String[] pathElements = VABPathTools.splitPath(path);

		if (path.isEmpty()) {
			return getSubmodelElementCollection();
		} else if (isValueAccess(path)) {
			return getElements();
		} else if (isValuesAccess(path)) {
			return getElementsValues();
		} else {
			return getElementByIdShort(pathElements);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValue(String path, Object newValue) throws ProviderException {
		path = VABPathTools.stripSlashes(path);
		String[] pathElements = VABPathTools.splitPath(path);

		if (path.isEmpty()) {
			// Convert the Collection of Elements to the internally used Map
			Map<String, Object> value = SubmodelElementMapCollectionConverter.mapToSmECollection((Map<String, Object>) newValue);
			proxy.setValue(path, value);
		} else if (isValueAccess(path)) {
			// Convert the Collection of Elements to the internally used Map
			Map<String, Object> value = SubmodelElementMapCollectionConverter.convertCollectionToIDMap(newValue);
			proxy.setValue(path, value);
		} else {
			// Directly access an element inside of the collection
			String idShort = pathElements[0];
			String subPath = VABPathTools.buildPath(pathElements, 1);
			getElementProvider(idShort).setValue(subPath, newValue);
		}
	}

	@Override
	public void createValue(String path, Object newEntity) throws ProviderException {
		path = VABPathTools.stripSlashes(path);
		String[] pathElements = VABPathTools.splitPath(path);

		if (pathElements.length == 1) {
			String valuePath = VABPathTools.concatenatePaths(MultiSubmodelElementProvider.VALUE, path);
			// It is allowed to overwrite existing properties inside of collections
			try {
				proxy.setValue(valuePath, newEntity);
			} catch (ResourceNotFoundException e) {
				proxy.createValue(valuePath, newEntity);
			}
		} else {
			// Directly access an element inside of the collection
			String idShort = pathElements[0];
			String subPath = VABPathTools.buildPath(pathElements, 1);
			getElementProvider(idShort).createValue(subPath, newEntity);
		}
	}

	@Override
	public void deleteValue(String path) throws ProviderException {
		path = VABPathTools.stripSlashes(path);
		String[] pathElements = VABPathTools.splitPath(path);

		// "value" is a keyword and can not be used as the ID of an Element
		if (path.isEmpty() || isValueAccess(path)) {
			throw new MalformedRequestException("Path must not be empty or /value");
		} else {
			// If Path contains only one Element, use the proxy directly
			if (pathElements.length == 1) {
				proxy.deleteValue(VABPathTools.concatenatePaths(MultiSubmodelElementProvider.VALUE, path));
			} else {
				// If Path contains more Elements, get the Provider for the first Element in
				// Path
				String idShort = pathElements[0];
				String subPath = VABPathTools.buildPath(pathElements, 1);
				getElementProvider(idShort).deleteValue(subPath);
			}
		}
	}

	@Override
	public void deleteValue(String path, Object obj) throws ProviderException {
		throw new MalformedRequestException("Delete with a passed argument not allowed");
	}

	@Override
	public Object invokeOperation(String path, Object... parameter) throws ProviderException {
		path = VABPathTools.stripSlashes(path);
		String[] pathElements = VABPathTools.splitPath(path);

		if (path.isEmpty() || isValueAccess(path)) {
			throw new MalformedRequestException("Path must not be empty or /value");
		} else {
			// Directly access an element inside of the collection
			String idShort = pathElements[0];
			String subPath = VABPathTools.buildPath(pathElements, 1);
			return getElementProvider(idShort).invokeOperation(subPath, parameter);
		}
	}

	private boolean isValueAccess(String path) {
		return path.equals(MultiSubmodelElementProvider.VALUE);
	}

	private boolean isValuesAccess(String path) {
		return path.equals(SubmodelProvider.VALUES);
	}

	/**
	 * Gets single element by idShort
	 * 
	 * @param pathElements
	 *            containing idShort as the first item
	 * @return
	 */
	private Object getElementByIdShort(String[] pathElements) {
		String idShort = pathElements[0];
		String subPath = VABPathTools.buildPath(pathElements, 1);

		return getElementProvider(idShort).getValue(subPath);
	}

	/**
	 * Gets elements values from the proxy Converts the internally used Map to a
	 * Collection before returning the smECollection
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> getElementsValues() {
		Map<String, Object> map = (Map<String, Object>) proxy.getValue("");
		SubmodelElementCollection smElemColl = SubmodelElementCollection.createAsFacade(map);
		return smElemColl.getValues();
	}

	/**
	 * Gets element collection from the proxy
	 * 
	 * @return Only a Collection of Elements. Not the internally used Map.
	 */
	private Collection<Map<String, Object>> getElements() {
		return SubmodelElementMapCollectionConverter.convertIDMapToCollection(proxy.getValue(MultiSubmodelElementProvider.VALUE));
	}

	/**
	 * Gets Submodel Element Collection from the proxy Converts the internally used
	 * Map to a Collection before returning the smECollection
	 * 
	 * @return map of the SMElementCollection
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> getSubmodelElementCollection() {
		Map<String, Object> map = (Map<String, Object>) proxy.getValue("");
		SubmodelElementCollection smElemColl = SubmodelElementCollection.createAsFacade(map);
		return SubmodelElementMapCollectionConverter.smElementToMap(smElemColl);
	}
}
