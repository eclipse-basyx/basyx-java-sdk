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

public interface IStorageSubmodelElement {
	public String getSubmodelId();

	public void setSubmodelId(String submodelId);

	public Timestamp getTimestamp();

	public void setTimestamp(Timestamp timestamp);

	public String getElementIdShortPath();

	public void setElementIdShortPath(String elementIdShortPath);

	public String getOperation();

	public void setOperation(String operation);

	public String getModelType();

	public void setModelType(String modelType);

	public String getModelTypeSpecial();

	public void setModelTypeSpecial(String modelTypeSpecial);

	public String getSerializedElementValue();

	public void setSerializedElementValue(String serializedElementValue);
}
