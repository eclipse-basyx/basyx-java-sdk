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
package org.eclipse.basyx.testsuite.regression.vab.coder.json;

import org.eclipse.basyx.testsuite.regression.vab.modelprovider.SimpleVABElement;
import org.eclipse.basyx.testsuite.regression.vab.modelprovider.TestProvider;
import org.eclipse.basyx.testsuite.regression.vab.protocol.http.TestsuiteDirectory;
import org.eclipse.basyx.vab.coder.json.connector.JSONConnector;
import org.eclipse.basyx.vab.coder.json.provider.JSONProvider;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;
import org.eclipse.basyx.vab.protocol.api.ConnectorFactory;

/**
 * Test JSONConnector against JSONProvider
 * 
 * @author pschorn
 *
 */
public class TestJSONConnectorProviderIntegration extends TestProvider {

	protected VABConnectionManager connManager = new VABConnectionManager(new TestsuiteDirectory(),
			new ConnectorFactory() {

				@Override
				protected IModelProvider createProvider(String addr) {

					// BACKEND
					// Creates a new VABMapProvider which manages a data model as defined in
					// SimpleVABElement.
					VABMapProvider modelprovider = new VABMapProvider(new SimpleVABElement());

					// We stack the JSONProvider on top of the model to handle serialization and exceptions
					JSONProvider<VABMapProvider> provider = new JSONProvider<VABMapProvider>(modelprovider);

					// FRONTEND
					// We stack the JSONConnector on top of the JSONProvider to handle de-serialization and response
					// verification
					JSONConnector connector = new JSONConnector(
							new IBasyxConnectorFacade<VABMapProvider>(provider));

					return connector;
				}
			});

	@Override
	protected VABConnectionManager getConnectionManager() {
		return connManager;
	}
}
