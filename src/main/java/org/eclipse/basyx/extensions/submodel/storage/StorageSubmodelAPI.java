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
import java.util.Collection;
import java.util.List;

import org.eclipse.basyx.extensions.submodel.storage.elements.IStorageSubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

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
		persistElementAction(StorageSubmodelAPIHelper.getSubmodelElementCreation(getSubmodel(), elem.getIdShort(), elem));
		submodelAPI.addSubmodelElement(elem);
	}

	@Override
	public void addSubmodelElement(String idShortPath, ISubmodelElement elem) {
		persistElementAction(StorageSubmodelAPIHelper.getSubmodelElementCreation(getSubmodel(), idShortPath, elem));
		submodelAPI.addSubmodelElement(idShortPath, elem);
	}

	@Override
	public ISubmodelElement getSubmodelElement(String idShortPath) {
		return submodelAPI.getSubmodelElement(idShortPath);
	}

	@Override
	public void deleteSubmodelElement(String idShortPath) {
		persistElementAction(StorageSubmodelAPIHelper.getSubmodelElementDeletion(getSubmodel(), idShortPath));
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
		persistElementAction(StorageSubmodelAPIHelper.getSubmodelElementUpdate(getSubmodel(), idShortPath, newValue));
		submodelAPI.updateSubmodelElement(idShortPath, newValue);
	}

	private void persistElementAction(Object action) {
		entityManager.getTransaction().begin();
		entityManager.persist(action);
		entityManager.getTransaction().commit();
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

	// TODO: Builder pattern for JPQL Queries

	/**
	 * Selects all historic StorageSumbodelElements for the given filters.
	 *
	 * @param idShort
	 * @return a list of @StorageSubmodelElement with the given idShort ordered by
	 *         their descending timestamp
	 */
	public List<IStorageSubmodelElement> getSubmodelElementHistoricValues(String submodelId) {
		Query query = entityManager.createQuery("SELECT s from StorageSubmodelElement s WHERE s.submodelId = :submodelId ORDER BY s.timestamp DESC, s.id DESC");
		query.setParameter("submodelId", submodelId);
		List<IStorageSubmodelElement> results = query.getResultList();
		return results;
	}

	/**
	 * Selects all historic StorageSumbodelElements for the given filters.
	 *
	 * @param idShort
	 * @return a list of @StorageSubmodelElement with the given idShort ordered by
	 *         their descending timestamp
	 */
	public List<IStorageSubmodelElement> getSubmodelElementHistoricValues(String submodelId, String idShort) {
		Query query = entityManager.createQuery("SELECT s from StorageSubmodelElement s WHERE s.submodelId = :submodelId AND s.idShort = :id ORDER BY s.timestamp DESC, s.id DESC");
		query.setParameter("submodelId", submodelId);
		query.setParameter("id", idShort);
		List<IStorageSubmodelElement> results = query.getResultList();
		return results;
	}

	/**
	 * Selects all historic StorageSumbodelElements for the given filters.
	 *
	 * @param idShort
	 * @param begin
	 * @param end
	 * @return a list of @StorageSubmodelElement with the given idShort with changes
	 *         between the begin and end timestamp ordered by their descending
	 *         timestamp
	 */
	public List<IStorageSubmodelElement> getSubmodelElementHistoricValues(String submodelId, String idShort, Timestamp begin, Timestamp end) {
		Query query = entityManager.createQuery("SELECT s from StorageSubmodelElement s WHERE s.submodelId = :submodelId AND s.timestamp BETWEEN :begin AND :end AND s.idShort = :id ORDER BY s.timestamp DESC, s.id DESC");
		query.setParameter("submodelId", submodelId);
		query.setParameter("id", idShort);
		query.setParameter("begin", begin);
		query.setParameter("end", end);
		List<IStorageSubmodelElement> results = query.getResultList();
		return results;
	}
}
