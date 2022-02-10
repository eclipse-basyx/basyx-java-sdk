/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package org.eclipse.basyx.extensions.submodel.storage;

import java.sql.Timestamp;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

public class StorageSubmodelQueryBuilder {
	protected EntityManager entityManager;
	protected String submodelId;
	protected String idShort;
	protected Timestamp begin;
	protected Timestamp end;

	public StorageSubmodelQueryBuilder(EntityManager givenManager) {
		entityManager = givenManager;
	}

	public Query build() {
		String queryString = createQueryString();
		Query query = entityManager.createQuery(queryString);

		if (submodelId != null) {
			query.setParameter("submodelId", submodelId);
		}

		if (idShort != null) {
			query.setParameter("id", idShort);
		}

		if (begin != null && end != null) {
			query.setParameter("begin", begin);
			query.setParameter("end", end);
		}

		return query;
	}

	private String createQueryString() {
		String queryString = "SELECT s from StorageSubmodelElement s WHERE ";
		StorageSubmodelFilterBuilder filterBuilder = new StorageSubmodelFilterBuilder();
		if (submodelId != null) {
			filterBuilder.setSubmodelIdFilter();
		}

		if (idShort != null) {
			filterBuilder.setIdShortFilter();
		}

		if (begin != null && end != null) {
			filterBuilder.setTimespanFilter();
		}
		queryString += filterBuilder.build();
		queryString += " ORDER BY s.timestamp DESC, s.id DESC";
		return queryString;
	}

	public StorageSubmodelQueryBuilder setSubmodelId(String submodelId) {
		this.submodelId = submodelId;
		return this;
	}

	public StorageSubmodelQueryBuilder setIdShort(String idShort) {
		this.idShort = idShort;
		return this;
	}

	public StorageSubmodelQueryBuilder setTimespan(Timestamp begin, Timestamp end) {
		this.begin = begin;
		this.end = end;
		return this;
	}

	// public StorageSubmodelQueryBuilder setOrder(String orderBy) {
	// // add order by?
	// return this;
	// }
}
