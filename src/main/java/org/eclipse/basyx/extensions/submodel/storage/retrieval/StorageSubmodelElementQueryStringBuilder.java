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

import org.eclipse.basyx.extensions.submodel.storage.retrieval.filters.StorageRetrievalOperationAbbreviations;
import org.eclipse.basyx.extensions.submodel.storage.retrieval.filters.StorageSubmodelElementFilter;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;

public class StorageSubmodelElementQueryStringBuilder {
	public static final String BETWEEN_START_SUFFIX = "_START";
	public static final String BETWEEN_END_SUFFIX = "_END";
	private static final String QUERY_AND = " AND ";
	private String filterString;
	private String order;
	private String defaultOrder = "s.timestamp DESC, s.operationId DESC";

	public StorageSubmodelElementQueryStringBuilder() {
		this.filterString = "";
	}

	public String build() {
		String queryString = "SELECT s from StorageSubmodelElement s";

		if (filterString != null) {
			queryString += " WHERE ";
			queryString += filterString;
		}

		queryString += " ORDER BY ";
		if (order != null) {
			queryString += order;
		} else {
			queryString += defaultOrder;
		}

		return queryString;
	}

	public StorageSubmodelElementQueryStringBuilder setSubmodelIdFilter() {
		if (!filterString.isEmpty()) {
			filterString += QUERY_AND;
		}
		filterString += "s.submodelId = :submodelId";
		return this;
	}

	public StorageSubmodelElementQueryStringBuilder setElementIdShortPathFilter() {
		if (!filterString.isEmpty()) {
			filterString += QUERY_AND;
		}
		filterString += "s.elementIdShortPath = :elementIdShortPath";
		return this;
	}

	public StorageSubmodelElementQueryStringBuilder setOperationFilter() {
		if (!filterString.isEmpty()) {
			filterString += QUERY_AND;
		}
		filterString += "s.operation = :operation";
		return this;
	}

	public StorageSubmodelElementQueryStringBuilder setParameterEqualFilter(String key) {
		if (!filterString.isEmpty()) {
			filterString += QUERY_AND;
		}
		filterString += "s." + key + " = :" + key;
		return this;
	}

	public StorageSubmodelElementQueryStringBuilder setParameterNotEqualFilter(String key) {
		if (!filterString.isEmpty()) {
			filterString += QUERY_AND;
		}
		filterString += "s." + key + " != :" + key;
		return this;
	}

	public StorageSubmodelElementQueryStringBuilder setParameterGreaterThanFilter(String key) {
		if (!filterString.isEmpty()) {
			filterString += QUERY_AND;
		}
		filterString += "s." + key + " > :" + key;
		return this;
	}

	public StorageSubmodelElementQueryStringBuilder setParameterLessThanFilter(String key) {
		if (!filterString.isEmpty()) {
			filterString += QUERY_AND;
		}
		filterString += "s." + key + " < :" + key;
		return this;
	}

	public StorageSubmodelElementQueryStringBuilder setParameterGreaterOrEqualFilter(String key) {
		if (!filterString.isEmpty()) {
			filterString += QUERY_AND;
		}
		filterString += "s." + key + " >= :" + key;
		return this;
	}

	public StorageSubmodelElementQueryStringBuilder setParameterLessOrEqualFilter(String key) {
		if (!filterString.isEmpty()) {
			filterString += QUERY_AND;
		}
		filterString += "s." + key + " <= :" + key;
		return this;
	}

	public StorageSubmodelElementQueryStringBuilder setParameterBetweenFilter(String key) {
		if (!filterString.isEmpty()) {
			filterString += QUERY_AND;
		}
		filterString += "s." + key + " BETWEEN :" + key + BETWEEN_START_SUFFIX + " AND :" + key + BETWEEN_END_SUFFIX;
		return this;
	}

	public StorageSubmodelElementQueryStringBuilder setParameterFilter(StorageSubmodelElementFilter filter) {
		String key = filter.getKey();
		switch (filter.getFilterOperation()) {
		case StorageRetrievalOperationAbbreviations.EQUAL:
			setParameterEqualFilter(key);
			break;
		case StorageRetrievalOperationAbbreviations.NOT_EQUAL:
			setParameterNotEqualFilter(key);
			break;
		case StorageRetrievalOperationAbbreviations.GREATER_THAN:
			setParameterGreaterThanFilter(key);
			break;
		case StorageRetrievalOperationAbbreviations.GREATER_OR_EQUAL:
			setParameterGreaterOrEqualFilter(key);
			break;
		case StorageRetrievalOperationAbbreviations.LESS_THAN:
			setParameterLessThanFilter(key);
			break;
		case StorageRetrievalOperationAbbreviations.LESS_OR_EQUAL:
			setParameterLessOrEqualFilter(key);
			break;
		case StorageRetrievalOperationAbbreviations.BETWEEN:
			setParameterBetweenFilter(key);
			break;
		default:
			throw new MalformedRequestException("Unkown filter operation: " + filter.getFilterOperation());
		}
		return this;
	}

}
