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
package org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship;

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.vab.model.VABModelMap;

/**
 * Container class for holding the value of RelationshipElement
 * 
 * @author conradi
 *
 */
public class RelationshipElementValue extends VABModelMap<Object> {

	protected RelationshipElementValue() {
	}

	public RelationshipElementValue(IReference first, IReference second) {
		put(RelationshipElement.FIRST, first);
		put(RelationshipElement.SECOND, second);
	}

	/**
	 * Creates a RelationshipElementValue object from a map
	 * 
	 * @param obj
	 *            a RelationshipElementValue object as raw map
	 * @return a RelationshipElementValue object, that behaves like a facade for the
	 *         given map
	 */
	public static RelationshipElementValue createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}

		RelationshipElementValue facade = new RelationshipElementValue();
		facade.setMap(obj);
		return facade;
	}

	@SuppressWarnings("unchecked")
	public static boolean isRelationshipElementValue(Object value) {

		// Given Object must be a Map
		if (!(value instanceof Map<?, ?>)) {
			return false;
		}

		Map<String, Object> map = (Map<String, Object>) value;

		return Reference.isReference(map.get(RelationshipElement.FIRST)) && Reference.isReference(map.get(RelationshipElement.SECOND));
	}

	@SuppressWarnings("unchecked")
	public IReference getFirst() {
		return Reference.createAsFacade((Map<String, Object>) get(RelationshipElement.FIRST));
	}

	@SuppressWarnings("unchecked")
	public IReference getSecond() {
		return Reference.createAsFacade((Map<String, Object>) get(RelationshipElement.SECOND));
	}

}
