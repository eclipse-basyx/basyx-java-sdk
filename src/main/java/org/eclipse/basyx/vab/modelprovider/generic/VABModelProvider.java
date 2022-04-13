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
package org.eclipse.basyx.vab.modelprovider.generic;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.eclipse.basyx.vab.exception.provider.NotAnInvokableException;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.exception.provider.ResourceAlreadyExistsException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

/**
 * A generic VAB model provider.
 * 
 * @author espen
 */
public class VABModelProvider implements IModelProvider {
	/**
	 * Handler, which handles single element objects
	 */
	private IVABElementHandler handler;

	/**
	 * Root object that stores contained elements
	 */
	protected Object elements;

	public VABModelProvider(Object elements, IVABElementHandler handler) {
		this.handler = handler;
		this.elements = elements;
	}

	@Override
	public Object getValue(String path) {
		Object element = getTargetElement(path);
		return handler.postprocessObject(element);
	}

	@Override
	public void setValue(String path, Object newValue) {
		VABPathTools.checkPathForNull(path);
		if (VABPathTools.isEmptyPath(path)) {
			// Empty path => parent element == null => replace root, if it exists
			if (elements != null) {
				elements = newValue;
			}
			return;
		}

		Object parentElement = getParentElement(path);
		String propertyName = VABPathTools.getLastElement(path);
		// Throws an exception, if the element does not exist
		handler.getElementProperty(parentElement, propertyName);
		// => Can only set elements that have already been created
		handler.setModelPropertyValue(parentElement, propertyName, newValue);
	}

	@Override
	public void createValue(String path, Object newValue) {
		VABPathTools.checkPathForNull(path);
		if (VABPathTools.isEmptyPath(path)) {
			// The complete model should be replaced if it does not exist
			if (elements == null) {
				elements = newValue;
			} else {
				throw new ResourceAlreadyExistsException("Root element does already exist.");
			}
			return;
		}

		// Find parent & name of new element
		Object parentElement = getParentElement(path);
		String propertyName = VABPathTools.getLastElement(path);
		try {
			Object childElement = handler.getElementProperty(parentElement, propertyName);
			// The last path element does exist -> create the new value here
			handler.createValue(childElement, newValue);
		} catch (ResourceNotFoundException e) {
			// The last path element does not exist
			// -> create the new property in the parent element
			handler.setModelPropertyValue(parentElement, propertyName, newValue);
		}
	}

	@Override
	public void deleteValue(String path) {
		VABPathTools.checkPathForNull(path);

		Object parentElement = getParentElement(path);
		String propertyName = VABPathTools.getLastElement(path);
		handler.deleteValue(parentElement, propertyName);
	}

	@Override
	public void deleteValue(String path, Object obj) {
		Object targetElement = getTargetElement(path);
		handler.deleteValue(targetElement, obj);
	}

	@Override
	public Object invokeOperation(String path, Object... parameters) {

		path = VABPathTools.stripInvokeFromPath(path);

		Object childElement = getValue(path);

		// Invoke operation for function interfaces
		if (childElement instanceof Function<?, ?>) {
			return runFunction(childElement, parameters);
		} else if (childElement instanceof Supplier<?>) {
			return runSupplier(childElement);
		} else if (childElement instanceof Consumer<?>) {
			return runConsumer(childElement, parameters);
		} else if (childElement instanceof Runnable) {
			return runRunnable(childElement);
		} else {
			throw new NotAnInvokableException("Element \"" + path + "\" is not a function.");
		}
	}

	private Object runRunnable(Object childElement) {
		Runnable runnable = (Runnable) childElement;
		try {
			runnable.run();
		} catch (ProviderException e) {
			throw e;
		} catch (Exception e) {
			throw new ProviderException(e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Object runConsumer(Object childElement, Object... parameters) {
		Consumer<Object> consumer = (Consumer<Object>) childElement;
		try {
			consumer.accept(parameters);
		} catch (ProviderException e) {
			throw e;
		} catch (Exception e) {
			throw new ProviderException(e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Object runSupplier(Object childElement) {
		Supplier<Object> supplier = (Supplier<Object>) childElement;
		try {
			return supplier.get();
		} catch (ProviderException e) {
			throw e;
		} catch (Exception e) {
			throw new ProviderException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private Object runFunction(Object childElement, Object... parameters) {
		Function<Object[], Object> function = (Function<Object[], Object>) childElement;
		try {
			return function.apply(parameters);
		} catch (ProviderException e) {
			throw e;
		} catch (Exception e) {
			throw new ProviderException(e);
		}
	}

	/**
	 * Get the parent of an element in this provider. The path should include the
	 * path to the element separated by '/'. E.g., for accessing element c in path
	 * a/b, the path should be a/b/c.
	 */
	private Object getParentElement(String path) {
		VABPathTools.checkPathForNull(path);

		// Split path into its elements, separated by '/'
		String[] pathElements = VABPathTools.splitPath(path);

		Object currentElement = elements;
		// ignore the leaf element, only return the leaf's parent element
		for (int i = 0; i < pathElements.length - 1; i++) {
			currentElement = handler.getElementProperty(currentElement, pathElements[i]);
		}

		if (currentElement == null) {
			throw new ResourceNotFoundException("Parent element for \"" + path + "\" does not exist.");
		}

		return currentElement;
	}

	/**
	 * Instead of returning the parent element of a path, this function gives the
	 * target element. E.g., it returns c for the path a/b/c
	 */
	protected Object getTargetElement(String path) {
		VABPathTools.checkPathForNull(path);
		if (VABPathTools.isEmptyPath(path)) {
			return elements;
		}

		Object parentElement = getParentElement(path);
		String operationName = VABPathTools.getLastElement(path);
		if (operationName != null) {
			return handler.getElementProperty(parentElement, operationName);
		}
		return null;
	}
}
