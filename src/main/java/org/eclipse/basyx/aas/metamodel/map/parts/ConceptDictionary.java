/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.metamodel.map.parts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.api.parts.IConceptDictionary;
import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.parts.IConceptDescription;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.reference.ReferenceHelper;
import org.eclipse.basyx.vab.model.VABModelMap;

/**
 * ConceptDictionary class as described in DAAS document
 * 
 * @author elsheikh, schnicke
 *
 */
public class ConceptDictionary extends VABModelMap<Object> implements IConceptDictionary {
	public static final String CONCEPTDESCRIPTION = "conceptDescription";

	// Extension of meta model to support local concept descriptions
	public static final String CONCEPTDESCRIPTIONS = "conceptDescriptions";

	/**
	 * Constructor
	 */
	public ConceptDictionary() {
		put(CONCEPTDESCRIPTION, new ArrayList<IReference>());
		put(CONCEPTDESCRIPTIONS, new ArrayList<IConceptDescription>());
	}
	
	/**
	 * Constructor accepting only mandatory attribute
	 * @param idShort
	 */
	public ConceptDictionary(String idShort) {
		this();
		setIdShort(idShort);
	}

	public ConceptDictionary(Collection<IReference> ref) {
		putAll(new Referable());
		put(CONCEPTDESCRIPTION, ref);
		put(CONCEPTDESCRIPTIONS, new ArrayList<IConceptDescription>());
	}

	/**
	 * Creates a ConceptDictionary object from a map
	 * 
	 * @param map
	 *            a ConceptDictionary object as raw map
	 * @return a ConceptDictionary object, that behaves like a facade for the given
	 *         map
	 */
	public static ConceptDictionary createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}
		
		if (!isValid(map)) {
			throw new MetamodelConstructionException(ConceptDictionary.class, map);
		}
		
		ConceptDictionary ret = new ConceptDictionary();
		ret.setMap(map);
		return ret;
	}
	
	/**
	 * Check whether all mandatory elements for the metamodel
	 * exist in a map
	 * @return true/false
	 */
	public static boolean isValid(Map<String, Object> map) {
		return Referable.isValid(map);
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
	public IReference getParent() {
		return Referable.createAsFacade(this, getKeyElement()).getParent();
	}

	public void setIdShort(String idShort) {
		Referable.createAsFacadeNonStrict(this, getKeyElement()).setIdShort(idShort);
	}

	public void setCategory(String category) {
		Referable.createAsFacade(this, getKeyElement()).setCategory(category);
	}

	public void setDescription(LangStrings description) {
		Referable.createAsFacade(this, getKeyElement()).setDescription(description);
	}

	public void setParent(IReference obj) {
		Referable.createAsFacade(this, getKeyElement()).setParent(obj);
	}

	@Override
	public Collection<IReference> getConceptDescriptionReferences() {
		return ReferenceHelper.transform(get(ConceptDictionary.CONCEPTDESCRIPTION));
	}

	/**
	 * Sets
	 * 
	 * @param ref
	 */
	public void setConceptDescriptionReferences(Collection<IReference> ref) {
		put(ConceptDictionary.CONCEPTDESCRIPTION, ref);
	}

	/**
	 * Sets the concept descriptions for this concept dictionary. The method sets local references to the added
	 * concept descriptions, too.
	 * 
	 * @param descriptions All the concept descriptions the concept dictionary shall have
	 */
	public void setConceptDescriptions(Collection<IConceptDescription> descriptions) {
		put(CONCEPTDESCRIPTIONS, descriptions);
		// Also add the references to these concept descriptions
		Collection<IReference> refs = new ArrayList<>();
		for ( IConceptDescription desc : descriptions ) {
			refs.add(createConceptDescriptionRef(desc));
		}
		setConceptDescriptionReferences(refs);
	}

	/**
	 * Adds a new concept description together with a local reference to it.
	 * 
	 * @param description The new concept description
	 */
	@SuppressWarnings("unchecked")
	public void addConceptDescription(IConceptDescription description) {
		Collection<IConceptDescription> desc = ((Collection<IConceptDescription>) get(CONCEPTDESCRIPTIONS));
		desc.add(description);
		Collection<IReference> refs = (Collection<IReference>) get(ConceptDictionary.CONCEPTDESCRIPTION);
		refs.add(createConceptDescriptionRef(description));
	}

	private IReference createConceptDescriptionRef(IConceptDescription description) {
		IIdentifier id = description.getIdentification();
		return new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, true, id.getId(), id.getIdType()));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<IConceptDescription> getConceptDescriptions() {
		return ((Collection<IConceptDescription>) get(CONCEPTDESCRIPTIONS));
	}
	
	private KeyElements getKeyElement() {
		return KeyElements.CONCEPTDICTIONARY;
	}

	@Override
	public IReference getReference() {
		return Referable.createAsFacade(this, getKeyElement()).getReference();
	}
}
