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

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.basyx.extensions.submodel.storage.retrieval.StorageSubmodelElementFilter.StorageRetrievalOperationAbbreviations;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

public class StorageSubmodelElementQueryBuilder {
	protected EntityManager entityManager;
	protected String submodelId;
	protected String elementIdShortPath;
	protected ArrayList<StorageSubmodelElementFilter> filterList = new ArrayList<>();

	public StorageSubmodelElementQueryBuilder(EntityManager givenManager) {
		entityManager = givenManager;
	}

	public Query build() {
		String queryString = createQueryString();
		Query query = entityManager.createQuery(queryString);

		if (submodelId != null) {
			query.setParameter("submodelId", submodelId);
		}

		if (elementIdShortPath != null) {
			query.setParameter("elementIdShortPath", elementIdShortPath);
		}

		for (StorageSubmodelElementFilter filter : filterList) {
			// TODO: work with specialized filters to avoid this clutter
			if (filter.getKey().equals("timestamp")) {
				if (filter.getFilterOperation().equals(StorageRetrievalOperationAbbreviations.BETWEEN)) {
					String[] values = filter.getValue().split(",");
					query.setParameter(filter.getKey() + StorageSubmodelElementQueryStringBuilder.BETWEEN_START_SUFFIX, createTimestamp(values[0]));
					query.setParameter(filter.getKey() + StorageSubmodelElementQueryStringBuilder.BETWEEN_END_SUFFIX, createTimestamp(values[1]));
				} else {
					query.setParameter(filter.getKey(), createTimestamp(filter.getValue()));
				}
			} else {
				query.setParameter(filter.getKey(), filter.getValue());
			}
		}

		return query;
	}

	private Timestamp createTimestamp(String date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return new Timestamp(df.parse(date.trim()).getTime());
		} catch (ParseException e) {
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
				// TODO: create specialized element filters with interface
				filterList.add(new StorageSubmodelElementFilter(entry));
			}
		}
		return this;
	}

}
