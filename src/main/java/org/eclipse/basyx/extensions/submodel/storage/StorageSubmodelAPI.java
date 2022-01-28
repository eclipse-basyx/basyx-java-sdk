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

import java.util.Collection;

import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;

import jakarta.persistence.EntityManager;

public class StorageSubmodelAPI implements ISubmodelAPI {

	protected ISubmodelAPI submodelAPI;
	protected EntityManager entityManager;

	public StorageSubmodelAPI(ISubmodelAPI givenSubmodelAPI, EntityManager givenManager) {
		submodelAPI = givenSubmodelAPI;
		entityManager = givenManager;
	}

	@Override
	public ISubmodel getSubmodel() {
		return submodelAPI.getSubmodel();
	}

	@Override
	public void addSubmodelElement(ISubmodelElement elem) {
		entityManager.getTransaction().begin();
		entityManager.persist(StorageSubmodelAPIHelper.getSubmodelElementCreation(getSubmodel(), elem.getIdShort(), elem));
		entityManager.getTransaction().commit();
		submodelAPI.addSubmodelElement(elem);
	}

	@Override
	public void addSubmodelElement(String idShortPath, ISubmodelElement elem) {
		entityManager.getTransaction().begin();
		entityManager.persist(StorageSubmodelAPIHelper.getSubmodelElementCreation(getSubmodel(), idShortPath, elem));
		entityManager.getTransaction().commit();
		submodelAPI.addSubmodelElement(idShortPath, elem);
	}

	@Override
	public ISubmodelElement getSubmodelElement(String idShortPath) {
		return submodelAPI.getSubmodelElement(idShortPath);
	}

	@Override
	public void deleteSubmodelElement(String idShortPath) {
		entityManager.getTransaction().begin();
		entityManager.persist(StorageSubmodelAPIHelper.getSubmodelElementDeletion(getSubmodel(), idShortPath));
		entityManager.getTransaction().commit();
		submodelAPI.deleteSubmodelElement(idShortPath);
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
	public void updateSubmodelElement(String idShortPath, Object newValue) {
		entityManager.getTransaction().begin();
		entityManager.persist(StorageSubmodelAPIHelper.getSubmodelElementUpdate(getSubmodel(), idShortPath, newValue));
		entityManager.getTransaction().commit();
		submodelAPI.updateSubmodelElement(idShortPath, newValue);
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
}
