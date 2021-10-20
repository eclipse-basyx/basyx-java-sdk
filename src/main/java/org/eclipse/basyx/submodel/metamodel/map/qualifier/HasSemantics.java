/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.map.qualifier;

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.IHasSemantics;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.vab.model.VABModelMap;

/**
 * HasSemantics class
 * 
 * @author kuhn, schnicke
 *
 */
public class HasSemantics extends VABModelMap<Object> implements IHasSemantics {
	public static final String SEMANTICID = "semanticId";

	/**
	 * Constructor
	 */
	public HasSemantics() {}

	/**
	 * Constructor
	 */
	public HasSemantics(IReference ref) {
		this.setSemanticId(ref);
	}

	/**
	 * Creates a HasSemantics object from a map
	 * 
	 * @param obj
	 *            a HasSemantics object as raw map
	 * @return a HasSemantics object, that behaves like a facade for the given map
	 */
	public static HasSemantics createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}

		HasSemantics ret = new HasSemantics();
		ret.setMap(map);
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IReference getSemanticId() {
		return Reference.createAsFacade((Map<String, Object>) get(HasSemantics.SEMANTICID));
	}

	public void setSemanticId(IReference ref) {
		put(HasSemantics.SEMANTICID, ref);
	}
}
