/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.extensions.aas.directory.tagged.map;

import java.util.HashMap;

import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.IAASTaggedDirectory;
import org.eclipse.basyx.extensions.aas.directory.tagged.map.MapTaggedDirectory;
import org.eclipse.basyx.testsuite.regression.extensions.aas.directory.tagged.TestTaggedDirectorySuite;

/**
 * Tests the map variant of the TaggedDirectory
 *
 * @author schnicke
 *
 */
public class TestMapTaggedDirectory extends TestTaggedDirectorySuite {

	@Override
	protected IAASTaggedDirectory getDirectory() {
		return new MapTaggedDirectory(new HashMap<>(), new HashMap<>(), new HashMap<>());
	}

	@Override
	protected IAASRegistry getRegistryService() {
		return getDirectory();
	}

}
