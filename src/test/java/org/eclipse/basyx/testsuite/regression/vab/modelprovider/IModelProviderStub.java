/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
