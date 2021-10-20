/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
		if(!RelationshipElementValue.isRelationshipElementValue(value)) {
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
		((Collection<Map<String, Object>>) get(AnnotatedRelationshipElement.ANNOTATIONS)).stream()
		.map(m -> SubmodelElementFacadeFactory.createSubmodelElement(m))
		.filter(e -> e instanceof IDataElement).forEach(e -> list.add((IDataElement)e));
		
		return list;
	}

}
