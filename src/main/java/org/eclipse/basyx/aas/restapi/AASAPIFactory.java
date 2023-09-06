/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.aas.restapi;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.aas.restapi.api.IAASAPIFactory;
import org.eclipse.basyx.aas.restapi.vab.VABAASAPIFactory;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * AAS API provider that provides the default AAS API
 * 
 * @author fried
 */
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
		return aasAPIFactory.create(aas);
	}

	@Override
	public IAASAPI create(IIdentifier aasId) {
		return aasAPIFactory.create(aasId);
	}

}
