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

import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IFormula;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.reference.ReferenceHelper;

/**
 * Forumla class as defined by DAAS document
 * 
 * @author schnicke
 *
 */
public class Formula extends Constraint implements IFormula {

	public static final String DEPENDSON = "dependsOn";

	public static final String MODELTYPE = "Formula";

	/**
	 * Constructor
	 */
	public Formula() {
		// Add model type
		putAll(new ModelType(MODELTYPE));

		put(DEPENDSON, new HashSet<Reference>());
	}

	/**
	 * 
	 * @param dependsOn
	 *            set of References the formula depends on
	 */
	public Formula(Collection<IReference> dependsOn) {
		putAll(new ModelType(MODELTYPE));
		put(DEPENDSON, dependsOn);
	}

	/**
	 * Creates a Formula object from a map
	 * 
	 * @param map
	 *            a Formula object as raw map
	 * @return a Formula object, that behaves like a facade for the given map
	 */
	public static Formula createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}

		Formula ret = new Formula();
		ret.setMap(map);
		return ret;
	}

	public void setDependsOn(Collection<IReference> dependsOn) {
		put(Formula.DEPENDSON, dependsOn);
	}

	@Override
	public Collection<IReference> getDependsOn() {
		return ReferenceHelper.transform(get(Formula.DEPENDSON));
	}


}
