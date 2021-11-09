/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
