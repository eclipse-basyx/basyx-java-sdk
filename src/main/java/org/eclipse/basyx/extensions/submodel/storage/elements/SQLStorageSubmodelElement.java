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

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name = "StorageSubmodelElement")
public class SQLStorageSubmodelElement implements IStorageSubmodelElement {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private String id;
	private String submodelId;
	private Timestamp timestamp;
	private String idShort;
	private String operation;
	private String serializedElementValue;

	@Override
	public String getSubmodelId() {
		return submodelId;
	}

	@Override
	public void setSubmodelId(String submodelId) {
		this.submodelId = submodelId;
	}

	@Override
	public Timestamp getTimestamp() {
		return timestamp;
	}

	@Override
	public void setTimestamp(Timestamp timestamp2) {
		this.timestamp = timestamp2;
	}

	@Override
	public String getOperation() {
		return operation;
	}

	@Override
	public void setOperation(String operation) {
		this.operation = operation;
	}

	@Override
	public String getIdShort() {
		return idShort;
	}

	@Override
	public void setIdShort(String idShort) {
		this.idShort = idShort;
	}

	@Override
	public String getSerializedElementValue() {
		return serializedElementValue;
	}

	@Override
	public void setSerializedElementValue(String serializedElementValue) {
		this.serializedElementValue = serializedElementValue;
	}
}
