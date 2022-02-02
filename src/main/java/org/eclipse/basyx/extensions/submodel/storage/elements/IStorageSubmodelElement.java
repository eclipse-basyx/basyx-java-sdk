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

	public void setTimestamp(Timestamp timestamp2);

	public String getOperation();

	public void setOperation(String operation);

	public String getIdShort();

	public void setIdShort(String idShort);

	public String getSerializedElementValue();

	public void setSerializedElementValue(String serializedElementValue);
}
