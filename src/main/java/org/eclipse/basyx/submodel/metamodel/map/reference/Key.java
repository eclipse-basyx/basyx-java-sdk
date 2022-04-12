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

import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.vab.model.VABModelMap;

/**
 * Key as defined in DAAS document <br>
 * <br>
 * A key is a reference to an element by its id.
 * 
 * @author schnicke
 *
 */
public class Key extends VABModelMap<Object> implements IKey {
	public static final String TYPE = "type";
	public static final String LOCAL = "local";
	public static final String VALUE = "value";
	public static final String IDTYPE = "idType";

	/**
	 * 
	 * @param type
	 *            Denote which kind of entity is referenced.
	 * @param local
	 *            Denotes if the key references a model element of the same AAS
	 *            (=true) or not (=false).
	 * @param value
	 *            The key value, for example an IRDI if the idType=IRDI.
	 * @param idType
	 *            Type of the key value. In case of idType = idShort local shall be
	 *            true. In case type=GlobalReference idType shall not be IdShort.
	 */
	public Key(KeyElements type, boolean local, String value, KeyType idType) {
		put(TYPE, type.toString());
		put(LOCAL, local);
		put(VALUE, value);
		put(IDTYPE, idType.toString());
	}
	
	/**
	 * Helper constructor to translate IdentifierType to KeyType. <br>
	 * In the meta model KeyType inheritcs from IdentifiertType, however Java does
	 * not support enum inheritance
	 * 
	 * @param type
	 * @param value
	 * @param idType
	 */
	public Key(KeyElements type, boolean local, String value, IdentifierType idType) {
		this(type, local, value, KeyType.fromString(idType.toString()));
	}

	/**
	 * Private constructor enabling createAsFacade pattern
	 */
	private Key() {

	}

	/**
	 * Creates a Key object from a map
	 * 
	 * @param map
	 *            a Key object as raw map
	 * @return a Key object, that behaves like a facade for the given map
	 */
	public static Key createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}
		
		if (!isValid(map)) {
			throw new MetamodelConstructionException(Key.class, map);
		}
		
		Key ret = new Key();
		ret.setMap(map);
		return ret;
	}
	
	/**
	 * Check whether all mandatory elements for the metamodel
	 * exist in a map
	 * @return true/false
	 */
	public static boolean isValid(Map<String, Object> map) {
		return map != null &&
				map.containsKey(TYPE) &&
				map.containsKey(LOCAL) &&
				map.containsKey(VALUE) &&
				map.containsKey(IDTYPE);
	}
	
	@SuppressWarnings("unchecked")
	public static boolean isKey(Object value) {
		if(!(value instanceof Map<?, ?>)) {
			return false;
		}
		
		Map<String, Object> map = (Map<String, Object>) value;
		
		if(!(map.get(LOCAL) instanceof Boolean && map.get(VALUE) instanceof String
				&& map.get(IDTYPE) instanceof String && map.get(TYPE) instanceof String)) {
			return false;
		}
		
		try {
			// Try to convert the Strings to Enum-Types
			// If that fails an Exception is thrown
			KeyType.fromString((String) map.get(IDTYPE));
			KeyElements.fromString((String) map.get(TYPE));
		} catch (IllegalArgumentException e) {
			return false;
		}
		
		return true;
	}

	@Override
	public KeyElements getType() {
		return KeyElements.fromString((String) get(Key.TYPE));
	}

	@Override
	public boolean isLocal() {
		return (boolean) get(Key.LOCAL);
	}

	@Override
	public String getValue() {
		return (String) get(Key.VALUE);
	}

	@Override
	public KeyType getIdType() {
		return KeyType.fromString((String) get(Key.IDTYPE));
	}

	public void setType(KeyElements type) {
		put(Key.TYPE, type.toString());
	}

	public void setLocal(boolean local) {
		put(Key.LOCAL, local);
	}

	public void setValue(String value) {
		put(Key.VALUE, value);
	}

	public void setIdType(KeyType idType) {
		put(Key.IDTYPE, idType.toString());
	}

	@Override
	public String toString() {
		return "Key [getType()=" + getType() + ", isLocal()=" + isLocal() + ", getValue()=" + getValue() + ", getIdType()=" + getIdType() + "]";
	}
}
