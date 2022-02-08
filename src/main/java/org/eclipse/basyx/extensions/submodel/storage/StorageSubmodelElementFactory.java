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

import org.eclipse.basyx.extensions.submodel.storage.elements.IStorageSubmodelElement;
import org.eclipse.basyx.extensions.submodel.storage.elements.NoSQLStorageSubmodelElement;
import org.eclipse.basyx.extensions.submodel.storage.elements.SQLStorageSubmodelElement;

public class StorageSubmodelElementFactory {
	private StorageSubmodelElementFactory() {
	}

	public static IStorageSubmodelElement create(String storageClassname) {
		final String NOSQL_CLASSNAME = "org.eclipse.basyx.extensions.submodel.storage.elements.NoSQLStorageSubmodelElement";
		final String SQL_CLASSNAME = "org.eclipse.basyx.extensions.submodel.storage.elements.SQLStorageSubmodelElement";

		if (storageClassname.equals(SQL_CLASSNAME)) {
			return new SQLStorageSubmodelElement();
		} else if (storageClassname.equals(NOSQL_CLASSNAME)) {
			return new NoSQLStorageSubmodelElement();
		}

		return null;
	}
}
