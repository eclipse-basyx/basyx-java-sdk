/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.testsuite.regression.extensions.storage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.basyx.extensions.internal.storage.BaSyxStorageAPI;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

/**
 * 
 * @author jungjan
 *
 */
public class TestBaSyxStorageAPI extends BaSyxStorageAPISuite {
	private Map<String, Submodel> mockedStorage = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	private BaSyxStorageAPI<Submodel> mockedStorageAPI = Mockito.spy(BaSyxStorageAPI.class);

	public TestBaSyxStorageAPI() {
		super();
		initMockedStorageAPI();
	}

	private void initMockedStorageAPI() {
		Mockito.when(mockedStorageAPI.createOrUpdate(Mockito.any(Submodel.class))).then(this::mockedCreateOrUpdate);
		Mockito.when(mockedStorageAPI.update(Mockito.any(Submodel.class), Mockito.anyString())).then(this::mockedupdate);
		Mockito.when(mockedStorageAPI.rawRetrieve(Mockito.anyString())).then(this::mockedRawRetrieve);
		Mockito.when(mockedStorageAPI.rawRetrieveAll()).then(this::mockedRawRetrieveAll);
		Mockito.when(mockedStorageAPI.delete(Mockito.anyString())).then(this::mockedDelete);
	}

	@Override
	protected BaSyxStorageAPI<Submodel> getStorageAPI() {
		return mockedStorageAPI;
	}

	private Submodel mockedCreateOrUpdate(InvocationOnMock invocation) {
		Submodel submodel = invocation.getArgument(0);
		String identificationId = submodel.getIdentification().getId();
		mockedStorage.put(identificationId, submodel);
		return submodel;
	}

	private Submodel mockedupdate(InvocationOnMock invocation) {
		String key = invocation.getArgument(0);
		Submodel submodel = invocation.getArgument(1);
		if (!mockedStorageHasKey(key)) {
			return null;
		}
		mockedStorage.put(key, submodel);
		return submodel;
	}

	private Submodel mockedRawRetrieve(InvocationOnMock invocation) {
		String key = invocation.getArgument(0);
		if (!mockedStorageHasKey(key)) {
			return null;
		}
		return mockedStorage.get(key);
	}

	private Collection<Submodel> mockedRawRetrieveAll(InvocationOnMock invocation) {
		return this.mockedStorage.values();
	}

	private boolean mockedDelete(InvocationOnMock invocation) {
		String key = invocation.getArgument(0);
		if (!mockedStorageHasKey(key)) {
			return false;
		}
		mockedStorage.remove(key);
		return true;
	}

	private boolean mockedStorageHasKey(String identificationId) {
		return mockedStorage.containsKey(identificationId);
	}
}
