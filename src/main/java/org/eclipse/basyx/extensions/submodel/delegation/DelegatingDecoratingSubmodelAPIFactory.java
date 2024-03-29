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
package org.eclipse.basyx.extensions.submodel.delegation;

import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;

/**
 * API provider for constructing a new SubmodelAPI which has delegated property
 * 
 * @author danish
 * 
 */
public class DelegatingDecoratingSubmodelAPIFactory implements ISubmodelAPIFactory {
	private ISubmodelAPIFactory submodelAPIFactory;

	public DelegatingDecoratingSubmodelAPIFactory(ISubmodelAPIFactory submodelAPIFactory) {
		this.submodelAPIFactory = submodelAPIFactory;
	}

	@Override
	public ISubmodelAPI getSubmodelAPI(Submodel submodel) {
		return new DelegatingSubmodelAPI(submodelAPIFactory.create(submodel), new PropertyDelegationManager(new HTTPConnectorFactory()));
	}
}
