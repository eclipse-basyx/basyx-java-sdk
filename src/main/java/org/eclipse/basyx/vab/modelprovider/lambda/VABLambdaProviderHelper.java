/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.modelprovider.lambda;

import java.util.HashMap;
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
		Map<String, Object> value = new HashMap<>();
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
		Map<String, Object> value = new HashMap<>();
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
		Map<String, Object> value = new HashMap<>();
		value.put(VABLambdaHandler.VALUE_GET_SUFFIX, get);
		value.put(VABLambdaHandler.VALUE_SET_SUFFIX, set);
		value.put(VABLambdaHandler.VALUE_REMOVEOBJ_SUFFIX, removeObject);
		value.put(VABLambdaHandler.VALUE_REMOVEKEY_SUFFIX, removeKey);
		value.put(VABLambdaHandler.VALUE_INSERT_SUFFIX, insert);
		return value;
	}
}
