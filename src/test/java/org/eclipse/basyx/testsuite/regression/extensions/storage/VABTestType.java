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

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IAdministrativeInformation;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IIdentifiable;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.vab.model.VABModelMap;

public class VABTestType extends VABModelMap<Object> implements IIdentifiable {
	// Keys
	public static final String TEST_ID = "testId";
	public static final String TEST_STR = "testStr";
	public static final String TEST_INT = "testInt";
	public static final String TEST_ARRAY = "testArray";
	public static final String TEST_COLLECTION = "testCollection";
	public static final String TEST_MAP = "testMap";
	public static final String IDENTIFICATION = "identification";

	// DefaultValues
	public static final String DEFAULT_ID = "test_id";
	public static final String DEFAULT_STR = "str";
	public static final int DEFAULT_INT = 42;
	public static final int[] DEFAULT_ARRAY = { -1, 0, 1 };

	private static final String[] STRING_ARRAY = { DEFAULT_STR + 0, DEFAULT_STR + 1, DEFAULT_STR + 2 };
	public static final Collection<String> DEFAULT_COLLECTION = Arrays.asList(STRING_ARRAY);
	public static final Map<String, String> DEFAULT_MAP = DEFAULT_COLLECTION.stream().collect(Collectors.toMap(entry -> entry.substring(entry.length() - 1), entry -> entry));

	public VABTestType() {
		setId(DEFAULT_ID);
		setTestStr(DEFAULT_STR);
		setTestInt(DEFAULT_INT);
		setTestArray(DEFAULT_ARRAY);
		setTestCollection(DEFAULT_COLLECTION);
		setTestMap(DEFAULT_MAP);
	}

	public VABTestType(String id, String testStr, int testInt, int[] testArray, Collection<String> testCollection, Map<String, String> testMap) {
		setId(id);
		setTestStr(testStr);
		setTestInt(testInt);
		setTestArray(testArray);
		setTestCollection(testCollection);
		setTestMap(testMap);
	}

	// copy constructor
	public VABTestType(VABTestType clone) {
		super(clone.map);
	}

	public void setId(String id) {
		IIdentifier identification = getIdentification();
		if (identification == null) {
			setIdentification(new Identifier());
			identification = getIdentification();
		}
		((Identifier) identification).setId(id);
	}


	public String getTestStr() {
		return (String) get(TEST_STR);
	}

	public void setTestStr(String testStr) {
		put(TEST_STR, testStr);
	}

	public int getTestInt() {
		return (int) get(TEST_INT);
	}

	public void setTestInt(int testInt) {
		put(TEST_INT, testInt);
	}

	public int[] getTestArray() {
		return (int[]) get(TEST_ARRAY);
	}

	public void setTestArray(int[] testArray) {
		put(TEST_ARRAY, testArray);
	}

	@SuppressWarnings("unchecked")
	public Collection<String> getTestCollection() {
		return (Collection<String>) get(TEST_COLLECTION);
	}

	public void setTestCollection(Collection<String> testCollection) {
		put(TEST_COLLECTION, testCollection);
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getTestMap() {
		return (Map<String, String>) get(TEST_MAP);
	}

	public void setTestMap(Map<String, String> testMap) {
		put(TEST_MAP, testMap);
	}

	public void setIdentification(IIdentifier identifier) {
		put(IDENTIFICATION, identifier);
	}

	@Override
	public IIdentifier getIdentification() {
		return (IIdentifier) get(IDENTIFICATION);
	}

	@Override
	public String getIdShort() {
		throw new NotImplementedException();
	}

	@Override
	public String getCategory() {
		throw new NotImplementedException();
	}

	@Override
	public LangStrings getDescription() {
		throw new NotImplementedException();
	}

	@Override
	public IReference getParent() {
		throw new NotImplementedException();
	}

	@Override
	public IReference getReference() {
		throw new NotImplementedException();
	}

	@Override
	public IAdministrativeInformation getAdministration() {
		throw new NotImplementedException();
	}
}
