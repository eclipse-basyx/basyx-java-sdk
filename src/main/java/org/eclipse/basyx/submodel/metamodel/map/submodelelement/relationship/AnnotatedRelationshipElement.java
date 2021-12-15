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

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IDataElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.relationship.IAnnotatedRelationshipElement;
import org.eclipse.basyx.submodel.metamodel.facade.submodelelement.SubmodelElementFacadeFactory;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;

/**
 * AnnotatedRelationshipElement as defined in DAAS document <br>
 * An annotated relationship element is a relationship element that can be annotated with additional data elements.
 * 
 * @author schnicke, conradi
 *
 */
public class AnnotatedRelationshipElement extends RelationshipElement implements IAnnotatedRelationshipElement {
	public static final String ANNOTATIONS = "annotation";
	public static final String MODELTYPE = "AnnotatedRelationshipElement";

	public AnnotatedRelationshipElement() {
		super();
		// Add model type
		putAll(new ModelType(MODELTYPE));
	}

	public AnnotatedRelationshipElement(String idShort, IReference first, IReference second) {
		super(idShort, first, second);
		// Add model type
		putAll(new ModelType(MODELTYPE));
	}

	public void setAnnotation(Collection<IDataElement> annotations) {
		put(ANNOTATIONS, annotations);
	}

	
	@SuppressWarnings("unchecked")
	private Collection<IDataElement> getAnnotations() {	
		Collection<IDataElement> list = new ArrayList<>();
		Collection<Map<String, Object>> annotations = (Collection<Map<String, Object>>) get(ANNOTATIONS);
		
		// If non mandatory element annotations does not exist, return empty list
		if(annotations == null) {
			return list;
		}
		
		annotations.stream()
		.map(m -> SubmodelElementFacadeFactory.createSubmodelElement(m))
		.filter(e -> e instanceof IDataElement).forEach(e -> list.add((IDataElement)e));
		
		return list;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object obj) {
		if (AnnotatedRelationshipElementValue.isAnnotatedRelationshipElementValue(obj)) {
			AnnotatedRelationshipElementValue value = AnnotatedRelationshipElementValue.createAsFacade((Map<String, Object>) obj);
			setValue(value);
		} else {
			throw new IllegalArgumentException("Given Object is not an AnnotatedRelationshipElementValue");
		}
	}

	/**
	 * Creates a AnnotatedRelationshipElement object from a map
	 * 
	 * @param obj
	 *            a AnnotatedRelationshipElement object as raw map
	 * @return a AnnotatedRelationshipElement object, that behaves like a facade for
	 *         the given map
	 */
	public static AnnotatedRelationshipElement createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}

		if (!isValid(obj)) {
			throw new MetamodelConstructionException(AnnotatedRelationshipElement.class, obj);
		}

		AnnotatedRelationshipElement ret = new AnnotatedRelationshipElement();
		ret.setMap(obj);
		return ret;
	}

	/**
	 * Check whether all mandatory elements for the metamodel exist in a map
	 * 
	 * @return true/false
	 */
	public static boolean isValid(Map<String, Object> obj) {
		return RelationshipElement.isValid(obj);
	}

	public static boolean isAnnotatedRelationshipElement(Map<String, Object> value) {
		String modelType = ModelType.createAsFacade(value).getName();
		// Either model type is set or the element type specific attributes are
		// contained
		return MODELTYPE.equals(modelType) || (modelType == null && isValid(value));
	}

	@Override
	public AnnotatedRelationshipElementValue getValue() {
		return new AnnotatedRelationshipElementValue(getFirst(), getSecond(), getAnnotations());
	}

	@Override
	public void setValue(AnnotatedRelationshipElementValue value) {
		super.setValue(value);
		setAnnotation(value.getAnnotations());
	}

}
