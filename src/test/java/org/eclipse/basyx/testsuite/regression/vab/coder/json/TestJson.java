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
package org.eclipse.basyx.testsuite.regression.vab.coder.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.basyx.vab.coder.json.serialization.DefaultTypeFactory;
import org.eclipse.basyx.vab.coder.json.serialization.GSONTools;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Tests if the json serialization is behaving as expected
 * 
 * @author schnicke
 *
 */
public class TestJson {

	GSONTools tools = new GSONTools(new DefaultTypeFactory(), false, false);

	/**
	 * Tests if a double is correctly (de-)serialized
	 */
	@Test
	public void testDouble() {
		testDeserializePrimitive(12.3);

		JsonPrimitive primitive = new JsonPrimitive(12.3);
		assertEquals(primitive.toString(), tools.serialize(12.3));
	}
	
	/**
	 * Tests if an integer is correctly (de-)serialized
	 */
	@Test
	public void testInteger() {
		testDeserializePrimitive(12);

		JsonPrimitive primitive = new JsonPrimitive(12);
		assertEquals(primitive.toString(), tools.serialize(12));
	}

	@Test
	public void testBigInteger() {
		BigInteger dec = new BigInteger("10000000000000000000000000000000000000");
		BigInteger deserialized = (BigInteger) tools.deserialize(tools.serialize(dec));
		assertEquals(dec, deserialized);
	}

	/**
	 * Tests if a boolean is correctly (de-)serialized
	 */
	@Test
	public void testBoolean() {
		testDeserializePrimitive(false);

		JsonPrimitive primitive = new JsonPrimitive(false);
		assertEquals(primitive.toString(), tools.serialize(false));
	}

	/**
	 * Tests if a string is correctly (de-)serialized
	 */
	@Test
	public void testString() {
		testDeserializePrimitive("HelloWorld");
		testDeserializePrimitive("12.2.2");

		JsonPrimitive primitive = new JsonPrimitive("HelloWorld");
		assertEquals(primitive.toString(), tools.serialize("HelloWorld"));
	}

	/**
	 * Tests if a map is correctly (de-)serialized
	 */
	@Test
	public void testMap() {
		String str = "{\"a\": { \"x\" : 123}, \"b\": \"123\"}";

		Map<String, Object> expected = new LinkedHashMap<>();
		Map<String, Object> a = new LinkedHashMap<>();
		a.put("x", 123);
		expected.put("a", a);
		expected.put("b", "123");

		assertEquals(expected, tools.deserialize(str));
		
		
		JsonObject aObj = new JsonObject();
		aObj.add("x", new JsonPrimitive(123));
		
		JsonObject expectedObj = new JsonObject();
		expectedObj.add("a", aObj);
		expectedObj.add("b", new JsonPrimitive("123"));

		assertEquals(expectedObj.toString(), tools.serialize(expected));
	}

	/**
	 * Tests if a map containing a collection is (de-)serialized correctly
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testMapWithCollection() {
		String str = "{\"a\": [1,2,3]}";

		Map<String, Object> listMap = new LinkedHashMap<>();
		listMap.put("a", Arrays.asList(1, 2, 3));
		
		Map<String, Object> setMap = new LinkedHashMap<>();
		setMap.put("a", Sets.newHashSet(1, 2, 3));
		
		Map<String, Object> deserialized = (Map<String, Object>) tools.deserialize(str);
		Collection<Object> coll = (Collection<Object>) deserialized.get("a");
		assertEquals(1, deserialized.size());
		assertEquals(3, coll.size());
		assertTrue(coll.contains(1));
		assertTrue(coll.contains(2));
		assertTrue(coll.contains(3));
		
		deserialized = (Map<String, Object>) tools.deserialize(tools.serialize(listMap));
		coll = (Collection<Object>) deserialized.get("a");
		assertEquals(1, deserialized.size());
		assertEquals(3, coll.size());
		assertTrue(coll.contains(1));
		assertTrue(coll.contains(2));
		assertTrue(coll.contains(3));
		
		deserialized = (Map<String, Object>) tools.deserialize(tools.serialize(setMap));
		coll = (Collection<Object>) deserialized.get("a");
		assertEquals(1, deserialized.size());
		assertEquals(3, coll.size());
		assertTrue(coll.contains(1));
		assertTrue(coll.contains(2));
		assertTrue(coll.contains(3));
	}

	/**
	 * Tests if a list is correctly (de-)serialized
	 */
	@Test
	public void testList() {
		String strComplexOrdered = "[{\"a\": 2}, {\"a\": false}]";
		List<Map<String, Object>> orderedCollection = new ArrayList<>();
		orderedCollection.add(Collections.singletonMap("a", 2));
		orderedCollection.add(Collections.singletonMap("a", false));

		assertEquals(orderedCollection, tools.deserialize(strComplexOrdered));

		JsonObject o1 = new JsonObject();
		o1.add("a", new JsonPrimitive(2));
		JsonObject o2 = new JsonObject();
		o2.add("a", new JsonPrimitive(false));
		JsonArray ordered = new JsonArray();
		ordered.add(o1);
		ordered.add(o2);

		assertEquals(ordered.toString(), tools.serialize(orderedCollection));

		List<Integer> primitiveList = new ArrayList<>();
		primitiveList.add(1);

		assertEquals(primitiveList, tools.deserialize(tools.serialize(primitiveList)));
	}

	/**
	 * Tests if an empty list is correctly (de-)serialized
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testEmptyList() {
		List<String> list = new ArrayList<>();
		String serialized = tools.serialize(list);
		Collection<Object> coll = (Collection<Object>) tools.deserialize(serialized);
		assertTrue(coll.isEmpty());
	}

	/**
	 * Tests edge case of a map containing an empty list
	 */
	@Test
	public void testEmptyListInMap() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("a", new ArrayList<>());
		assertEquals(map, tools.deserialize(tools.serialize(map)));
	}

	/**
	 * Tests if a set is correctly (de-)serialized
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSet() {
		String strComplexUnordered = "[{\"a\": 4}]";

		Set<Map<String, Object>> unorderedSet = new HashSet<>();
		unorderedSet.add(Collections.singletonMap("a", 4));
		
		Collection<Map<String, Object>> deserialized = (Collection<Map<String, Object>>) tools
				.deserialize(strComplexUnordered);
		assertEquals(1, deserialized.size());
		assertEquals(4, deserialized.iterator().next().get("a"));
		
		JsonArray unorderedArray = new JsonArray();
		JsonObject o1 = new JsonObject();
		o1.add("a", new JsonPrimitive(4));
		unorderedArray.add(o1);
		
		assertEquals(unorderedArray.toString(), tools.serialize(unorderedSet));

		Set<Integer> primitiveSet = new HashSet<>();
		primitiveSet.add(1);
		Collection<Object> deserializedColl = (Collection<Object>) tools.deserialize(tools.serialize(primitiveSet));
		assertEquals(1, deserializedColl.size());
		assertTrue(deserializedColl.contains(1));
	}

	/**
	 * Tests if an empty set is correctly (de-)serialized
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testEmptySet() {
		Set<String> set = new HashSet<>();
		String serialized = tools.serialize(set);
		Collection<Object> coll = (Collection<Object>) tools.deserialize(serialized);
		assertTrue(coll.isEmpty());
	}

	/**
	 * Tests edge case of a map containing an empty set
	 */
	@Test
	public void testEmptySetInMap() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("test", new ArrayList<>());
		String result = tools.serialize(map);
		assertEquals("{\"test\":[]}", result);
		assertEquals(map, tools.deserialize(tools.serialize(map)));
	}

	/**
	 * Tests if a nonserializable function is correctly serialized.
	 */
	@Test
	public void testNonSerializableFunction() {
		JsonObject functionObject = new JsonObject();
		functionObject.add(GSONTools.BASYXFUNCTIONTYPE, new JsonPrimitive(GSONTools.OPERATION));

		Consumer<Integer> testConsumer = x -> {
		};

		assertEquals(functionObject.toString(), tools.serialize(testConsumer));
		
		
		String operation = GSONTools.BASYXINVOCABLE;
		assertEquals(operation, tools.deserialize(functionObject.toString()));

	}

	/**
	 * Tests if a serializable function is correctly (de-)serialized.
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSerializableFunction() throws IOException {
		Function<Integer, Integer> testFunction = (Function<Integer, Integer> & Serializable) (x -> x * x);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		// Try to serialize object
		ObjectOutputStream oos = new ObjectOutputStream(outStream);
		oos.writeObject(testFunction);
		oos.close();

		// Try to encode to string
		String encoded = Base64.getEncoder().encodeToString(outStream.toByteArray());

		JsonObject functionObject = new JsonObject();
		functionObject.add(GSONTools.BASYXFUNCTIONTYPE, new JsonPrimitive(GSONTools.LAMBDA));
		functionObject.add(GSONTools.BASYXFUNCTIONVALUE, new JsonPrimitive(encoded));

		assertEquals(functionObject.toString(), tools.serialize(testFunction));

		Function<Integer, Integer> deserialized = (Function<Integer, Integer>) tools.deserialize(functionObject.toString());

		assertEquals(testFunction.apply(5), deserialized.apply(5));
	}
	
	/**
	 * Tests if null values and empty arrays are getting removed successfully
	 * with remove flag on
	 * 
	 */
	@Test
	public void testSerializeWithRemoveFlagsOn() throws IOException {
		GSONTools toolWithRemoveFlagOn = new GSONTools(new DefaultTypeFactory(), true, true);
		Map<String, Object> expected = new LinkedHashMap<>();
		Map<String, Object> a = new LinkedHashMap<>();
		a.put("x", 123);
		expected.put("a", a);
		expected.put("b", "123");
		expected.put("c", null);
		expected.put("d", new ArrayList<String>());
		
		JsonObject aObj = new JsonObject();
		aObj.add("x", new JsonPrimitive(123));
		
		JsonObject expectedObj = new JsonObject();
		expectedObj.add("a", aObj);
		expectedObj.add("b", new JsonPrimitive("123"));

		assertEquals(expectedObj.toString(), toolWithRemoveFlagOn.serialize(expected));
	}

	/**
	 * Tests for an arbitrary primitive object if it is deserialized correctly
	 * 
	 * @param toTest
	 */
	private void testDeserializePrimitive(Object toTest) {
		Object obj = tools.deserialize(toTest.toString());
		assertTrue(obj.getClass().equals(toTest.getClass()));
		assertEquals(toTest, obj);
	}
}
