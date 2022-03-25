package org.eclipse.basyx.extensions.submodel.storage.retrieval;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.extensions.submodel.storage.elements.IStorageSubmodelElement;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

public class StorageSubmodelElementRetrievalAPI {
	public static final String HISTORY = "history";
	protected EntityManager entityManager;

	public StorageSubmodelElementRetrievalAPI(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * Selects all historic StorageSumbodelElements for the given filters.
	 *
	 * @param submodelId
	 * @return a list of @StorageSubmodelElement with the given idShort ordered by
	 *         their descending timestamp
	 */
	public List<IStorageSubmodelElement> getSubmodelElementHistoricValues(String submodelId) {
		Query query = new StorageSubmodelElementQueryBuilder(entityManager).setSubmodelId(submodelId).build();
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
		Query query = new StorageSubmodelElementQueryBuilder(entityManager).setSubmodelId(submodelId).setElementIdShort(idShort).build();
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
	public List<IStorageSubmodelElement> getSubmodelElementHistoricValues(String submodelId, String idShort, Map<String, String> parameters) {
		Query query = new StorageSubmodelElementQueryBuilder(entityManager).setSubmodelId(submodelId).setElementIdShort(idShort).build();
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
		Query query = new StorageSubmodelElementQueryBuilder(entityManager).setSubmodelId(submodelId).setElementIdShort(idShort).setTimespan(begin, end).build();
		@SuppressWarnings("unchecked")
		List<IStorageSubmodelElement> results = query.getResultList();
		return results;
	}
}
