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
package org.eclipse.basyx.submodel.metamodel.map.submodelelement;

import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ICapability;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;

/**
 * A Capability element as defined in DAAS document
 * 
 * @author espen, fischer
 *
 */
public class Capability extends SubmodelElement implements ICapability {

	public static final String MODELTYPE = "Capability";

	public Capability() {
		putAll(new ModelType(MODELTYPE));
	}

	/**
	 * Constructor accepting only mandatory attribute
	 * 
	 * @param idShort
	 */
	public Capability(String idShort) {
		super(idShort);

		putAll(new ModelType(MODELTYPE));
	}

	@Override
	protected KeyElements getKeyElement() {
		return KeyElements.CAPABILITY;
	}

	/**
	 * Creates a Capability object from a map
	 * 
	 * @param obj
	 *            a Capability object as raw map
	 * @return a Capability object, that behaves like a facade for the given map
	 */
	public static Capability createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}

		if (!isValid(obj)) {
			throw new MetamodelConstructionException(Capability.class, obj);
		}

		Capability facade = new Capability();
		facade.setMap(obj);
		return facade;
	}

	/**
	 * Check whether all mandatory elements for the metamodel exist in a map
	 * 
	 * @return true/false
	 */
	public static boolean isValid(Map<String, Object> obj) {
		return SubmodelElement.isValid(obj);
	}

	/**
	 * Returns true if the given submodel element map is recognized as a Capability
	 * element
	 */
	public static boolean isCapability(Map<String, Object> map) {
		String modelType = ModelType.createAsFacade(map).getName();
		// Either model type is set or the element type specific attributes are
		// contained (fallback)
		return MODELTYPE.equals(modelType) || (modelType == null);
	}

	@Override
	public Capability getLocalCopy() {
		// Return a shallow copy
		Capability copy = new Capability();
		copy.putAll(this);
		return copy;
	}
}
