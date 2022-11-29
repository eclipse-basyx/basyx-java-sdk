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
package org.eclipse.basyx.vab.coder.json.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

/**
 * Provides means for (de-)serialization of Primitives (int, double, string,
 * boolean), Maps, Sets and Lists. <br>
 * Since JSON is not able to differentiate between Sets and Lists, additional
 * information is added. When a Collection of objects is serialized, this
 * information is directly added using an "index" key. <br>
 * However, collections of primitives do not allow adding an "index" key. To
 * handle this, a type tag is added on the same level as the collection. For
 * more details, see <i>TestJson</i>
 * 
 * @author rajashek, schnicke
 *
 */
public class GSONTools implements Serializer {

	private static Logger logger = LoggerFactory.getLogger(GSONTools.class);

	// Used string constants
	public static final String OPERATION = "operation";
	public static final String LAMBDA = "lambda";
	public static final String BASYXFUNCTIONTYPE = "_basyxFunctionType";
	public static final String BASYXINVOCABLE = "_basyxInvocable";
	public static final String BASYXFUNCTIONVALUE = "_basyxFunctionValue";

	/**
	 * JsonParser reference
	 */
	protected static JsonParser parser = new JsonParser();

	/**
	 * Type factory
	 */
	protected GSONToolsFactory toolsFactory = null;

	/**
	 * Flag to remove null values from serialized JSON
	 */
	private boolean removeNull = true;

	/**
	 * Flag to remove empty arrays from serialized JSON
	 */
	private boolean removeEmpty = false;

	/**
	 * Constructor
	 */
	public GSONTools(GSONToolsFactory factory) {
		// Store factory reference
		toolsFactory = factory;
	}

	/**
	 * Constructor
	 */
	public GSONTools(GSONToolsFactory factory, boolean removeNull, boolean removeEmpty) {
		this(factory);
		this.removeNull = removeNull;
		this.removeEmpty = removeEmpty;
	}

	/**
	 * Set factory instance
	 */
	public void setFactory(GSONToolsFactory newFactoryInstance) {
		// Store factory instance
		toolsFactory = newFactoryInstance;
	}

	@Override
	public Object deserialize(String str) {
		JsonElement elem = parser.parse(str);
		return deserializeJsonElement(elem);
	}

	@Override
	public String serialize(Object obj) {
		JsonElement elem = serializeObject(obj);
		StringBuilder writer = new StringBuilder();
		Gson gson = new Gson();
		if (elem.isJsonObject()) {
			serializeJsonObject(elem, writer, gson);
			return writer.toString();
		}
		if (removeNull) {
			gson.toJson(elem, writer);
		} else {
			writer.append(elem.toString());
		}
		return writer.toString();
	}

	private void serializeJsonObject(JsonElement elem, StringBuilder writer, Gson gson) {
		if (elem.getAsJsonObject().asMap().isEmpty()) {
			writer.append(gson.toJson(elem));
			return;
		}
		writer.append("{");
		for (Entry<String, JsonElement> en : elem.getAsJsonObject().entrySet()) {
			if (removeNull && !isNullValue(en)) {
				String jsonString = gson.toJson(createJsonObject4Entity(en));
				appendJsonToPreviousSerialization(writer, jsonString);
			} else {
				String jsonString = createJsonObject4Entity(en).toString();
				appendJsonToPreviousSerialization(writer, jsonString);
			}
		}
		writer.deleteCharAt(writer.lastIndexOf(","));
		writer.append("}");
	}

	private void appendJsonToPreviousSerialization(StringBuilder writer, String jsonString) {
		writer.append(jsonString.substring(1, jsonString.length() - 1));
		writer.append(String.format(","));
	}

	private JsonObject createJsonObject4Entity(Entry<String, JsonElement> en) {
		JsonObject jobj = new JsonObject();
		jobj.add(en.getKey(), en.getValue());
		return jobj;
	}

	private boolean isNullValue(Entry<String, JsonElement> en) {
		return en.getValue().isJsonNull();
	}

	/**
	 * Serialized an arbitrary object to a JsonElement
	 * 
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private JsonElement serializeObject(Object obj) {
		if (obj == null) {
			return JsonNull.INSTANCE;
		} else if (obj.getClass().isPrimitive() || isWrapperType(obj.getClass()) || obj instanceof String || obj instanceof Number) {
			return serializePrimitive(obj);
		} else if (obj instanceof Map<?, ?>) {
			return serializeMap((Map<String, Object>) obj);
		} else if (obj instanceof Collection<?>) {
			return serializeCollection((Collection<Object>) obj);
		} else if (isFunction(obj)) {
			return serializeFunction(obj);
		}
		throw new RuntimeException("Unknown element!");
	}

	/**
	 * Deserializes a JsonElement to an object
	 * 
	 * @param elem
	 * @return
	 */
	private Object deserializeJsonElement(JsonElement elem) {
		if (elem.isJsonPrimitive()) {
			return deserializeJsonPrimitive(elem.getAsJsonPrimitive());
		} else if (elem.isJsonObject()) {
			return deserializeJsonObject(elem.getAsJsonObject());
		} else if (elem.isJsonArray()) {
			return deserializeJsonArray(elem.getAsJsonArray());
		}
		return null;
	}

	/**
	 * Indicates if a class is a wrapper type, e.g. <i>Integer</i> for <i>int</i>
	 * 
	 * @param clazz
	 * @return
	 */
	private static boolean isWrapperType(Class<?> clazz) {
		return clazz.equals(Boolean.class) || clazz.equals(Integer.class) || clazz.equals(Character.class) || clazz.equals(Byte.class) || clazz.equals(Short.class) || clazz.equals(Double.class) || clazz.equals(Long.class)
				|| clazz.equals(Float.class);
	}

	/**
	 * Deserializes a JsonPrimitive to either string, int, double or boolean
	 * 
	 * @param primitive
	 * @return
	 */
	private Object deserializeJsonPrimitive(JsonPrimitive primitive) {
		if (primitive.isNumber()) {
			if (primitive.getAsString().contains(".")) {
				return primitive.getAsDouble();
			} else {
				// Get value as Big integer
				BigInteger tmp = primitive.getAsBigInteger();
				if (BigInteger.valueOf(Integer.MAX_VALUE).compareTo(tmp) >= 0 && BigInteger.valueOf(Integer.MIN_VALUE).compareTo(tmp) <= 0) {
					// convert to int
					return primitive.getAsInt();
				} else if (BigInteger.valueOf(Long.MAX_VALUE).compareTo(tmp) >= 0 && BigInteger.valueOf(Long.MIN_VALUE).compareTo(tmp) <= 0) {
					// convert to long
					return primitive.getAsLong();
				} else {
					// for types NonNegativeInteger, NonPositiveInteger, NegativeInteger,
					// PositiveInteger
					return tmp;
				}
			}
		} else if (primitive.isBoolean()) {
			return primitive.getAsBoolean();
		} else {
			return primitive.getAsString();
		}
	}

	/**
	 * Serializes either string, number or boolean to a JsonPrimitive
	 * 
	 * @param primitive
	 * @return
	 */
	private JsonPrimitive serializePrimitive(Object primitive) {
		if (primitive instanceof Number) {
			return new JsonPrimitive((Number) primitive);
		} else if (primitive instanceof Boolean) {
			return new JsonPrimitive((Boolean) primitive);
		} else {
			return new JsonPrimitive((String) primitive);
		}
	}

	/**
	 * Deserializes a JsonObject to either a map, an operations or an arbitrary
	 * serializable object
	 * 
	 * @param map
	 * @return
	 */
	private Object deserializeJsonObject(JsonObject map) {
		if (map.has(BASYXFUNCTIONTYPE)) {
			String functionType = map.get(BASYXFUNCTIONTYPE).getAsString();
			if (functionType.equals(OPERATION)) {
				return BASYXINVOCABLE;
			} else if (functionType.equals(LAMBDA)) {
				return deserializeObjectFromString(map.get(BASYXFUNCTIONVALUE).getAsString());
			} else {
				throw new RuntimeException("Unknown function type " + functionType + "!");
			}
		} else {
			return deserializeObject(map);
		}
	}

	/**
	 * Deserializes a JsonObject to a map
	 * 
	 * @param map
	 * @return
	 */
	private Map<String, Object> deserializeObject(JsonObject map) {
		Map<String, Object> ret = toolsFactory.createMap();
		for (String k : map.keySet()) {
			ret.put(k, deserializeJsonElement(map.get(k)));
		}
		return ret;
	}

	/**
	 * Serializes a Map to a JsonObject
	 * 
	 * @param map
	 * @return
	 */
	private JsonObject serializeMap(Map<String, Object> map) {
		JsonObject obj = new JsonObject();
		for (Entry<String, Object> entry : map.entrySet()) {
			Object value = entry.getValue();
			// Remove empty list if removeEmpty flag is on
			if (!removeEmpty || !(value instanceof Collection<?> && ((Collection<?>) value).isEmpty())) {
				obj.add(entry.getKey(), serializeObject(value));
			}
		}
		return obj;
	}

	/**
	 * Deserializes a JsonArray to a Collection<br>
	 * Remark: internally, a List will be used for deserialization & it is assumed,
	 * that the order in the json equals the correct intended order for the list. =>
	 * The ordering will be preserved in the returned collection
	 * 
	 * @param array
	 * @return
	 */
	private Collection<Object> deserializeJsonArray(JsonArray array) {
		Collection<Object> list = toolsFactory.createCollection();
		for (JsonElement element : array) {
			list.add(deserializeJsonElement(element));
		}
		return list;
	}

	/**
	 * Checks if an object is a lambda function
	 * 
	 * @param value
	 * @return
	 */
	private boolean isFunction(Object value) {
		return (value instanceof Supplier<?>) || (value instanceof Function<?, ?>) || (value instanceof Consumer<?>) || (value instanceof BiConsumer<?, ?> || (value instanceof Runnable));
	}

	/**
	 * Serializes a list to a JsonArray and adds index where appropriate
	 * 
	 * @param list
	 * @return
	 */
	private JsonArray serializeCollection(Collection<Object> collection) {
		JsonArray array = new JsonArray();
		collection.stream().map(this::serializeObject).forEach(array::add);
		return array;
	}

	/**
	 * Serializes a function if possible
	 * 
	 * @param function
	 * @return
	 */
	private JsonObject serializeFunction(Object function) {
		if (function instanceof Serializable) {
			return serializeSerializableOperation((Serializable) function);
		} else {
			return serializeNotSerializableOperation(function);
		}
	}

	/**
	 * Read an object from Base64 string.
	 */
	protected Object deserializeObjectFromString(String s) {
		Object result = null;
		byte[] data = Base64.getDecoder().decode(s);
		InputStream byteStream = new ByteArrayInputStream(data);

		try (ObjectInputStream stream = new ObjectInputStream(byteStream)) {
			result = stream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			logger.error("Exception in deserializeObjectFromString", e);
		}
		return result;
	}

	/**
	 * Write the object to a Base64 string.
	 */
	protected String serializeObjectToString(Serializable obj) {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		try (ObjectOutputStream oos = new ObjectOutputStream(outStream)) {
			oos.writeObject(obj);
		} catch (IOException e) {
			logger.error("Exception in serializeObjectToString", e);
		}

		byte[] data = outStream.toByteArray();
		return Base64.getEncoder().encodeToString(data);
	}

	/**
	 * Serialize an operation descriptor
	 */
	private JsonObject serializeSerializableOperation(Serializable value) {
		JsonObject target = new JsonObject();
		// Serializable functions will be serialized.
		target.add(BASYXFUNCTIONTYPE, new JsonPrimitive(LAMBDA));

		String serialized = serializeObjectToString(value);
		target.add(BASYXFUNCTIONVALUE, new JsonPrimitive(serialized));

		return target;
	}

	/**
	 * Serializes a NonSerializableOperation to a String indicating that fact
	 * 
	 * @param function
	 * @return
	 */
	private JsonObject serializeNotSerializableOperation(Object function) {
		JsonObject target = new JsonObject();
		// Not serializable functions will be not be serialized.
		target.add(BASYXFUNCTIONTYPE, new JsonPrimitive(OPERATION));

		return target;
	}
}
