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

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

public class StorageSubmodelElementQueryBuilder {
	protected EntityManager entityManager;
	protected String submodelId;
	protected String elementIdShortPath;
	protected Timestamp begin;
	protected Timestamp end;

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

		if (begin != null && end != null) {
			query.setParameter("begin", begin);
			query.setParameter("end", end);
		}

		return query;
	}

	private String createQueryString() {
		String queryString = "SELECT s from StorageSubmodelElement s WHERE ";
		StorageSubmodelElementFilterBuilder filterBuilder = new StorageSubmodelElementFilterBuilder();
		if (submodelId != null) {
			filterBuilder.setSubmodelIdFilter();
		}

		if (elementIdShortPath != null) {
			filterBuilder.setElementIdShortPathFilter();
		}

		if (begin != null && end != null) {
			filterBuilder.setTimespanFilter();
		}
		queryString += filterBuilder.build();
		queryString += " ORDER BY s.timestamp DESC, s.operationId DESC";
		return queryString;
	}

	public StorageSubmodelElementQueryBuilder setSubmodelId(String submodelId) {
		this.submodelId = submodelId;
		return this;
	}

	public StorageSubmodelElementQueryBuilder setElementIdShort(String elementIdShort) {
		this.elementIdShortPath = elementIdShort;
		return this;
	}

	public StorageSubmodelElementQueryBuilder setTimespan(Timestamp begin, Timestamp end) {
		this.begin = begin;
		this.end = end;
		return this;
	}

	// public StorageSubmodelQueryBuilder setOrder(String orderBy) {
	// // add order by?
	// return this;
	// }
}
