/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement;

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IReferenceElement;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;

/**
 * A ReferenceElement as defined in DAAS document <br/>
 * A reference element is a data element that defines a reference to another
 * element within the same or another AAS or a reference to an external object
 * or entity.
 * 
 * @author schnicke, pschorn
 *
 */
public class ReferenceElement extends DataElement implements IReferenceElement {
	public static final String MODELTYPE = "ReferenceElement";

	/**
	 * Constructor
	 */
	public ReferenceElement() {
		// Add model type
		putAll(new ModelType(MODELTYPE));
	}

	/**
	 * Constructor with mandatory attribute
	 * 
	 * @param idShort
	 */
	public ReferenceElement(String idShort) {
		super(idShort);
		putAll(new ModelType(MODELTYPE));
	}

	/**
	 * 
	 * @param idShort
	 * @param ref
	 *            Reference to any other referable element of the same or any other
	 *            AAS or a reference to an external object or entity
	 */
	public ReferenceElement(String idShort, Reference ref) {
		this(idShort);
		setValue(ref);
	}

	/**
	 * @param ref
	 *            Reference to any other referable element of the same or any other
	 *            AAS or a reference to an external object or entity
	 */
	public ReferenceElement(Reference ref) {
		// Add model type
		putAll(new ModelType(MODELTYPE));
		put(Property.VALUE, ref);
	}
	
	/**
	 * Creates a ReferenceElement object from a map
	 * 
	 * @param obj a ReferenceElement object as raw map
	 * @return a ReferenceElement object, that behaves like a facade for the given map
	 */
	public static ReferenceElement createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		ReferenceElement ret = new ReferenceElement();
		ret.setMap(obj);
		return ret;
	}

	/**
	 * Returns true if the given submodel element map is recognized as a ReferenceElement
	 */
	public static boolean isReferenceElement(Map<String, Object> map) {
		String modelType = ModelType.createAsFacade(map).getName();
		// Either model type is set or the element type specific attributes are contained (fallback)
		// Ambiguous - fallback could be further improved by parsing the value and recognizing references
		return MODELTYPE.equals(modelType)
				|| (modelType == null && (map.containsKey(Property.VALUE) && !map.containsKey(Property.VALUETYPE)
				&& !map.containsKey(Property.VALUEID) && !map.containsKey(File.MIMETYPE)
				&& !map.containsKey(SubmodelElementCollection.ORDERED)
						&& !map.containsKey(SubmodelElementCollection.ALLOWDUPLICATES)));
	}

	@Override
	@SuppressWarnings("unchecked")
	public IReference getValue() {
		return Reference.createAsFacade((Map<String, Object>) get(Property.VALUE));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object value) {
		if(Reference.isReference(value)) {
			setValue(Reference.createAsFacade((Map<String, Object>) value));
		}
		else {
			throw new IllegalArgumentException("Given Object is not a Reference");
		}
	}
	
	@Override
	protected KeyElements getKeyElement() {
		return KeyElements.REFERENCEELEMENT;
	}

	@Override
	public ReferenceElement getLocalCopy() {
		// Return a shallow copy
		ReferenceElement copy = new ReferenceElement();
		copy.putAll(this);
		return copy;
	}

	@Override
	public void setValue(IReference value) {
		put(Property.VALUE, value);
	}
}
