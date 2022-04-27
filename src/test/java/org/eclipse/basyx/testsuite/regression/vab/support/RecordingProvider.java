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
package org.eclipse.basyx.testsuite.regression.vab.support;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
		if (newEntity instanceof InputStream) {
			try {
				InputStream in = (InputStream) newEntity;
				int n = in.available();
				byte[] bytes = new byte[n];
				in.read(bytes, 0, n);
				String s = new String(bytes, StandardCharsets.UTF_8);
				wrapped.createValue(path, s);
			} catch (Exception e) {
				throw new ProviderException("Cannot parse input stream");
			}

		} else {
			wrapped.createValue(path, newEntity);
		}
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
