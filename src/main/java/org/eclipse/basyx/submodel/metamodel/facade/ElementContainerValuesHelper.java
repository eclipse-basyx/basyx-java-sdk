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
	 * @param container
	 *            the {@link IElementContainer} to get the values from.
	 * @return A Map mapping idShort to the value of the SubmodelElement
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getSubmodelValue(IElementContainer container) {
		Map<String, ISubmodelElement> elements = container.getSubmodelElements();

		return (Map<String, Object>) handleValue(elements.values());
	}

	@SuppressWarnings("unchecked")
	private static Object handleValue(Object value) {
		// Check if it is a collection but not a LangStrings (is internally also a
		// Collection)
		if (value instanceof Collection<?> && !(value instanceof LangStrings)) {
			return handleValueCollection((Collection<ISubmodelElement>) value);
		} else {
			// The value is not a collection -> return it as is
			return value;
		}
	}

	private static Map<String, Object> handleValueCollection(Collection<ISubmodelElement> collection) {
		Map<String, Object> ret = new LinkedHashMap<>();
		for (ISubmodelElement element : collection) {
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
