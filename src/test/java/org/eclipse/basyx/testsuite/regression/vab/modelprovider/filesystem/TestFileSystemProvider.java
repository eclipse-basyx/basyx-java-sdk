/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
