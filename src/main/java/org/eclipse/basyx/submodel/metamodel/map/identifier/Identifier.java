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
package org.eclipse.basyx.submodel.metamodel.map.identifier;

import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.vab.model.VABModelMap;

/**
 * Identification class
 * 
 * @author kuhn, schnicke
 *
 */
public class Identifier extends VABModelMap<Object> implements IIdentifier {

	public static final String IDTYPE = "idType";
	public static final String ID = "id";

	/**
	 * Constructor
	 */
	public Identifier() {
		// Default values
		put(IDTYPE, IdentifierType.IRDI.toString());
		put(ID, "");
	}

	/**
	 * Creates a Identifier object from a map
	 * 
	 * @param map
	 *            a Identifier object as raw map
	 * @return a Identifier object, that behaves like a facade for the given map
	 */
	public static Identifier createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}

		if (!isValid(map)) {
			throw new MetamodelConstructionException(Identifier.class, map);
		}

		Identifier ret = new Identifier();
		ret.setMap(map);
		return ret;
	}

	/**
	 * Check whether all mandatory elements for the metamodel exist in a map
	 * 
	 * @return true/false
	 */
	public static boolean isValid(Map<String, Object> map) {
		return map != null && map.containsKey(IDTYPE) && map.containsKey(ID);
	}

	/**
	 * Constructor that accepts parameter
	 */
	public Identifier(IdentifierType idType, String id) {
		// Load values
		if (idType == null) {
			put(IDTYPE, null);
		} else {
			put(IDTYPE, idType.toString());
		}
		put(ID, id);
	}

	@Override
	public IdentifierType getIdType() {
		return IdentifierType.fromString((String) get(Identifier.IDTYPE));
	}

	public void setIdType(IdentifierType newValue) {
		put(Identifier.IDTYPE, newValue.toString());
	}

	@Override
	public String getId() {
		return (String) get(Identifier.ID);
	}

	public void setId(String newValue) {
		put(Identifier.ID, newValue);
	}
}
