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
package org.eclipse.basyx.submodel.metamodel.map.dataspecification;

import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IValueReferencePair;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IIdentifiable;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.vab.model.VABModelMap;

/**
 * The map implementation for a value reference pair within a value list of the DataSpecificationIEC61360.
 * Each value has a global unique id defining its semantic.
 * 
 * @author espen
 *
 */
public class ValueReferencePair extends VABModelMap<Object> implements IValueReferencePair {
	public static final String VALUE = "value";
	public static final String VALUEID = "valueId";

	/**
	 * Constructor
	 */
	public ValueReferencePair() {}

	/**
	 * Constructs a reference based on an {@link IIdentifiable} and additional
	 * information (see {@link Key#Key(KeyElements, boolean, String, KeyType)}).
	 * 
	 * @param value
	 * @param valueId
	 */
	public ValueReferencePair(String value, IReference valueId) {
		setValue(value);
		setValueId(valueId);
	}

	/**
	 * Creates a ValueReferencePair object from a map
	 * 
	 * @param map
	 *            a ValueReferencePair object as raw map
	 * @return a ValueReferencePair object, that behaves like a facade for the given map
	 */
	public static ValueReferencePair createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}
		
		if (!isValid(map)) {
			throw new MetamodelConstructionException(ValueReferencePair.class, map);
		}
		
		ValueReferencePair ret = new ValueReferencePair();
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
		return map != null &&
				map.containsKey(VALUE) && 
				map.containsKey(VALUEID) &&
				Reference.isValid((Map<String, Object>)map.get(VALUEID));
	}

	@Override
	@SuppressWarnings("unchecked")
	public IReference getValueId() {
		return Reference.createAsFacade((Map<String, Object>) get(VALUEID));
	}

	@Override
	public String getValue() {
		return (String) get(VALUE);
	}

	public void setValueId(IReference valueId) {
		put(VALUEID, valueId);
	}

	public void setValue(String value) {
		put(VALUE, value);
	}
}
