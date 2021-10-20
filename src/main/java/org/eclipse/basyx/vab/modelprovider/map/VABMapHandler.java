/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.modelprovider.map;

import java.util.Collection;
import java.util.Map;

import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.exception.provider.ResourceAlreadyExistsException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.generic.IVABElementHandler;

/**
 * A VAB map handler. Handles "Map" elements.
 * 
 * @author espen
 *
 */
public class VABMapHandler implements IVABElementHandler {
	@Override
	public Object getElementProperty(Object element, String propertyName) {
		if (element instanceof Map<?, ?>) {
			Map<?, ?> map = (Map<?, ?>) element;

			//check if requested property exists in map
			if (!map.containsKey(propertyName)) {
				throw new ResourceNotFoundException("Property \"" + propertyName + "\" does not exist.");
			}
			return map.get(propertyName);
		} else if (element instanceof Collection<?> || element instanceof Object[]) {
			throw new ResourceNotFoundException("It is not possible to access single elements in lists.");
		} else {
			throw new MalformedRequestException("Could not get property \"" + propertyName + "\".");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setModelPropertyValue(Object element, String propertyName, Object newValue) throws ProviderException {
		if (element instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) element;
			map.put(propertyName, newValue);
		} else if (element instanceof Collection<?> || element instanceof Object[]) {
			throw new ResourceNotFoundException("It is not possible to set single elements in lists.");
		} else {
			throw new MalformedRequestException("Could not set property \"" + propertyName + "\".");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void createValue(Object element, Object newValue) {
		if (element instanceof Collection<?>) {
			Collection<Object> collection = (Collection<Object>) element;
			collection.add(newValue);
		} else {
			throw new ResourceAlreadyExistsException("Could not create property.");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteValue(Object element, String propertyName) {
		if (element instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) element;
			// check if requested property exists in map
			if(!map.containsKey(propertyName)) {
				throw new ResourceNotFoundException("Property \"" + propertyName + "\" does not exist. Therefore it can not be deleted.");
			}
			map.remove(propertyName);
		} else if (element instanceof Collection<?> || element instanceof Object[]) {
			throw new ResourceNotFoundException("It is not possible to remove elements from a list using an index.");
		} else {
			throw new MalformedRequestException("Could not delete property \"" + propertyName + "\".");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteValue(Object element, Object property) {
		if (element instanceof Collection) {
			((Collection<Object>) element).remove(property);
		} else {
			throw new MalformedRequestException("Could not delete object from property.");
		}
	}
}
