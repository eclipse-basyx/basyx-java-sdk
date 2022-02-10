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
import java.util.stream.Collectors;

import org.eclipse.basyx.extensions.submodel.storage.elements.IStorageSubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.persistence.jpa.JpaEntityManager;

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
		persistElementAction(getStorageElementForActionCreation(getSubmodel(), elem.getIdShort(), elem));
		submodelAPI.addSubmodelElement(elem);
	}

	@Override
	public void addSubmodelElement(String idShortPath, ISubmodelElement elem) {
		persistElementAction(getStorageElementForActionCreation(getSubmodel(), idShortPath, elem));
		submodelAPI.addSubmodelElement(idShortPath, elem);
	}

	@Override
	public ISubmodelElement getSubmodelElement(String idShortPath) {
		return submodelAPI.getSubmodelElement(idShortPath);
	}

	@Override
	public void deleteSubmodelElement(String idShortPath) {
		persistElementAction(getStorageElementForActionDeletion(getSubmodel(), idShortPath));
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
		persistElementAction(getStorageElementForActionUpdate(getSubmodel(), idShortPath, newValue));
		submodelAPI.updateSubmodelElement(idShortPath, newValue);
	}

	private void persistElementAction(IStorageSubmodelElement action) {
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

	public IStorageSubmodelElement getStorageElementForActionCreation(ISubmodel submodel, String idShortPath, ISubmodelElement elem) {
		IStorageSubmodelElement element = getShared(submodel, idShortPath);

		element.setOperation(StorageSubmodelElementOperations.CREATE);
		element.setSerializedElementValue(elem.getValue().toString());

		return element;
	}

	public IStorageSubmodelElement getStorageElementForActionUpdate(ISubmodel submodel, String idShortPath, Object newValue) {
		IStorageSubmodelElement element = getShared(submodel, idShortPath);

		element.setOperation(StorageSubmodelElementOperations.UPDATE);
		element.setSerializedElementValue(newValue.toString());

		return element;
	}

	public IStorageSubmodelElement getStorageElementForActionDeletion(ISubmodel submodel, String idShortPath) {
		IStorageSubmodelElement element = getShared(submodel, idShortPath);

		element.setOperation(StorageSubmodelElementOperations.DELETE);

		return element;
	}

	private IStorageSubmodelElement getShared(ISubmodel submodel, String idShortPath) {
		IStorageSubmodelElement element = StorageSubmodelElementFactory.create(getStorageClassname());

		element.setSubmodelId(submodel.getIdentification().getId());
		element.setIdShort(idShortPath);
		element.setTimestamp(new Timestamp(System.currentTimeMillis()));

		return element;
	}

	private String getStorageClassname() {
		return entityManager.unwrap(JpaEntityManager.class).getServerSession().getDescriptors().entrySet().iterator().next().getValue().getJavaClassName();
	}

	@SuppressWarnings("unused")
	private static List<String> getParentKeys(ISubmodel submodel) {
		return submodel.getParent().getKeys().stream().map(s -> s.getValue()).collect(Collectors.toList());
	}

	/**
	 * Selects all historic StorageSumbodelElements for the given filters.
	 *
	 * @param submodelId
	 * @return a list of @StorageSubmodelElement with the given idShort ordered by
	 *         their descending timestamp
	 */
	public List<IStorageSubmodelElement> getSubmodelElementHistoricValues(String submodelId) {
		Query query = new StorageSubmodelQueryBuilder(entityManager).setSubmodelId(submodelId).build();
		@SuppressWarnings("unchecked")
		List<IStorageSubmodelElement> results = query.getResultList();
		return results;
	}

	/**
	 * Selects all historic StorageSumbodelElements for the given filters.
	 *
	 * @param submodelId
	 * @param idShort
	 * @return a list of @StorageSubmodelElement with the given idShort ordered by
	 *         their descending timestamp
	 */
	public List<IStorageSubmodelElement> getSubmodelElementHistoricValues(String submodelId, String idShort) {
		Query query = new StorageSubmodelQueryBuilder(entityManager).setSubmodelId(submodelId).setIdShort(idShort).build();
		@SuppressWarnings("unchecked")
		List<IStorageSubmodelElement> results = query.getResultList();
		return results;
	}

	/**
	 * Selects all historic StorageSumbodelElements for the given filters.
	 *
	 * @param submodelId
	 * @param idShort
	 * @param begin
	 * @param end
	 * @return a list of @StorageSubmodelElement with the given idShort with changes
	 *         between the begin and end timestamp ordered by their descending
	 *         timestamp
	 */
	public List<IStorageSubmodelElement> getSubmodelElementHistoricValues(String submodelId, String idShort, Timestamp begin, Timestamp end) {
		Query query = new StorageSubmodelQueryBuilder(entityManager).setSubmodelId(submodelId).setIdShort(idShort).setTimespan(begin, end).build();
		@SuppressWarnings("unchecked")
		List<IStorageSubmodelElement> results = query.getResultList();
		return results;
	}

	public static class StorageSubmodelElementOperations {
		public static String CREATE = "CREATE";
		public static String UPDATE = "UPDATE";
		public static String DELETE = "DELETE";
	}
}
