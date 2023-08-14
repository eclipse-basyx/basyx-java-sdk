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
package org.eclipse.basyx.submodel.metamodel.map.dataspecification;

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IDataSpecificationContent;
import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IDataSpecificationIEC61360Content;
import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IEmbeddedDataSpecification;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.vab.model.VABModelMap;

public class EmbeddedDataSpecification extends VABModelMap<Object> implements IEmbeddedDataSpecification {
	public static final String CONTENT = "dataSpecificationContent";
	public static final String DATASPECIFICATION = "dataSpecification";

	/**
	 * Creates a EmbeddedDataSpecification object from a map
	 * 
	 * @param map
	 *            a EmbeddedDataSpecification object as raw map
	 * @return a EmbeddedDataSpecification object, that behaves like a facade for
	 *         the given map
	 */
	public static EmbeddedDataSpecification createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}

		EmbeddedDataSpecification ret = new EmbeddedDataSpecification();
		ret.setMap(map);
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IReference getDataSpecificationTemplate() {
		return Reference.createAsFacade((Map<String, Object>) get(DATASPECIFICATION));
	}

	public void setDataSpecificationTemplate(IReference ref) {
		put(DATASPECIFICATION, ref);
	}

	@SuppressWarnings("unchecked")
	@Override
	public IDataSpecificationContent getContent() {
		return DataSpecificationContent.createAsFacade((Map<String, Object>) get(CONTENT));
	}

	public void setContent(IDataSpecificationContent content) {
		put(CONTENT, content);
	}
}
