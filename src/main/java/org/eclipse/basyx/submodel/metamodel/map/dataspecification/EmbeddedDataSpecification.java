/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
	 * @return a EmbeddedDataSpecification object, that behaves like a facade for the given map
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
		// Assume the concent complies to the IEC61360 template
		// => only this template is supported currently
		return DataSpecificationIEC61360Content.createAsFacade((Map<String, Object>) get(CONTENT));
	}

	public void setContent(IDataSpecificationIEC61360Content content) {
		put(CONTENT, content);
	}
}
