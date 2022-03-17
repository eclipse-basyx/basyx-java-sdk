/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package org.eclipse.basyx.extensions.submodel.storage.api;

import java.util.Collection;

import org.eclipse.basyx.extensions.submodel.storage.StorageSubmodelElementComponent;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;

import jakarta.persistence.EntityManager;

public class StorageSubmodelAPI implements ISubmodelAPI {

	protected ISubmodelAPI submodelAPI;
	protected EntityManager entityManager;
	protected StorageSubmodelElementComponent submodelElementStorageComponent;

	public StorageSubmodelAPI(ISubmodelAPI submodelAPI, EntityManager entityManager) {
		this.submodelAPI = submodelAPI;
		this.entityManager = entityManager;
		this.submodelElementStorageComponent = new StorageSubmodelElementComponent(this.entityManager);
	}

	public StorageSubmodelAPI(ISubmodelAPI submodelAPI, EntityManager entityManager, String submodelElementStorageOption) {
		this.submodelAPI = submodelAPI;
		this.entityManager = entityManager;
		this.submodelElementStorageComponent = new StorageSubmodelElementComponent(this.entityManager, submodelElementStorageOption);
	}

	@Override
	public ISubmodel getSubmodel() {
		return submodelAPI.getSubmodel();
	}

	@Override
	public ISubmodelElement getSubmodelElement(String idShortPath) {
		return submodelAPI.getSubmodelElement(idShortPath);
	}

	@Override
	public Collection<IOperation> getOperations() {
		return submodelAPI.getOperations();
	}

	@Override
	public Collection<ISubmodelElement> getSubmodelElements() {
		return submodelAPI.getSubmodelElements();
	}

	@Override
	public Object getSubmodelElementValue(String idShortPath) {
		return submodelAPI.getSubmodelElementValue(idShortPath);
	}

	@Override
	public Object invokeOperation(String idShortPath, Object... params) {
		return submodelAPI.invokeOperation(idShortPath, params);
	}

	@Override
	public Object invokeAsync(String idShortPath, Object... params) {
		return submodelAPI.invokeAsync(idShortPath, params);
	}

	@Override
	public Object getOperationResult(String idShort, String requestId) {
		return submodelAPI.getOperationResult(idShort, requestId);
	}

	@Override
	public void addSubmodelElement(ISubmodelElement elem) {
		try {
			submodelElementStorageComponent.beginTransaction();
			submodelElementStorageComponent.persistStorageElementCreation(getSubmodel(), elem.getIdShort(), elem.getLocalCopy());
			submodelAPI.addSubmodelElement(elem);
			submodelElementStorageComponent.commitTransaction();
		} catch (Exception e) {
			submodelElementStorageComponent.rollbackTransaction();
			throw e;
		}
	}

	@Override
	public void addSubmodelElement(String idShortPath, ISubmodelElement elem) {
		try {
			submodelElementStorageComponent.beginTransaction();
			submodelElementStorageComponent.persistStorageElementCreation(getSubmodel(), idShortPath, elem.getLocalCopy());
			submodelAPI.addSubmodelElement(idShortPath, elem);
			submodelElementStorageComponent.commitTransaction();
		} catch (Exception e) {
			submodelElementStorageComponent.rollbackTransaction();
			throw e;
		}
	}

	@Override
	public void updateSubmodelElement(String idShortPath, Object newValue) {
		try {
			submodelElementStorageComponent.beginTransaction();
			submodelElementStorageComponent.persistStorageElementUpdate(getSubmodel(), idShortPath, newValue);
			submodelAPI.updateSubmodelElement(idShortPath, newValue);
			submodelElementStorageComponent.commitTransaction();
		} catch (Exception e) {
			submodelElementStorageComponent.rollbackTransaction();
			throw e;
		}
	}

	@Override
	public void deleteSubmodelElement(String idShortPath) {
		try {
			submodelElementStorageComponent.beginTransaction();
			submodelElementStorageComponent.persistStorageElementDeletion(getSubmodel(), idShortPath);
			submodelAPI.deleteSubmodelElement(idShortPath);
			submodelElementStorageComponent.commitTransaction();
		} catch (Exception e) {
			submodelElementStorageComponent.rollbackTransaction();
			throw e;
		}
	}

}
