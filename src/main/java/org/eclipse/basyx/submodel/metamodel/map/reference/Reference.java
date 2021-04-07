/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.map.reference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IIdentifiable;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.vab.model.VABModelMap;

/**
 * Reference as described by DAAS document <br>
 * <br>
 * Reference to either a model element of the same or another AAS or to an
 * external entity. A reference is an ordered list of keys, each key referencing
 * an element. The complete list of keys may for example be concatenated to a
 * path that then gives unique access to an element or entity.
 * 
 * @author schnicke
 *
 */
public class Reference extends VABModelMap<Object> implements IReference {
	public static final String KEY = "keys";

	/**
	 * Constructor
	 */
	public Reference() {
		setKeys(new ArrayList<IKey>());
	}

	/**
	 * Constructs a reference based on an {@link IIdentifiable} and additional
	 * information (see {@link Key#Key(KeyElements, boolean, String, KeyType)}).
	 * 
	 * @param identifiable
	 * @param keyElement
	 */
	public Reference(IIdentifiable identifiable, KeyElements keyElement, boolean local) {
		this(identifiable.getIdentification(), keyElement, local);
	}

	/**
	 * Constructs a reference based on an {@link IIdentifier} and additional
	 * information (see {@link Key#Key(KeyElements, boolean, String, KeyType)}).
	 * 
	 * @param identifier
	 * @param keyElement
	 * @param local
	 */
	public Reference(IIdentifier identifier, KeyElements keyElement, boolean local) {
		this(new Key(keyElement, local, identifier.getId(), identifier.getIdType()));
	}

	/**
	 * 
	 * @param keys Unique reference in its name space.
	 */
	public Reference(List<IKey> keys) {
		setKeys(keys);
	}

	/**
	 * 
	 * @param key
	 *            Unique reference in its name space.
	 */
	public Reference(Key key) {
		this(Collections.singletonList(key));
	}

	/**
	 * Creates a Reference object from a map
	 * 
	 * @param map
	 *            a Reference object as raw map
	 * @return a Reference object, that behaves like a facade for the given map
	 */
	public static Reference createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}
		
		if (!isValid(map)) {
			throw new MetamodelConstructionException(Reference.class, map);
		}
		
		Reference ret = new Reference();
		ret.setMap(map);
		return ret;
	}
	
	/**
	 * Check whether all mandatory elements for the metamodel
	 * exist in a map
	 * @return true/false
	 */
	@SuppressWarnings("unchecked")
	public static boolean isValid(Map<String, Object> map) {
		if (map != null && map.containsKey(Reference.KEY)) {
			Collection<Map<String, Object>> keysCollection = (Collection<Map<String, Object>>)map.get(Reference.KEY);
			for (Map<String, Object> key : keysCollection) {
				if (!Key.isValid(key)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Creates a Reference object from a map
	 * without checking mandatory attributes present
	 * 
	 * @param map
	 *            a Reference object as raw map
	 * @return a Reference object, that behaves like a facade for the given map
	 */
	public static Reference createAsFacadeNonStrict(Map<String, Object> map) {
		if (map == null) {
			return null;
		}
		
		Reference ret = new Reference();
		ret.setMap(map);
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean isReference(Object value) {
		if(!(value instanceof Map<?, ?>)) {
			return false;
		}
		
		Map<String, Object> map = (Map<String, Object>) value;
		
		if(!(map.get(KEY) instanceof Collection<?>)) {
			return false;
		}
		
		return ((Collection<?>) map.get(KEY)).stream().allMatch(Key::isKey);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IKey> getKeys() {
		List<IKey> ret = new ArrayList<>();

		// Transform list of maps to set of IKey
		Collection<Map<String, Object>> coll = (Collection<Map<String, Object>>) get(Reference.KEY);
		for (Map<String, Object> m : coll) {
			ret.add(Key.createAsFacade(m));
		}

		return ret;
	}

	public void setKeys(List<IKey> keys) {
		// Copy the key list to make sure an actual hashmap is put inside this map
		List<IKey> copy = new ArrayList<>();
		for (IKey key : keys) {
			copy.add(new Key(key.getType(), key.isLocal(), key.getValue(), key.getIdType()));
		}
		put(Reference.KEY, copy);
	}

	@Override
	public String toString() {
		return "Reference [getKeys()=" + getKeys() + "]";
	}

}
