/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.restapi.api;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;

/**
 * Interface for providing an AAS API
 * 
 * @author espen
 *
 */
public interface IAASAPIFactory {
	/**
	 * Return a constructed AAS API based on a raw model provider
	 * 
	 * @return
	 */
	public IAASAPI getAASApi(AssetAdministrationShell aas);
}
