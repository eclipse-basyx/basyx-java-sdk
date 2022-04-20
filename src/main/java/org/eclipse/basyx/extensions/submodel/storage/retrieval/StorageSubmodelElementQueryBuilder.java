/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package org.eclipse.basyx.extensions.submodel.storage.retrieval;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.basyx.extensions.submodel.storage.retrieval.filters.StorageSubmodelElementFilter;
import org.eclipse.basyx.extensions.submodel.storage.retrieval.filters.StorageSubmodelElementFilterFactory;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

public class StorageSubmodelElementQueryBuilder {
	protected EntityManager entityManager;
	protected String submodelId;
	protected String elementIdShortPath;
	protected boolean countEnabled;
	protected ArrayList<StorageSubmodelElementFilter> filterList = new ArrayList<>();

	public StorageSubmodelElementQueryBuilder(EntityManager givenManager) {
		entityManager = givenManager;
	}

	public Query build() {
		String queryString = createQueryString();
		Query query = createQuery(queryString);

		if (submodelId != null) {
			query.setParameter("submodelId", submodelId);
		}

		if (elementIdShortPath != null) {
			query.setParameter("elementIdShortPath", elementIdShortPath);
		}

		for (StorageSubmodelElementFilter filter : filterList) {
			filter.getParameterMap().forEach(query::setParameter);
		}

		return query;
	}

	private Query createQuery(String queryString) {
		try {
			return entityManager.createQuery(queryString);
		} catch (IllegalArgumentException e) {
			throw new MalformedRequestException(e);
		}
	}

	private String createQueryString() {
		StorageSubmodelElementQueryStringBuilder queryStringBuilder = new StorageSubmodelElementQueryStringBuilder();
		if (submodelId != null) {
			queryStringBuilder.setSubmodelIdFilter();
		}

		if (elementIdShortPath != null) {
			queryStringBuilder.setElementIdShortPathFilter();
		}

		if (filterList != null) {
			for (StorageSubmodelElementFilter filter : filterList) {
				queryStringBuilder.setParameterFilter(filter);
			}
		}

		if (countEnabled) {
			queryStringBuilder.enableCount();
		}

		return queryStringBuilder.build();
	}

	public StorageSubmodelElementQueryBuilder setSubmodelId(String submodelId) {
		this.submodelId = submodelId;
		return this;
	}

	public StorageSubmodelElementQueryBuilder setElementIdShort(String elementIdShort) {
		this.elementIdShortPath = elementIdShort;
		return this;
	}

	public StorageSubmodelElementQueryBuilder setParameters(Map<String, String> queryParameters) {
		if (queryParameters != null) {
			for (Entry<String, String> entry : queryParameters.entrySet()) {
				filterList.addAll(StorageSubmodelElementFilterFactory.createAllFilters(entry));
			}
		}
		return this;
	}

	public StorageSubmodelElementQueryBuilder enableCount() {
		countEnabled = true;
		return this;
	}

}
