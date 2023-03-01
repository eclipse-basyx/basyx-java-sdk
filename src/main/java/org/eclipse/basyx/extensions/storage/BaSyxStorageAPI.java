/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.extensions.storage;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IIdentifiable;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.facade.submodelelement.SubmodelElementFacadeFactory;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class to create a storage API. The generic type {@code <T>}
 * determines the type of Objects, this API should handle. One API can handle
 * exactly one type of object i.e it manages exactly one data collection within
 * the remote storage.
 * 
 * @author jungjan
 *
 * @param <T>
 *            The type of Objects to be handled
 */
public abstract class BaSyxStorageAPI<T> implements IBaSyxStorageAPI<T> {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected final String COLLECTION_NAME;
	protected final Class<T> TYPE;

	/**
	 * 
	 * @param collectionName
	 *            The name of the collection, managed by this API
	 * @param type
	 *            Must be the exact same type as the type of the generic parameter
	 *            {@code <T>}
	 */
	public BaSyxStorageAPI(String collectionName, Class<T> type) {
		COLLECTION_NAME = collectionName;
		TYPE = type;
	}

	/**
	 * DISCLAIMER: Currently only supports to extract keys from IIdentifiables.
	 * Helper method that extracts a key for persistence storage requests from an
	 * object.
	 * 
	 * @param obj
	 *            An object that contains a key that can be used to find the
	 *            persisted version of the object.
	 * @return The key
	 */
	protected String getKey(T obj) {
		if (!(obj instanceof IIdentifiable)) {
			throw new IllegalArgumentException("Can only extract a key from a object of type " + IIdentifiable.class.getName());
		}
		return ((IIdentifiable) obj).getIdentification().getId();
	}

	/**
	 * Retrieves an object by it's key from the persistence storage.
	 * 
	 * The result of this retrieval will then be further processed by the
	 * {@link #retrieve(String)} method to ensure it is interpreted correctly.
	 * 
	 * @param key
	 *            The key of the object to be retrieved
	 * @return The expected object if successful
	 * @param key
	 * @return
	 */
	public abstract T rawRetrieve(String key);

	/**
	 * Returns a Object that was originally retrieved from the abstract method
	 * {@code rawRetrieve}. If the object to be returned is a submodel type, it will
	 * be updated with a correct interpretation of is's SubmodelElements.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T retrieve(String key) {
		T retrieved = rawRetrieve(key);
		if (retrieved != null && isSubmodelType(retrieved.getClass())) {
			return (T) handleRetrievedSubmodel((Submodel) retrieved);
		}
		return retrieved;
	}

	/**
	 * Helper to bring the SubmodelElements a retrieved SubmodelObject to the
	 * correct Type of ISubmodelElements. Background: SubmodelsElements are
	 * typically retrieved from a persistence storage within a Submodel and come in
	 * the raw form of {@code Map<String, Map<String, Object>>}. This method
	 * processes the raw SubmodelElements to a
	 * {@code Map<String, ISubmodelElement>}.
	 * 
	 * @param retrieved
	 *            The retrieved submodel with raw SubmodelElements
	 * @return An updated version of the submodel with correctly interpreted
	 *         SubmodelElements
	 */
	@SuppressWarnings("unchecked")
	protected Submodel handleRetrievedSubmodel(Submodel retrieved) {
		Map<String, Map<String, Object>> elementMaps = (Map<String, Map<String, Object>>) retrieved.get(Submodel.SUBMODELELEMENT);
		Map<String, ISubmodelElement> elements = forceToISubmodelElements(elementMaps);
		retrieved.put(Submodel.SUBMODELELEMENT, elements);
		return retrieved;
	}

	/**
	 * Converts raw SubmodelElements from a retrieved SubmodelObject to
	 * ISubmodelElements. Background: SubmodelsElements are typically retrieved from
	 * a persistence storage within a Submodel and come in the raw form of
	 * {@code Map<String, Map<String, Object>>}. This method processes the raw
	 * SubmodelElements to a {@code Map<String, ISubmodelElement>}.
	 * 
	 * @param submodelElementObjectMap
	 *            The raw SubmodelElements (typically retrieved within a submdoel ->
	 *            can be get with {@code submodel.get(Submodel.SUBMODELELEMENT)}))
	 * @return A map in the expected form of {@code Map<String, ISubmodelElement>}
	 */
	private Map<String, ISubmodelElement> forceToISubmodelElements(Map<String, Map<String, Object>> submodelElementObjectMap) {
		Map<String, ISubmodelElement> elements = new HashMap<>();

		submodelElementObjectMap.forEach((idShort, elementMap) -> {
			ISubmodelElement element = SubmodelElementFacadeFactory.createSubmodelElement(elementMap);
			elements.put(idShort, element);
		});
		return elements;
	}

	/*
	 * Not yet tested
	 */
	protected boolean isSubmodelType(Class<?> type) {
		return ISubmodel.class.isAssignableFrom(type);
	}

	/*
	 * Not yet tested
	 */
	protected boolean isShellType(Class<?> type) {
		return IAssetAdministrationShell.class.isAssignableFrom(type);
	}

	/*
	 * Not yet tested
	 */
	protected boolean isAASDescriptorType(Class<?> type) {
		return AASDescriptor.class.isAssignableFrom(type);
	}

	/*
	 * Not yet tested
	 */
	protected boolean isBaSyxType(Class<?> type) {
		return (isShellType(type) || isSubmodelType(type) || isAASDescriptorType(type));
	}
}
