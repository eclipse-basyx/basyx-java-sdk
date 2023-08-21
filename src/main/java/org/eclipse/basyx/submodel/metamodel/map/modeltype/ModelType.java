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
package org.eclipse.basyx.submodel.metamodel.map.modeltype;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.basyx.vab.model.VABModelMap;

/**
 * Describes the type of the used model and is used to add a model type to
 * existing AAS meta model entries. <br>
 * Needed for C# compatibility
 *
 * @author schnicke
 *
 */
public class ModelType extends VABModelMap<Object> {
	public static final String MODELTYPE = "modelType";
	public static final String NAME = "name";

	public ModelType(String type) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put(NAME, type);
		put(MODELTYPE, map);
	}

	private ModelType() {
	}

	/**
	 * Creates a DataSpecification object from a map
	 *
	 * @param map
	 *            a DataSpecification object as raw map
	 * @return a DataSpecification object, that behaves like a facade for
	 *         the given map
	 */
	public static ModelType createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}

		ModelType ret = new ModelType();
		ret.setMap(map);
		return ret;
	}

	@SuppressWarnings("unchecked")
	public String getName() {
		if (get(ModelType.MODELTYPE) == null)
			return null;
		return (String) ((Map<String, Object>) get(ModelType.MODELTYPE)).get(ModelType.NAME);
	}

}
