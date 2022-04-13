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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IDataElement;
import org.eclipse.basyx.submodel.metamodel.facade.submodelelement.SubmodelElementFacadeFactory;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;

/**
 * Container class for holding the value of RelationshipElement
 * 
 * @author schnicke, conradi
 *
 */
public class AnnotatedRelationshipElementValue extends RelationshipElementValue {

	public AnnotatedRelationshipElementValue(IReference first, IReference second, Collection<IDataElement> annotations) {
		super(first, second);
		put(AnnotatedRelationshipElement.ANNOTATIONS, annotations);
	}

	private AnnotatedRelationshipElementValue() {
		super();
	}

	/**
	 * Creates a AnnotatedRelationshipElementValue object from a map
	 * 
	 * @param obj
	 *            a AnnotatedRelationshipElementValue object as raw map
	 * @return a AnnotatedRelationshipElementValue object, that behaves like a
	 *         facade for the given map
	 */
	public static AnnotatedRelationshipElementValue createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}

		AnnotatedRelationshipElementValue facade = new AnnotatedRelationshipElementValue();
		facade.setMap(obj);
		return facade;
	}

	@SuppressWarnings("unchecked")
	public static boolean isAnnotatedRelationshipElementValue(Object value) {
		if (!RelationshipElementValue.isRelationshipElementValue(value)) {
			return false;
		} else {
			Map<String, Object> map = (Map<String, Object>) value;
			return Reference.isReference(map.get(AnnotatedRelationshipElement.ANNOTATIONS));
		}
	}

	@SuppressWarnings("unchecked")
	public Collection<IDataElement> getAnnotations() {
		Collection<IDataElement> list = new ArrayList<>();

		// Feed all Elements in ANNOTATIONS through the SubmodelElementFacadeFactory
		// then collect them in a List<IDataElement>
		((Collection<Map<String, Object>>) get(AnnotatedRelationshipElement.ANNOTATIONS)).stream().map(m -> SubmodelElementFacadeFactory.createSubmodelElement(m)).filter(e -> e instanceof IDataElement)
				.forEach(e -> list.add((IDataElement) e));

		return list;
	}

}
