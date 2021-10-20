/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.vab.support;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

/**
 * Supporting provider that allows to record path requests on the VAB while
 * wrapping another provider
 * 
 * @author schnicke
 *
 */
public class RecordingProvider implements IModelProvider {

	private List<String> paths = new ArrayList<>();
	private IModelProvider wrapped;

	/**
	 * Creates the RecordingProvider wrapping another provider
	 * 
	 * @param wrapped
	 */
	public RecordingProvider(IModelProvider wrapped) {
		this.wrapped = wrapped;
	}

	/**
	 * Resets the path recording
	 */
	public void reset() {
		paths.clear();
	}


	@Override
	public Object getValue(String path) throws ProviderException {
		paths.add(path);
		return wrapped.getValue(path);
	}

	@Override
	public void setValue(String path, Object newValue) throws ProviderException {
		paths.add(path);
		wrapped.setValue(path, newValue);
	}

	@Override
	public void createValue(String path, Object newEntity) throws ProviderException {
		paths.add(path);
		wrapped.createValue(path, newEntity);
	}

	@Override
	public void deleteValue(String path) throws ProviderException {
		paths.add(path);
		wrapped.deleteValue(path);
	}

	@Override
	public void deleteValue(String path, Object obj) throws ProviderException {
		paths.add(path);
		wrapped.deleteValue(path, obj);
	}

	@Override
	public Object invokeOperation(String path, Object... parameter) throws ProviderException {
		paths.add(path);
		return wrapped.invokeOperation(path, parameter);
	}

	/**
	 * Returns the path accessed since the last call to
	 * {@link RecordingProvider#reset()}.
	 * 
	 * @return
	 */
	public List<String> getPaths() {
		return paths;
	}

}
