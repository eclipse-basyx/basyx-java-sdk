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
