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

public class StorageSubmodelFilterBuilder {
	protected String filterString;
	protected String submodelId;
	protected String idShort;
	protected Timestamp begin;
	protected Timestamp end;

	public StorageSubmodelFilterBuilder() {
		this.filterString = "";
	}

	public String build() {
		return filterString;
	}

	public StorageSubmodelFilterBuilder setSubmodelIdFilter() {
		if (!filterString.isEmpty()) {
			filterString += " AND ";
		}
		filterString += "s.submodelId = :submodelId";
		return this;
	}

	public StorageSubmodelFilterBuilder setIdShortFilter() {
		if (!filterString.isEmpty()) {
			filterString += " AND ";
		}
		filterString += "s.idShort = :id";
		return this;
	}

	public StorageSubmodelFilterBuilder setTimespanFilter() {
		if (!filterString.isEmpty()) {
			filterString += " AND ";
		}
		filterString += "s.timestamp BETWEEN :begin AND :end";
		return this;
	}
}
