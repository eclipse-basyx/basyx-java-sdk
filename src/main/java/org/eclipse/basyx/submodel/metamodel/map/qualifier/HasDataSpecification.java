/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.map.qualifier;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IEmbeddedDataSpecification;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IHasDataSpecification;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.dataspecification.EmbeddedDataSpecification;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.reference.ReferenceHelper;
import org.eclipse.basyx.vab.model.VABModelMap;

/**
 * HasDataSpecification class
 * 
 * @author elsheikh, schnicke
 *
 */
public class HasDataSpecification extends VABModelMap<Object> implements IHasDataSpecification {
	public static final String DATASPECIFICATION = "dataSpecification";
	public static final String EMBEDDEDDATASPECIFICATIONS = "embeddedDataSpecifications";
	/**
	 * Constructor
	 */
	public HasDataSpecification() {
		put(DATASPECIFICATION, new HashSet<Reference>());
		put(EMBEDDEDDATASPECIFICATIONS, new HashSet<IEmbeddedDataSpecification>());
	}

	/**
	 * Constructor
	 */
	public HasDataSpecification(Collection<IEmbeddedDataSpecification> embedded, Collection<IReference> ref) {
		put(DATASPECIFICATION, ref);
		put(EMBEDDEDDATASPECIFICATIONS, embedded);
	}

	/**
	 * Creates a DataSpecificationIEC61360 object from a map
	 * 
	 * @param map
	 *            a DataSpecificationIEC61360 object as raw map
	 * @return a DataSpecificationIEC61360 object, that behaves like a facade for
	 *         the given map
	 */
	public static HasDataSpecification createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}

		HasDataSpecification ret = new HasDataSpecification();
		ret.setMap(map);
		return ret;
	}

	@Override
	public Collection<IReference> getDataSpecificationReferences() {
		return ReferenceHelper.transform(get(DATASPECIFICATION));
	}

	public void setDataSpecificationReferences(Collection<IReference> refs) {
		put(DATASPECIFICATION, refs);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<IEmbeddedDataSpecification> getEmbeddedDataSpecifications() {
		Collection<Map<String, Object>> collection = (Collection<Map<String, Object>>) get(EMBEDDEDDATASPECIFICATIONS);
		return collection == null ? new HashSet<IEmbeddedDataSpecification>() : collection.stream().map(EmbeddedDataSpecification::createAsFacade).collect(Collectors.toSet());
	}

	public void setEmbeddedDataSpecifications(Collection<IEmbeddedDataSpecification> embeddedDataSpecifications) {
		put(EMBEDDEDDATASPECIFICATIONS, embeddedDataSpecifications);
	}
}
