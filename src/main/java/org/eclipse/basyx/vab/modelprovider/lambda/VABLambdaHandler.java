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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.map.VABMapHandler;

/**
 * VABHandler that can additionally handle maps with hidden
 * get/set/delete/invoke properties.
 * 
 * @author schnicke, espen
 *
 */
public class VABLambdaHandler extends VABMapHandler {
	public static final String VALUE_SET_SUFFIX = "set";
	public static final String VALUE_GET_SUFFIX = "get";
	public static final String VALUE_INSERT_SUFFIX = "insert";
	public static final String VALUE_REMOVEKEY_SUFFIX = "removeKey";
	public static final String VALUE_REMOVEOBJ_SUFFIX = "removeObject";

	@Override
	public Object postprocessObject(Object element) {
		return super.postprocessObject(resolveAll(element));
	}

	@Override
	public Object getElementProperty(Object element, String propertyName) {
		return super.getElementProperty(resolveSingle(element), propertyName);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setModelPropertyValue(Object element, String propertyName, Object newValue) {
		Object child = null;
		try {
			child = getElementProperty(element, propertyName);
		} catch (ResourceNotFoundException e) {}
		if (hasHiddenSetter(child)) {
			((Consumer<Object>) ((Map<String, Object>) child).get(VALUE_SET_SUFFIX)).accept(newValue);
		} else if (hasHiddenInserter(element) && (resolveSingle(element) instanceof Map<?, ?>)) {
			((BiConsumer<String, Object>) ((Map<String, Object>) element).get(VALUE_INSERT_SUFFIX)).accept(propertyName,
					newValue);
		} else {
			super.setModelPropertyValue(resolveSingle(element), propertyName, newValue);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void createValue(Object element, Object newValue) {
		if (hasHiddenInserter(element)) {
			((Consumer<Object>) ((Map<String, Object>) element).get(VALUE_INSERT_SUFFIX)).accept(newValue);
		} else {
			super.createValue(element, newValue);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteValue(Object element, String propertyName) {
		if (hasHiddenKeyRemover(element)) {
			super.getElementProperty(resolveSingle(element), propertyName);
			Consumer<String> c = (Consumer<String>) ((Map<String, Object>) element).get(VALUE_REMOVEKEY_SUFFIX);
			c.accept(propertyName);
		} else {
			super.deleteValue(element, propertyName);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteValue(Object element, Object property) {
		if (hasHiddenObjectRemover(element)) {
			if (resolveSingle(element) instanceof Map) {
				// Can not delete by value from Maps
				throw new MalformedRequestException("Could not delete property from a map.");
			}
			Consumer<Object> c = (Consumer<Object>) ((Map<String, Object>) element).get(VALUE_REMOVEOBJ_SUFFIX);
			c.accept(property);
		} else {
			super.deleteValue(element, property);
		}
	}

	@SuppressWarnings("unchecked")
	private Object resolveSingle(Object o) {
		while (hasHiddenGetter(o)) {
			Map<?, ?> map = (Map<?, ?>) o;
			o = ((Supplier<Object>) map.get(VALUE_GET_SUFFIX)).get();
		}
		return o;
	}

	/**
	 * Checks if a value is a raw value or points to a gettable property and
	 * resolves the underlying structure
	 */
	@SuppressWarnings("unchecked")
	private Object resolveAll(Object o) {
		o = resolveSingle(o);
		if (o instanceof Map<?, ?>) {
			return resolveMap((Map<String, Object>) o);
		} else if (o instanceof Collection<?>) {
			return resolveCollection((Collection<Object>) o);
		} else {
			return o;
		}
	}

	private Object resolveMap(Map<String, Object> map) {
		Map<String, Object> ret = new LinkedHashMap<>();
		for (String s : map.keySet()) {
			ret.put(s, resolveAll(map.get(s)));
		}
		return ret;
	}

	private Object resolveCollection(Collection<Object> coll) {
		List<Object> ret = new ArrayList<>(coll.size());
		for (Object o : coll) {
			ret.add(resolveAll(o));
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	private boolean hasHiddenInserter(Object elem) {
		if (elem instanceof Map<?, ?>) {
			Map<String, Object> map = (Map<String, Object>) elem;
			Object o = map.get(VALUE_INSERT_SUFFIX);
			return o instanceof BiConsumer<?, ?> || o instanceof Consumer<?>;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean hasHiddenObjectRemover(Object elem) {
		if (elem instanceof Map<?, ?>) {
			Map<String, Object> map = (Map<String, Object>) elem;
			Object o = map.get(VALUE_REMOVEOBJ_SUFFIX);
			return o instanceof Consumer<?>;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean hasHiddenKeyRemover(Object elem) {
		if (elem instanceof Map<?, ?>) {
			Map<String, Object> map = (Map<String, Object>) elem;
			Object o = map.get(VALUE_REMOVEKEY_SUFFIX);
			return o instanceof Consumer<?>;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean hasHiddenSetter(Object elem) {
		if (elem instanceof Map<?, ?>) {
			Map<String, Object> map = (Map<String, Object>) elem;
			Object o = map.get(VALUE_SET_SUFFIX);
			return o instanceof Consumer<?>;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean hasHiddenGetter(Object elem) {
		if (elem instanceof Map<?, ?>) {
			Map<String, Object> map = (Map<String, Object>) elem;
			Object o = map.get(VALUE_GET_SUFFIX);
			return o instanceof Supplier<?>;
		}
		return false;
	}
}
