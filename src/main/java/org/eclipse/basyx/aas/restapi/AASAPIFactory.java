/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.restapi;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.aas.restapi.api.IAASAPIFactory;
import org.eclipse.basyx.aas.restapi.vab.VABAASAPIFactory;

public class AASAPIFactory implements IAASAPIFactory {
	IAASAPIFactory aasAPIFactory;

	public AASAPIFactory() {
		aasAPIFactory = new VABAASAPIFactory();
	}

	public AASAPIFactory(IAASAPIFactory factoryToBeDecorated) {
		aasAPIFactory = factoryToBeDecorated;
	}

	@Override
	public IAASAPI getAASApi(AssetAdministrationShell aas) {
		return aasAPIFactory.getAASApi(aas);
	}

}
