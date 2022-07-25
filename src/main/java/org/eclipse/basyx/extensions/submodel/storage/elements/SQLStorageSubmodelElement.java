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

import org.eclipse.persistence.annotations.Index;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity(name = "StorageSubmodelElement")
@Index(name = "search_index", columnNames = { "submodelId", "elementIdShortPath" })
public class SQLStorageSubmodelElement implements IStorageSubmodelElement {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private String operationId; // serial number
	private Timestamp timestamp; // current timestamp
	@Index
	private String submodelId; // corresponding submodelId
	@Index
	private String elementIdShortPath; // own idShortPath
	private String operation; // CREATE/UPDATE/DELETE
	private String modelType; // Property, File, SubmodelCollection?
	private String modelTypeSpecial; // Kind of Property, ...
	@Lob // allow for large texts
	private String serializedElementValue; // value in serialized form

	@Override
	public Timestamp getTimestamp() {
		return timestamp;
	}

	@Override
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String getSubmodelId() {
		return submodelId;
	}

	@Override
	public void setSubmodelId(String submodelId) {
		this.submodelId = submodelId;
	}

	@Override
	public String getElementIdShortPath() {
		return elementIdShortPath;
	}

	@Override
	public void setElementIdShortPath(String elementIdShortPath) {
		this.elementIdShortPath = elementIdShortPath;
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
	public String getModelType() {
		return modelType;
	}

	@Override
	public void setModelType(String modelType) {
		this.modelType = modelType;
	}

	@Override
	public String getModelTypeSpecial() {
		return modelTypeSpecial;
	}

	@Override
	public void setModelTypeSpecial(String modelTypeSpecial) {
		this.modelTypeSpecial = modelTypeSpecial;
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
