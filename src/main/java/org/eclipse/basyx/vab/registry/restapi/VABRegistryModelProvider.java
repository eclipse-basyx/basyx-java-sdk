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
