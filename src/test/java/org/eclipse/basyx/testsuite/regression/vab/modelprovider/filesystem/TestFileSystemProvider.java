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
package org.eclipse.basyx.testsuite.regression.vab.modelprovider.filesystem;

import org.eclipse.basyx.testsuite.regression.vab.modelprovider.SimpleVABElement;
import org.eclipse.basyx.testsuite.regression.vab.modelprovider.TestProvider;
import org.eclipse.basyx.testsuite.regression.vab.protocol.http.TestsuiteDirectory;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.modelprovider.filesystem.FileSystemProvider;
import org.eclipse.basyx.vab.modelprovider.filesystem.filesystem.FileSystem;
import org.eclipse.basyx.vab.modelprovider.filesystem.filesystem.GenericFileSystem;
import org.eclipse.basyx.vab.protocol.api.ConnectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests the functionality of the FileSystemProvider according to the test cases
 * in the snippet package
 * 
 * @author schnicke
 *
 */
public class TestFileSystemProvider extends TestProvider {
	
	private static Logger logger = LoggerFactory.getLogger(TestFileSystemProvider.class);
	
	private VABConnectionManager connManager;

	@Override
	protected VABConnectionManager getConnectionManager() {
		if (connManager == null) {
			connManager = new VABConnectionManager(new TestsuiteDirectory(), new ConnectorFactory() {
				@Override
				protected IModelProvider createProvider(String addr) {

					String root = "regressiontest/HMDR/Test";
					FileSystem fs = new GenericFileSystem();
					try {
						FileSystemProvider provider = new FileSystemProvider(fs, root, new SimpleVABElement(), true);
						return provider;
					} catch (Exception e) {
						logger.error("[TEST] Exception in getConnectionManager", e);
						throw new RuntimeException();
					}
				}
			});
		}
		return connManager;
	}

	@Override
	public void testMapInvoke() {
		// not implemented for file system providers
	}
}
