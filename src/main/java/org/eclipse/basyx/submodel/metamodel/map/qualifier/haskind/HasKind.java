/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.map.qualifier.haskind;

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.IHasKind;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.vab.model.VABModelMap;

/**
 * HasKind class
 * 
 * @author elsheikh, schnicke
 *
 */
public class HasKind extends VABModelMap<Object> implements IHasKind {
	public static final String KIND = "kind";

	/**
	 * Constructor
	 */
	public HasKind() {}

	/**
	 * Constructor that takes
	 * {@link ModelingKind} (either Kind.Instance or Kind.Type)
	 */
	public HasKind(ModelingKind kind) {
		// Kind of the element: either type or instance.

		put(KIND, kind.toString());
	}

	/**
	 * Creates a HasKind object from a map
	 * 
	 * @param map
	 *            a HasKind object as raw map
	 * @return a HasKind object, that behaves like a facade for the given map
	 */
	public static HasKind createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}

		HasKind ret = new HasKind();
		ret.setMap(map);
		return ret;
	}

	/**
	 * @deprecated Please use {@link #getKind()} instead.
	 */
	@Override
	public ModelingKind getModelingKind() {
		return this.getKind();
	}

	/**
	 * @deprecated Please use {@link #setKind(ModelingKind)} instead.
	 */
	public void setModelingKind(ModelingKind kind) {
		this.setKind(kind);
	}

	@Override
	public ModelingKind getKind() {
		String str = (String) get(HasKind.KIND);
		return ModelingKind.fromString(str);
	}

	public void setKind(ModelingKind kind) {
		if (kind != null) {
			put(HasKind.KIND, kind.toString());
		} else {
			put(HasKind.KIND, null);
		}
	}

}
