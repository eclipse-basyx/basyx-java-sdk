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
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;

public class StorageSubmodelAPIHelper {
	public static Object getSubmodelElementCreation(ISubmodel submodel, String idShortPath, ISubmodelElement elem) {
		StorageSubmodelElement element = getShared(submodel, idShortPath);

		element.setOperation(StorageSubmodelElementOperations.CREATE);
		element.setSerializedElementValue(elem.getValue().toString());

		return element;
	}

	public static Object getSubmodelElementUpdate(ISubmodel submodel, String idShortPath, Object newValue) {
		StorageSubmodelElement element = getShared(submodel, idShortPath);

		element.setOperation(StorageSubmodelElementOperations.UPDATE);
		element.setSerializedElementValue(newValue.toString());

		return element;
	}

	public static Object getSubmodelElementDeletion(ISubmodel submodel, String idShortPath) {
		StorageSubmodelElement element = getShared(submodel, idShortPath);

		element.setOperation(StorageSubmodelElementOperations.DELETE);

		return element;
	}

	private static StorageSubmodelElement getShared(ISubmodel submodel, String idShortPath) {
		StorageSubmodelElement element = new StorageSubmodelElement();

		element.setSubmodelId(submodel.getIdentification().getId());
		element.setIdShort(idShortPath);
		element.setTimestamp(new Timestamp(System.currentTimeMillis()));

		return element;
	}

	private static List<String> getParentKeys(ISubmodel submodel) {
		return submodel.getParent().getKeys().stream().map(s -> s.getValue()).collect(Collectors.toList());
	}

	public static class StorageSubmodelElementOperations {
		public static String CREATE = "CREATE";
		public static String UPDATE = "UPDATE";
		public static String DELETE = "DELETE";
	}
}
