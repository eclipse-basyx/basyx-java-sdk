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
package org.eclipse.basyx.submodel.metamodel.map.qualifier.haskind;

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.IHasKind;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.vab.model.VABModelMap;

/**
 * HasKind class
 * 
 * @author elsheikh, schnicke
 *
 */
public class HasKind extends VABModelMap<Object> implements IHasKind {
	public static final String KIND = "kind";

	/**
	 * Constructor
	 */
	public HasKind() {
	}

	/**
	 * Constructor that takes {@link ModelingKind} (either Kind.Instance or
	 * Kind.Type)
	 */
	public HasKind(ModelingKind kind) {
		// Kind of the element: either type or instance.

		put(KIND, kind.toString());
	}

	/**
	 * Creates a HasKind object from a map
	 * 
	 * @param map
	 *            a HasKind object as raw map
	 * @return a HasKind object, that behaves like a facade for the given map
	 */
	public static HasKind createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}

		HasKind ret = new HasKind();
		ret.setMap(map);
		return ret;
	}

	/**
	 * @deprecated Please use {@link #getKind()} instead.
	 */
	@Override
	public ModelingKind getModelingKind() {
		return this.getKind();
	}

	/**
	 * @deprecated Please use {@link #setKind(ModelingKind)} instead.
	 */
	public void setModelingKind(ModelingKind kind) {
		this.setKind(kind);
	}

	@Override
	public ModelingKind getKind() {
		String str = (String) get(HasKind.KIND);
		return ModelingKind.fromString(str);
	}

	public void setKind(ModelingKind kind) {
		if (kind != null) {
			put(HasKind.KIND, kind.toString());
		} else {
			put(HasKind.KIND, null);
		}
	}

}
