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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.eclipse.basyx.vab.exception.provider.ProviderException;

/**
 * A simple VAB model that explains the use of the VAB primitives
 * 
 * @author kuhn, espen
 *
 */
public class SimpleVABElement extends LinkedHashMap<String, Object> {
	public static final String EXCEPTION_MESSAGE = "Exception description";

	private static final long serialVersionUID = 3942399852711325850L;

	private static Object consumed;

	public static Object getAndResetConsumed() {
		Object tmp = consumed;
		consumed = null;
		return tmp;
	}

	private static boolean runnableRan = false;

	public static boolean getAndResetRunnableRan() {
		boolean tmp = runnableRan;
		runnableRan = false;
		return tmp;
	}

	/**
	 * Constructor for a simple VAB element that contains all data types
	 */
	public SimpleVABElement() {
		Map<String, Object> primitives = createPrimitiveTypes();
		put("primitives", primitives);

		Map<String, Object> functions = createFunctions();
		put("operations", functions);

		Map<String, Object> structure = createStructureTypes();
		put("structure", structure);

		Map<String, Object> special = new LinkedHashMap<>();
		special.putAll(createCaseSensitiveEntries());

		Map<String, Object> nestedA = createNestedMap();
		special.put("nested", nestedA);

		special.put("null", null);

		put("special", special);
	}

	private Map<String, Object> createNestedMap() {
		Map<String, Object> nestedA = new LinkedHashMap<>();
		Map<String, Object> nestedB = new LinkedHashMap<>();
		nestedA.put("nested", nestedB);
		nestedB.put("value", 100);
		return nestedA;
	}

	private Map<String, Object> createCaseSensitiveEntries() {
		Map<String, Object> caseSensitive = new LinkedHashMap<>();
		caseSensitive.put("casesensitivity", true);
		caseSensitive.put("caseSensitivity", false);
		return caseSensitive;
	}

	private Map<String, Object> createStructureTypes() {
		Map<String, Object> structure = new LinkedHashMap<>();
		structure.put("map", new LinkedHashMap<String, Object>());
		structure.put("set", new HashSet<Object>());
		structure.put("list", new ArrayList<Object>());
		return structure;
	}

	private Map<String, Object> createFunctions() {
		Map<String, Object> functions = new LinkedHashMap<>();
		functions.put("supplier", (Supplier<Object>) () -> {
			return true;
		});

		functions.put("consumer", (Consumer<Object>) (o) -> {
			consumed = o;
		});

		functions.put("runnable", new Runnable() {

			@Override
			public void run() {
				runnableRan = true;
			}
		});

		functions.put("providerException", (Function<Object[], Object>) (param) -> {
			throw new ProviderException(EXCEPTION_MESSAGE);
		});
		functions.put("nullException", (Function<Object[], Object>) (param) -> {
			throw new NullPointerException();
		});

		functions.put("complex", (Function<Object[], Object>) (param) -> {
			return (int) param[0] + (int) param[1];
		});
		functions.put("serializable", (Function<Object[], Object> & Serializable) (param) -> {
			return (int) param[0] + (int) param[1];
		});
		functions.put("invalid", true);
		functions.put("invokable", (Function<Object[], Object>) (param) -> {
			return true;
		});
		return functions;
	}

	private Map<String, Object> createPrimitiveTypes() {
		Map<String, Object> primitives = new LinkedHashMap<>();
		primitives.put("integer", 123);
		primitives.put("double", 3.14d);
		primitives.put("string", "TestValue");
		return primitives;
	}
}
