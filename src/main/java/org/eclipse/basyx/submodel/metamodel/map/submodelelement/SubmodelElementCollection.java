/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.map.submodelelement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.IElementContainer;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.map.helper.ElementContainerHelper;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasDataSpecification;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;

/**
 * SubmodelElementCollection as defined by DAAS document <br>
 * A submodel element collection is a set or list of submodel elements
 * 
 * @author schnicke
 *
 */
public class SubmodelElementCollection extends SubmodelElement implements ISubmodelElementCollection, IElementContainer {
	public static final String ORDERED = "ordered";
	public static final String ALLOWDUPLICATES = "allowDuplicates";
	public static final String MODELTYPE = "SubmodelElementCollection";


	/**
	 * Constructor
	 */
	public SubmodelElementCollection() {
		// Add model type
		putAll(new ModelType(MODELTYPE));

		// Put attributes
		put(Property.VALUE, new HashMap<>());
		put(ORDERED, true);
		put(ALLOWDUPLICATES, true);
	}
	
	/**
	 * Constructor with only mandatory attribute
	 * @param idShort
	 */
	public SubmodelElementCollection(String idShort) {
		super(idShort);
		// Add model type
		putAll(new ModelType(MODELTYPE));

		// Put attributes
		setValue(new ArrayList<>());
		setOrdered(true);
		setAllowDuplicates(true);
	}

	/**
	 * 
	 * @param value
	 *            Submodel element contained in the collection
	 * @param ordered
	 *            If ordered=false then the elements in the property collection are
	 *            not ordered. If ordered=true then the elements in the collection
	 *            are ordered.
	 * @param allowDuplicates
	 *            If allowDuplicates=true then it is allowed that the collection
	 *            contains the same element several times
	 */
	public SubmodelElementCollection(Collection<ISubmodelElement> value, boolean ordered, boolean allowDuplicates) {
		// Add model type
		putAll(new ModelType(MODELTYPE));
		
		// Put attributes
		put(Property.VALUE, SubmodelElementMapCollectionConverter.convertCollectionToIDMap(value));
		put(ORDERED, ordered);
		put(ALLOWDUPLICATES, allowDuplicates);
	}
	
	/**
	 * Creates a SubmodelElementCollection object from a map
	 * 
	 * @param obj a SubmodelElementCollection object as raw map
	 * @return a SubmodelElementCollection object, that behaves like a facade for the given map
	 */
	public static SubmodelElementCollection createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		if (!isValid(obj)) {
			throw new MetamodelConstructionException(SubmodelElementCollection.class, obj);
		}
		
		return SubmodelElementMapCollectionConverter.mapToSmECollection(obj);
	}
	
	/**
	 * Check whether all mandatory elements for the metamodel
	 * exist in a map
	 * @return true/false
	 */
	public static boolean isValid(Map<String, Object> obj) {
		return SubmodelElement.isValid(obj);
	}
	
	/**
	 * Returns true if the given submodel element map is recognized as a submodel element collection
	 */
	public static boolean isSubmodelElementCollection(Map<String, Object> map) {
		String modelType = ModelType.createAsFacade(map).getName();
		// Either model type is set or the element type specific attributes are contained (fallback)
		return MODELTYPE.equals(modelType) || (modelType == null
				&& (map.containsKey(Property.VALUE) && map.containsKey(ORDERED) && map.containsKey(ALLOWDUPLICATES)));
	}

	/**
	 * Adds an element to the SubmodelElementCollection
	 * 
	 * @param elem
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addSubmodelElement(ISubmodelElement elem) {
		if (elem instanceof SubmodelElement) {
			((SubmodelElement) elem).setParent(getReference());
		}
		((Map<String, ISubmodelElement>) get(Property.VALUE)).put(elem.getIdShort(), elem);
	}

	@Override
	public Collection<IReference> getDataSpecificationReferences() {
		return HasDataSpecification.createAsFacade(this).getDataSpecificationReferences();
	}

	@Override
	public void setDataSpecificationReferences(Collection<IReference> ref) {
		HasDataSpecification.createAsFacade(this).setDataSpecificationReferences(ref);

	}

	@Override
	public String getIdShort() {
		return Referable.createAsFacade(this, getKeyElement()).getIdShort();
	}

	@Override
	public String getCategory() {
		return Referable.createAsFacade(this, getKeyElement()).getCategory();
	}

	@Override
	public LangStrings getDescription() {
		return Referable.createAsFacade(this, getKeyElement()).getDescription();
	}

	@Override
	public void setValue(Object value) {
		put(Property.VALUE, SubmodelElementMapCollectionConverter.convertCollectionToIDMap(value));
	}

	@Override
	public Collection<ISubmodelElement> getValue() {
		return Collections.unmodifiableList(new ArrayList<>((getSubmodelElements()).values()));
	}

	public void setOrdered(boolean value) {
		put(SubmodelElementCollection.ORDERED, value);
	}

	@Override
	public boolean isOrdered() {
		return (boolean) get(SubmodelElementCollection.ORDERED);
	}

	public void setAllowDuplicates(boolean value) {
		put(SubmodelElementCollection.ALLOWDUPLICATES, value);
	}

	@Override
	public boolean isAllowDuplicates() {
		return (boolean) get(SubmodelElementCollection.ALLOWDUPLICATES);
	}

	public void setElements(Map<String, ISubmodelElement> value) {
		put(Property.VALUE, value);
	}

	public void setElements(Collection<ISubmodelElement> value) {
		put(Property.VALUE, SubmodelElementMapCollectionConverter.convertCollectionToIDMap(value));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, ISubmodelElement> getSubmodelElements() {
		return (Map<String, ISubmodelElement>) get(Property.VALUE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, IProperty> getProperties() {
		Map<String, IProperty> ret = new HashMap<>();
		Map<String, ISubmodelElement> smElems = (Map<String, ISubmodelElement>) get(Property.VALUE);
		
		for(ISubmodelElement smElement: smElems.values()) {
			if (Property.isProperty((Map<String, Object>) smElement)) {
				ret.put(smElement.getIdShort(), (IProperty) smElement);
			}
		}

		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, IOperation> getOperations() {
		Map<String, IOperation> ret = new HashMap<>();
		Map<String, ISubmodelElement> smElems = (Map<String, ISubmodelElement>) get(Property.VALUE);
		
		for(ISubmodelElement smElement: smElems.values()) {
			if (Operation.isOperation(smElement)) {
				ret.put(smElement.getIdShort(), (IOperation) smElement);
			}
		}

		return ret;
	}

	/**
	 * Retrieves an element from element collection
	 * @param id
	 * @return retrieved element
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ISubmodelElement getSubmodelElement(String id) {
		Map<String, ISubmodelElement> submodelElems = (Map<String, ISubmodelElement>) get(Property.VALUE);
		return ElementContainerHelper.getElementById(submodelElems, id);
	}

	/**
	 * Deletes an element from element collection
	 * @param id
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void deleteSubmodelElement(String id) {
		Map<String, ISubmodelElement> submodelElems = (Map<String, ISubmodelElement>) get(Property.VALUE);
		ElementContainerHelper.removeElementById(submodelElems, id);
	}
	
	@Override
	protected KeyElements getKeyElement() {
		return KeyElements.SUBMODELELEMENTCOLLECTION;
	}

	@Override
	public SubmodelElementCollection getLocalCopy() {
		// Create a shallow copy
		SubmodelElementCollection copy = new SubmodelElementCollection();
		copy.putAll(this);
		// Then clone all submodelElements
		Collection<ISubmodelElement> value = getValue();
		Collection<ISubmodelElement> clonedValue = new ArrayList<>();
		value.forEach(element -> clonedValue.add(element.getLocalCopy()));
		// And return this copy with the cloned values
		copy.setValue(clonedValue);
		return copy;
	}
}
