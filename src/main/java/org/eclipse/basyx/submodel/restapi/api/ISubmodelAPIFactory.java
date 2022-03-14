/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.restapi.api;

import org.eclipse.basyx.submodel.metamodel.map.Submodel;

/**
 * Interface for providing an Submodel API
 * 
 * @author espen
 *
 */
public interface ISubmodelAPIFactory {
	/**
	 * Return a constructed Submodel API
	 * @deprecated This method is deprecated please use {@link #create(Submodel)}
	 * 
	 * @return
	 */
	@Deprecated
	public ISubmodelAPI getSubmodelAPI(Submodel submodel);
	
	public default ISubmodelAPI create(Submodel submodel) {
		return getSubmodelAPI(submodel);
	}
}
