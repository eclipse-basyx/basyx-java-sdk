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
package org.eclipse.basyx.submodel.metamodel.map.reference;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;

/**
 * Helper class for working with references
 * 
 * @author schnicke
 *
 */
public class ReferenceHelper {
	/**
	 * Helper method used e.g. by facades to transform a set of maps to a set of
	 * {@literal IReference -> Assumes the given object is a Collection<Map<String, Object>>}
	 * 
	 * @param obj
	 * @return
	 */
	public static Collection<IReference> transform(Object obj) {
		if (obj == null) {
			return new HashSet<>();
		}
		// Transform set of maps to set of IReference
		@SuppressWarnings("unchecked")
		Collection<Map<String, Object>> collection = (Collection<Map<String, Object>>) obj;
		return collection.stream().map(Reference::createAsFacade).collect(Collectors.toSet());
	}
}
