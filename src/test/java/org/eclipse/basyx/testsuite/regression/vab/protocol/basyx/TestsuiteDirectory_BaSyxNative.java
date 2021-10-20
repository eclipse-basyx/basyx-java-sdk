/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.vab.protocol.basyx;

import org.eclipse.basyx.vab.registry.memory.VABInMemoryRegistry;




/**
 * Implement the test suite directory service with pre-configured directory entries
 * 
 * @author kuhn
 *
 */
public class TestsuiteDirectory_BaSyxNative extends VABInMemoryRegistry {

	
	/**
	 * Constructor - load all directory entries
	 */
	public TestsuiteDirectory_BaSyxNative() {
		// VAB Element mapping
		addMapping("urn:fhg:es.iese:vab:1:1:simplevabelement", "basyx://localhost:6998");
	}	
}
