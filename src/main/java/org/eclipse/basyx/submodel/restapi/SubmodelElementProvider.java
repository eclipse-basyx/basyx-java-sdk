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

import java.io.FileInputStream;
import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.facade.submodelelement.SubmodelElementFacadeFactory;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

/**
 * Handles a SubmodelElement according to AAS meta model
 *
 * @author schnicke, conradi
 *
 */
public class SubmodelElementProvider implements IModelProvider {

	private IModelProvider proxy;

	// Flag used to indicate whether a specialized ElementProvider is used
	private boolean specializedProvider = false;

	public SubmodelElementProvider(IModelProvider proxy) {
		IModelProvider unchangedProxy = proxy;
		this.proxy = getElementProvider(proxy);
		// if the returned element provider is the same, no specialized provider exists
		specializedProvider = unchangedProxy != this.proxy;
	}

	/**
	 * Used to find out if an Element needs a specialized Provider (Collection,
	 * Operation)
	 * 
	 * @param proxy
	 *            the Provider given from above
	 * @return either the unchanged Provider or the Provider nested into a
	 *         specialized ElementProvider
	 */
	@SuppressWarnings("unchecked")
	public static IModelProvider getElementProvider(IModelProvider proxy) {
		Map<String, Object> elementMap = (Map<String, Object>) proxy.getValue("");
		if (Operation.isOperation(elementMap)) {
			return new OperationProvider(proxy);
		} else if (SubmodelElementCollection.isSubmodelElementCollection(elementMap)) {
			return new SubmodelElementCollectionProvider(proxy);
		} else if (Property.isProperty(elementMap)) {
			return new PropertyProvider(proxy);
		} else {
			return proxy;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getValue(String path) throws ProviderException {
		path = VABPathTools.stripSlashes(path);

		if (path.equals(MultiSubmodelElementProvider.VALUE)) {
			// Handle "/value" path
			// return value

			if (specializedProvider) {
				return proxy.getValue(path);
			}

			Map<String, Object> elementMap = (Map<String, Object>) proxy.getValue("");

			ISubmodelElement element = SubmodelElementFacadeFactory.createSubmodelElement(elementMap);

			try {
				return element.getValue();
			} catch (UnsupportedOperationException e) {
				// e.g. an Operation
				throw new MalformedRequestException("The requested Element '" + element.getIdShort() + "' has no value.");
			}
		} else {
			// Path has more Elements -> pass it to Provider below
			return proxy.getValue(path);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValue(String path, Object newValue) throws ProviderException {
		path = VABPathTools.stripSlashes(path);

		if (!path.endsWith(MultiSubmodelElementProvider.VALUE)) {
			throw new MalformedRequestException("The given path '" + path + "' does not end in /value.");
		}

		if (!specializedProvider && path.equals(MultiSubmodelElementProvider.VALUE)
				&& !isFileInputStream(newValue)) {
			// Path is only "value" and no specialized Provider has to be used -> update the
			// Element of this Provider
			Map<String, Object> elementMap = (Map<String, Object>) proxy.getValue("");

			ISubmodelElement element = SubmodelElementFacadeFactory.createSubmodelElement(elementMap);

			try {
				element.setValue(newValue);
			} catch (IllegalArgumentException e) {
				throw new MalformedRequestException("The given Value was not valid for Element '" + path + "'");
			}

			proxy.setValue("", element);

		} else {
			// Path has more Elements -> pass it to Provider below
			proxy.setValue(path, newValue);
		}
	}

	@Override
	public void createValue(String path, Object newEntity) throws ProviderException {
		if (!specializedProvider) {
			// In a regular SubmodelElement nothing can be created
			throw new MalformedRequestException("Creating a new Element is not allowed at '" + path + "'");
		} else {
			// If a specialized Provider is used, pass it down
			proxy.createValue(path, newEntity);
		}
	}

	@Override
	public void deleteValue(String path) throws ProviderException {
		if (!specializedProvider) {
			// From a regular SubmodelElement nothing can be deleted
			throw new MalformedRequestException("Deleting the Element '" + path + "' is not allowed");
		} else {
			// If a specialized Provider is used, pass it down
			proxy.deleteValue(path);
		}
	}

	@Override
	public void deleteValue(String path, Object obj) throws ProviderException {
		throw new MalformedRequestException("Delete with a passed argument not allowed");
	}

	@Override
	public Object invokeOperation(String path, Object... parameter) throws ProviderException {
		return proxy.invokeOperation(path, parameter);
	}

	private boolean isFileInputStream(Object newValue) {
		return newValue instanceof FileInputStream;
	}

}
