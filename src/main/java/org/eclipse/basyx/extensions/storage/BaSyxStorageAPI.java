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
package org.eclipse.basyx.extensions.storage;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.IIdentifiable;

public abstract class BaSyxStorageAPI<T> implements IBaSyxStorageAPI<T> {

	protected final String COLLECTION_NAME;

	public BaSyxStorageAPI(String collectionName) {
		COLLECTION_NAME = collectionName;
	}

	/**
	 * DISCLAIMER: Currently only supports the extraction of keys from
	 * IIdentifiables.
	 * his is a helper method that extracts a key from an object that can be used
	 * to update information of the object in the persistence store without
	 * explicitly specifying this key.
	 * 
	 * @param obj
	 *            An object that contains a key that can be used to find the
	 *            persisted version of the object.
	 * @return The key
	 */
	protected String getKey(T obj) {
		if (!(obj instanceof IIdentifiable)) {
			throw new IllegalArgumentException("Can only extract a key from a object of type " + IIdentifiable.class.getName());
		}
		return ((IIdentifiable) obj).getIdentification().getId();
	}
}
