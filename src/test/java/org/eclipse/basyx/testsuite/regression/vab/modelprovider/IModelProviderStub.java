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
package org.eclipse.basyx.testsuite.regression.vab.modelprovider;

import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

public class IModelProviderStub implements IModelProvider {

	private String path;
	private Object value;

	@Override
	public Object getValue(String path) {
		value = null;
		this.path = path;
		return null;
	}

	@Override
	public void setValue(String path, Object newValue) throws ProviderException {
		value = newValue;
		this.path = path;
	}

	@Override
	public void createValue(String path, Object newEntity) throws ProviderException {
		value = newEntity;
		this.path = path;
	}

	@Override
	public void deleteValue(String path) throws ProviderException {
		value = null;
		this.path = path;
	}

	@Override
	public void deleteValue(String path, Object obj) throws ProviderException {
		value = obj;
		this.path = path;
	}

	@Override
	public Object invokeOperation(String path, Object... parameter) throws ProviderException {
		value = parameter;
		this.path = path;
		return null;
	}

	public String getPath() {
		return path;
	}

	public Object getValue() {
		return value;
	}

}
