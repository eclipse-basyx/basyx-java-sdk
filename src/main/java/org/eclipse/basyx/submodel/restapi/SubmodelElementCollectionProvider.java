/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.restapi;

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

		// The "value" before the id is needed by the providers lower down in order to handle collections correctly
		// The paths then look like e.g. "submodelElements/collectionID/value/propertyID"
		IModelProvider defaultProvider = new VABElementProxy(
				VABPathTools.concatenatePaths(MultiSubmodelElementProvider.VALUE, idShort), proxy);

		// Wrap the property with idShort into a SubmodelElementProvider and return that provider
		return new SubmodelElementProvider(defaultProvider);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getValue(String path) throws ProviderException {
		path = VABPathTools.stripSlashes(path);
		String[] pathElements = VABPathTools.splitPath(path);

		if (path.isEmpty()) {
			// Convert the internally used Map to a Collection before returning the smECollection
			Map<String, Object> map = (Map<String, Object>) proxy.getValue(path);
			SubmodelElementCollection smElemColl = SubmodelElementCollection.createAsFacade(map);
			return SubmodelElementMapCollectionConverter.smElementToMap(smElemColl);
		} else if(path.equals(MultiSubmodelElementProvider.VALUE)) {
			// Return only a Collection of Elements. Not the internally used Map.
			return SubmodelElementMapCollectionConverter.convertIDMapToCollection(proxy.getValue(path));
		} else {
			// Directly access an element inside of the collection
			String idShort = pathElements[0];
			String subPath = VABPathTools.buildPath(pathElements, 1);
			
			return getElementProvider(idShort).getValue(subPath);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValue(String path, Object newValue) throws ProviderException {
		path = VABPathTools.stripSlashes(path);
		String[] pathElements = VABPathTools.splitPath(path);

		if (path.isEmpty()) {
			// Convert the Collection of Elements to the internally used Map
			Map<String, Object> value =
					SubmodelElementMapCollectionConverter.mapToSmECollection((Map<String, Object>) newValue);
			proxy.setValue(path, value);
		} else if(path.equals(MultiSubmodelElementProvider.VALUE)) {
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
		if (path.isEmpty() || path.equals(MultiSubmodelElementProvider.VALUE)) {
			throw new MalformedRequestException("Path must not be empty or /value");
		} else {
			// If Path contains only one Element, use the proxy directly
			if(pathElements.length == 1) {
				proxy.deleteValue(VABPathTools.concatenatePaths(MultiSubmodelElementProvider.VALUE, path));
			} else {
				// If Path contains more Elements, get the Provider for the first Element in Path
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

		if (path.isEmpty() || path.equals(MultiSubmodelElementProvider.VALUE)) {
			throw new MalformedRequestException("Path must not be empty or /value");
		} else {
			// Directly access an element inside of the collection
			String idShort = pathElements[0];
			String subPath = VABPathTools.buildPath(pathElements, 1);
			return getElementProvider(idShort).invokeOperation(subPath, parameter);
		}
	}
}
