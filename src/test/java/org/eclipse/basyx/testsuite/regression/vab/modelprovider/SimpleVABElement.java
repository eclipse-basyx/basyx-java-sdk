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
