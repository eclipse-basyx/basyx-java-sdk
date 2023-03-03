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

import java.util.Collection;

/**
 * Provides basic methods to write data into or read and delete data from a
 * persistence storage
 * 
 * @author jungjan
 */
public interface IBaSyxStorageAPI<T> {

	/**
	 * Creates or updates objects in the persistence storage
	 * 
	 * @param obj
	 *            The object to be created or updated
	 * @return The created or updated object if successful
	 */
	public T createOrUpdate(T obj);

	/**
	 * Updates objects in the persistence storage
	 * 
	 * @param obj
	 *            The object with the updated information
	 * @param key
	 *            The key of the object to be updated
	 * @return The updated object if successful
	 */
	public T update(T obj, String key);

	/**
	 * Retrieves an object by it's key from the persistence storage
	 * 
	 * @param key
	 *            The key of the object to be retrieved
	 * @return The expected object if successful
	 */
	public T retrieve(String key);

	/**
	 * Retrieves all object of one type from the persistence storage Collection
	 * items)
	 * 
	 * @return All objects of the specified type that are persistent in the storage
	 */
	public Collection<T> retrieveAll();

	/**
	 * Deletes an object by it's key from the persistence storage
	 * 
	 * @param key
	 *            The key of the object to be deleted
	 * @return true if successful else false
	 */
	public boolean delete(String key);

	/**
	 * Creates a collection if it does not already exist, whereby "collection" is a
	 * data container in the context of the respective persistence storage. For the
	 * concrete persistence storage, another name may be common: e.g. for relational
	 * database systems: "table", for S3: "bucket".
	 * 
	 * @param collectionName
	 *            The name of the collection
	 */
	public void createCollectionIfNotExists(String collectionName);

	/**
	 * Deletes a collection, whereby "collection" is a data container in the context
	 * of the respective persistence storage. For the concrete persistence storage,
	 * another name may be common: e.g. for relational database systems: "table",
	 * for S3: "bucket".
	 */
	public void deleteCollection();
}
