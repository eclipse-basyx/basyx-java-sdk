/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.facade;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.IElementContainer;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;

/**
 * Helper class for getting the /values Map from a Element Container.
 * 
 * @author conradi, haque
 *
 */
public class ElementContainerValuesHelper {

	/**
	 * Gets the Values from a {@link IElementContainer}
	 * 
	 * @param container the {@link IElementContainer} to get the values from.
	 * @return A Map mapping idShort to the value of the SubmodelElement
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getSubmodelValue(IElementContainer container) {
		Map<String, ISubmodelElement> elements = container.getSubmodelElements();
		
		return (Map<String, Object>) handleValue(elements.values());
	}
	
	
	@SuppressWarnings("unchecked")
	private static Object handleValue(Object value) {
		// Check if it is a collection but not a LangStrings (is internally also a Collection)
		if (value instanceof Collection<?> && !(value instanceof LangStrings)) {
			return handleValueCollection((Collection<ISubmodelElement>) value);
		} else {
			// The value is not a collection -> return it as is
			return value;
		}
	}


	private static Map<String, Object> handleValueCollection(Collection<ISubmodelElement> collection) {
		Map<String, Object> ret = new LinkedHashMap<>();
		for(ISubmodelElement element: collection) {
			try {
				ret.put(element.getIdShort(), handleValue(element.getValue()));
			} catch (UnsupportedOperationException e) {
				// this Element has no value (e.g. an Operation)
				// -> just ignore it
			}
		}
		return ret;
	}
	
}
