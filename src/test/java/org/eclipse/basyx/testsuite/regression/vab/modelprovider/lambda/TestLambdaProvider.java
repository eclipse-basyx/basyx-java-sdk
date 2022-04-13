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
package org.eclipse.basyx.testsuite.regression.vab.modelprovider.lambda;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.basyx.testsuite.regression.vab.modelprovider.SimpleVABElement;
import org.eclipse.basyx.testsuite.regression.vab.modelprovider.TestProvider;
import org.eclipse.basyx.testsuite.regression.vab.protocol.http.TestsuiteDirectory;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProviderHelper;
import org.eclipse.basyx.vab.protocol.api.ConnectorFactory;

/**
 * Tests the functionality of the VABLambdaProvider according to the test cases
 * in the snippet package
 * 
 * @author schnicke, espen
 *
 */
public class TestLambdaProvider extends TestProvider {

	private static double doubleElement = 3.14d;
	private static Collection<Object> collectionElement = new ArrayList<>();
	private static Map<String, Object> rootAccessor;
	private static LinkedHashMap<String, Object> rootElement = new SimpleVABElement();
	@SuppressWarnings("unchecked")
	private static LinkedHashMap<String, Object> structureElement = (LinkedHashMap<String, Object>) rootElement.get("structure");
	@SuppressWarnings("unchecked")
	private static LinkedHashMap<String, Object> mapElement = (LinkedHashMap<String, Object>) structureElement.get("map");

	protected VABConnectionManager connManager = new VABConnectionManager(new TestsuiteDirectory(), new ConnectorFactory() {

		@Override
		protected IModelProvider createProvider(String addr) {
			return buildProvider();
		}
	});

	@Override
	protected VABConnectionManager getConnectionManager() {
		return connManager;
	}

	@SuppressWarnings("unchecked")
	private static IModelProvider buildProvider() {
		// Create primitive lambda elements
		LinkedHashMap<String, Object> primitives = (LinkedHashMap<String, Object>) rootElement.get("primitives");
		// Has no hidden setter (==null), so value should be completely replaced when
		// set
		primitives.put("integer", VABLambdaProviderHelper.createSimple((Supplier<Object>) () -> {
			return 123;
		}, null));
		// Has a hidden setter, so write access to this element changes "doubleElement",
		// which is returned by the getter
		primitives.put("double", VABLambdaProviderHelper.createSimple((Supplier<Object>) () -> {
			return doubleElement;
		}, (Consumer<Object>) (newObject) -> {
			doubleElement = (double) newObject;
		}));

		// Create collection lambda element
		LinkedHashMap<String, Object> collections = (LinkedHashMap<String, Object>) rootElement.get("structure");
		Map<String, Object> collectionAccessor = VABLambdaProviderHelper.createCollection((Supplier<Object>) () -> {
			return collectionElement;
		}, (Consumer<Collection<Object>>) (collection) -> {
			collectionElement = new ArrayList<>(collection);
		}, (Consumer<Object>) (value) -> {
			collectionElement.add(value);
		}, (Consumer<Object>) (object) -> {
			collectionElement.remove(object);
		}, (Consumer<String>) (key) -> {
		});
		collections.put("list", collectionAccessor);

		// Create accessors for map property propertyMap
		rootAccessor = VABLambdaProviderHelper.createMap((Supplier<?>) () -> {
			return rootElement;
		}, (Consumer<Map<String, Object>>) (map) -> {
			rootElement = (LinkedHashMap<String, Object>) map;
		}, (BiConsumer<String, Object>) (key, value) -> {
			rootElement.put(key, value);
		}, (Consumer<Object>) (o) -> {
		}, (Consumer<String>) (key) -> {
			rootElement.remove(key);
		});

		// Create first accessor for structure/map element (-> test nested lambda
		// properties)
		Map<String, Object> structureAccessor = VABLambdaProviderHelper.createMap((Supplier<?>) () -> {
			return structureElement;
		}, (Consumer<Map<String, Object>>) (map) -> {
			structureElement = (LinkedHashMap<String, Object>) map;
		}, (BiConsumer<String, Object>) (key, value) -> {
			structureElement.put(key, value);
		}, (Consumer<Object>) (o) -> {
		}, (Consumer<String>) (key) -> {
			structureElement.remove(key);
		});
		// Replace actual structure property with lambda accessor
		rootElement.put("structure", structureAccessor);
		// Create second accessor for structure/map element (-> nested lambda
		// properties)
		Map<String, Object> mapAccessor = VABLambdaProviderHelper.createMap((Supplier<?>) () -> {
			return mapElement;
		}, (Consumer<Map<String, Object>>) (map) -> {
			mapElement = (LinkedHashMap<String, Object>) map;
		}, (BiConsumer<String, Object>) (key, value) -> {
			mapElement.put(key, value);
		}, (Consumer<Object>) (o) -> {
		}, (Consumer<String>) (key) -> {
			mapElement.remove(key);
		});
		// Replace actual map property with lambda accessor
		structureElement.put("map", mapAccessor);

		VABLambdaProvider provider = new VABLambdaProvider(rootAccessor);
		return provider;
	}
}
