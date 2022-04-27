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
package org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IConstraint;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IQualifiable;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.vab.model.VABModelMap;

/**
 * Qualifiable class
 * 
 * @author kuhn
 *
 */
public class Qualifiable extends VABModelMap<Object> implements IQualifiable {
	public static final String QUALIFIERS = "qualifiers";

	/**
	 * Constructor
	 */
	public Qualifiable() {
	}

	/**
	 * Constructor
	 */
	public Qualifiable(Constraint qualifier) {
		// Create collection with qualifiers
		Collection<Constraint> qualifiers = new HashSet<>();
		// - Add qualifier
		qualifiers.add(qualifier);

		// The instance of an element may be further qualified by one or more
		// qualifiers.
		put(QUALIFIERS, qualifiers);
	}

	/**
	 * Constructor
	 */
	public Qualifiable(Collection<Constraint> qualifiers) {
		// The instance of an element may be further qualified by one or more
		// qualifiers.
		put(QUALIFIERS, qualifiers);
	}

	/**
	 * Creates a Qualifiable object from a map
	 * 
	 * @param map
	 *            a Qualifiable object as raw map
	 * @return a Qualifiable object, that behaves like a facade for the given map
	 */
	public static Qualifiable createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}

		Qualifiable ret = new Qualifiable();
		ret.setMap(map);
		return ret;
	}

	public void setQualifiers(Collection<IConstraint> qualifiers) {
		put(Qualifiable.QUALIFIERS, qualifiers);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<IConstraint> getQualifiers() {
		// Transform set of maps to set of IConstraints
		Collection<Map<String, Object>> set = (Collection<Map<String, Object>>) get(Qualifiable.QUALIFIERS);
		Collection<IConstraint> ret = new HashSet<>();
		if (set != null) {
			for (Map<String, Object> m : set) {
				if (ModelType.createAsFacade(m).getName().equals(Formula.MODELTYPE)) {
					ret.add(Formula.createAsFacade(m));
				} else {
					ret.add(Qualifier.createAsFacade(m));
				}
			}
		}
		return ret;
	}
}
