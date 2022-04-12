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
package org.eclipse.basyx.vab.modelprovider.lambda;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Helper class which allows to easily create properties as processed by the
 * {@link org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider}
 * 
 * @author schnicke, espen
 *
 */
public class VABLambdaProviderHelper {
	/**
	 * Creates a property referencing a simple value, e.g. int, double, ..
	 * 
	 * @param get
	 *            Method used to get the value
	 * @param set
	 *            Method used to set the value
	 * @return
	 */
	public static Map<String, Object> createSimple(Supplier<Object> get, Consumer<Object> set) {
		Map<String, Object> value = new LinkedHashMap<>();
		value.put(VABLambdaHandler.VALUE_GET_SUFFIX, get);
		value.put(VABLambdaHandler.VALUE_SET_SUFFIX, set);
		return value;
	}

	/**
	 * Creates a property referencing a map
	 * 
	 * @param get
	 *            Method used to get the map
	 * @param set
	 *            Method used to set the map
	 * @param insert
	 *            Method used to insert an element into the map
	 * @param removeObject
	 *            Method used to remove an object from the map
	 * @param removeKey
	 *            Method used to remove a key from the map
	 * @return
	 */
	public static Map<String, Object> createMap(Supplier<?> get, Consumer<?> set, BiConsumer<String, Object> insert,
			Consumer<Object> removeObject, Consumer<String> removeKey) {
		Map<String, Object> value = new LinkedHashMap<>();
		value.put(VABLambdaHandler.VALUE_GET_SUFFIX, get);
		value.put(VABLambdaHandler.VALUE_SET_SUFFIX, set);
		value.put(VABLambdaHandler.VALUE_REMOVEOBJ_SUFFIX, removeObject);
		value.put(VABLambdaHandler.VALUE_REMOVEKEY_SUFFIX, removeKey);
		value.put(VABLambdaHandler.VALUE_INSERT_SUFFIX, insert);
		return value;
	}

	/**
	 * Creates a property referencing a collection
	 * 
	 * @param get
	 *            Method used to get the collection
	 * @param set
	 *            Method used to set the collection
	 * @param insert
	 *            Method used to insert an element into the map
	 * @param removeObject
	 *            Method used to remove an object from the map
	 * @param removeKey
	 *            Method used to remove a key from the map
	 * @return
	 */
	public static Map<String, Object> createCollection(Supplier<?> get, Consumer<?> set, Consumer<Object> insert,
			Consumer<Object> removeObject, Consumer<String> removeKey) {
		Map<String, Object> value = new LinkedHashMap<>();
		value.put(VABLambdaHandler.VALUE_GET_SUFFIX, get);
		value.put(VABLambdaHandler.VALUE_SET_SUFFIX, set);
		value.put(VABLambdaHandler.VALUE_REMOVEOBJ_SUFFIX, removeObject);
		value.put(VABLambdaHandler.VALUE_REMOVEKEY_SUFFIX, removeKey);
		value.put(VABLambdaHandler.VALUE_INSERT_SUFFIX, insert);
		return value;
	}
}
