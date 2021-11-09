/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.api;

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.IHasDataSpecification;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IHasSemantics;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IIdentifiable;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.IHasKind;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IQualifiable;

/**
 * A submodel defines a specific aspect of the asset represented by the
 * AAS.<br>
 * <br>
 * A submodel is used to structure the digital representation and technical
 * functionality of an Administration Shell into distinguishable parts. Each
 * submodel refers to a well-defined domain or subject matter. Submodels can
 * become standardized and thus become submodels types. Submodels can have
 * different life-cycles.
 * 
 * @author kuhn, schnicke
 *
 */
public interface ISubmodel extends IElement, IHasSemantics, IIdentifiable, IQualifiable, IHasDataSpecification, IHasKind, IElementContainer {
	
	/**
	 * Gets a {@literal Map<IdShort, smElement.getValue() >} containing the values of all submodelElements
	 * 
	 * @return a Map with the values of all submodelElements
	 */
	public Map<String, Object> getValues();
	
}
