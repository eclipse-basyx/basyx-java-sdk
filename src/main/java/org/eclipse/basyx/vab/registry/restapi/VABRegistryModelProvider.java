/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.registry.restapi;

import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.registry.api.IVABRegistryService;
import org.eclipse.basyx.vab.registry.memory.VABInMemoryRegistry;

/**
 * Connects an arbitrary IVABDirectoryService implementation to the VAB
 * 
 * @author schnicke
 */

public class VABRegistryModelProvider implements IModelProvider {

	private IVABRegistryService directory;

	/**
	 * Creates a DirectoryModelProvider wrapping an IVABDirectoryService
	 * 
	 * @param directory
	 */
	public VABRegistryModelProvider(IVABRegistryService directory) {
		super();
		this.directory = directory;
	}

	/**
	 * Creates a default DirectoryModelProvider wrapping an InMemoryDirectory
	 */
	public VABRegistryModelProvider() {
		this(new VABInMemoryRegistry());
	}

	@Override
	public Object getValue(String path) throws ProviderException {
		path = VABPathTools.stripSlashes(path);
		return directory.lookup(path);
	}

	@Override
	public void setValue(String path, Object newValue) throws ProviderException {
		throw new RuntimeException("Set not supported by VAB Directory");
	}

	@Override
	public void createValue(String path, Object newEntity) throws ProviderException {
		path = VABPathTools.stripSlashes(path);
		directory.addMapping(path, (String) newEntity);
	}

	@Override
	public void deleteValue(String path) throws ProviderException {
		path = VABPathTools.stripSlashes(path);
		directory.removeMapping(path);
	}

	@Override
	public void deleteValue(String path, Object obj) throws ProviderException {
		throw new RuntimeException("Delete with parameter not supported by VAB Directory");
	}

	@Override
	public Object invokeOperation(String path, Object... parameter) throws ProviderException {
		throw new RuntimeException("Invoke not supported by VAB Directory");
	}

}
