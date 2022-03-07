/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package org.eclipse.basyx.extensions.submodel.storage.elements;

public class StorageSubmodelElementFilterBuilder {
	protected String filterString;

	public StorageSubmodelElementFilterBuilder() {
		this.filterString = "";
	}

	public String build() {
		return filterString;
	}

	public StorageSubmodelElementFilterBuilder setSubmodelIdFilter() {
		if (!filterString.isEmpty()) {
			filterString += " AND ";
		}
		filterString += "s.submodelId = :submodelId";
		return this;
	}

	public StorageSubmodelElementFilterBuilder setElementIdShortPathFilter() {
		if (!filterString.isEmpty()) {
			filterString += " AND ";
		}
		filterString += "s.elementIdShortPath = :elementIdShortPath";
		return this;
	}

	public StorageSubmodelElementFilterBuilder setTimespanFilter() {
		if (!filterString.isEmpty()) {
			filterString += " AND ";
		}
		filterString += "s.timestamp BETWEEN :begin AND :end";
		return this;
	}
}
