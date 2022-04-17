package org.eclipse.basyx.extensions.submodel.storage;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.extensions.submodel.storage.elements.IStorageSubmodelElement;
import org.eclipse.basyx.extensions.submodel.storage.elements.StorageSubmodelElementFactory;
import org.eclipse.basyx.extensions.submodel.storage.elements.StorageSubmodelElementOperations;
import org.eclipse.basyx.extensions.submodel.storage.elements.StorageSubmodelElementStorageOptions;
import org.eclipse.basyx.extensions.submodel.storage.retrieval.StorageSubmodelElementRetrievalAPI;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.facade.submodelelement.SubmodelElementFacadeFactory;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.persistence.jpa.JpaEntityManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class StorageSubmodelElementComponent {
	private EntityManager entityManager;
	private EntityTransaction transaction;
	private String submodelElementStorageOption;

	public StorageSubmodelElementComponent(EntityManager entityManager) {
		this.entityManager = entityManager;
		this.submodelElementStorageOption = StorageSubmodelElementStorageOptions.COMPLETE;
	}

	public StorageSubmodelElementComponent(EntityManager entityManager, String submodelElementStorageOption) {
		this.entityManager = entityManager;
		this.submodelElementStorageOption = submodelElementStorageOption;
	}

	public void beginTransaction() {
		transaction = entityManager.getTransaction();
		if (!transaction.isActive()) {
			transaction.begin();
		}
	}

	public void commitTransaction() {
		transaction.commit();
	}

	public void rollbackTransaction() {
		if (transaction.isActive()) {
			transaction.rollback();
		}
	}

	public void persistStorageElementCreation(ISubmodel submodel, String idShortPath, SubmodelElement submodelElement) {
		idShortPath = VABPathTools.stripSlashes(idShortPath);
		if (StorageSubmodelElementComponentHelper.isElementPersistable(submodelElement)) {
			if (isElementPersisted(submodel.getIdentification().getId(), idShortPath)) {
				persist(submodelElement, StorageSubmodelElementOperations.OVERWRITE, submodel.getIdentification().getId(), idShortPath, submodelElement.getValue());
			} else {
				persist(submodelElement, StorageSubmodelElementOperations.CREATE, submodel.getIdentification().getId(), idShortPath, submodelElement.getValue());
			}
			if (submodelElement instanceof SubmodelElementCollection) {
				persistCreationOfCollectionElements(submodel, idShortPath, submodelElement);
			}
		}
	}

	private void persistCreationOfCollectionElements(ISubmodel submodel, String idShortPath, SubmodelElement submodelElement) {
		SubmodelElementCollection collection = (SubmodelElementCollection) submodelElement;
		collection.getValue().forEach(element -> {
			String elementIdShortPath = VABPathTools.concatenatePaths(idShortPath, element.getIdShort());
			persistStorageElementCreation(submodel, elementIdShortPath, element.getLocalCopy());
		});
	}

	public void persistStorageElementUpdate(ISubmodel submodel, String idShortPath, Object newValue) {
		idShortPath = VABPathTools.stripSlashes(idShortPath);
		if (isElementPersisted(submodel.getIdentification().getId(), idShortPath)) {
			persist(getOldSubmodelElement(submodel, idShortPath), StorageSubmodelElementOperations.UPDATE, submodel.getIdentification().getId(), idShortPath, newValue);
			if (newValue instanceof Collection<?>) {
				persistStorageElementCollectionUpdate(submodel, idShortPath, newValue);
			}
		} else {
			persistStorageElementCreation(submodel, idShortPath, submodel.getSubmodelElement(idShortPath).getLocalCopy());
		}
	}

	private void persistStorageElementCollectionUpdate(ISubmodel submodel, String idShortPath, Object newValue) {
		@SuppressWarnings("unchecked")
		Collection<Map<String, Object>> collection = (Collection<Map<String, Object>>) newValue;
		Collection<ISubmodelElement> smCollection = new ArrayList<>();

		for (Map<String, Object> element : collection) {
			smCollection.add(SubmodelElementFacadeFactory.createSubmodelElement(element));
		}

		smCollection.forEach(element -> {
			String elementIdShortPath = VABPathTools.concatenatePaths(idShortPath, element.getIdShort());
			if (isElementPersisted(submodel.getIdentification().getId(), elementIdShortPath)) {
				persistStorageElementUpdate(submodel, elementIdShortPath, element.getValue());
			} else {
				persistStorageElementCreation(submodel, elementIdShortPath, element.getLocalCopy());
			}
		});
	}

	public void persistStorageElementDeletion(ISubmodel submodel, String idShortPath) {
		idShortPath = VABPathTools.stripSlashes(idShortPath);
		if (isElementPersisted(submodel.getIdentification().getId(), idShortPath)) {
			persist(getOldSubmodelElement(submodel, idShortPath), StorageSubmodelElementOperations.DELETE, submodel.getIdentification().getId(), idShortPath, null);
		}
	}

	private boolean shouldStoreElement(ISubmodelElement submodelElement) {
		if (submodelElementStorageOption.equals(StorageSubmodelElementStorageOptions.COMPLETE)) {
			return true;
		}
		if (submodelElementStorageOption.equals(StorageSubmodelElementStorageOptions.QUALIFIER)) {
			return StorageSubmodelElementComponentHelper.isStorageQualifierSet(submodelElement);
		}
		return false;
	}

	private boolean isElementPersisted(String submodelId, String elementIdShortPath) {
		StorageSubmodelElementRetrievalAPI retrievalAPI = new StorageSubmodelElementRetrievalAPI(entityManager);
		List<IStorageSubmodelElement> results = retrievalAPI.getSubmodelElementHistoricValues(submodelId, elementIdShortPath);
		return (!results.isEmpty());
	}

	private void persist(ISubmodelElement submodelElement, String operation, String submodelId, String idShortPath, Object newValue) {
		if (shouldStoreElement(submodelElement)) {
			IStorageSubmodelElement element = createStorageSubmodelElement(idShortPath, submodelElement.getModelType(), StorageSubmodelElementComponentHelper.getModelTypeSpecial(submodelElement), operation, submodelId, newValue);

			entityManager.persist(element);
		}
	}

	private IStorageSubmodelElement createStorageSubmodelElement(String idShortPath, String modelType, String modelTypeSpecial, String operation, String submodelId, Object newValue) {
		IStorageSubmodelElement element = StorageSubmodelElementFactory.create(getStorageClassname());

		element.setElementIdShortPath(idShortPath);
		element.setModelType(modelType);
		element.setModelTypeSpecial(modelTypeSpecial);
		element.setOperation(operation);
		element.setSubmodelId(submodelId);
		element.setTimestamp(new Timestamp(System.currentTimeMillis()));

		if (!(newValue == null || modelType.equals(SubmodelElementCollection.MODELTYPE))) {
			String serializedValue = newValue.toString();
			element.setSerializedElementValue(serializedValue);
		}

		return element;
	}

	private ISubmodelElement getOldSubmodelElement(ISubmodel submodel, String elementIdShortPath) {
		String[] idShortPathArray = VABPathTools.splitPath(elementIdShortPath);
		if (idShortPathArray.length > 1) {
			ISubmodelElementCollection collection = (ISubmodelElementCollection) submodel.getSubmodelElement(idShortPathArray[0]);

			for (int currentItem = 1; currentItem < idShortPathArray.length - 1; currentItem++) {
				collection = (ISubmodelElementCollection) collection.getSubmodelElement(idShortPathArray[currentItem]);
			}

			return collection.getSubmodelElement(idShortPathArray[idShortPathArray.length - 1]);
		}

		return submodel.getSubmodelElement(elementIdShortPath);
	}

	private String getStorageClassname() {
		return entityManager.unwrap(JpaEntityManager.class).getServerSession().getDescriptors().entrySet().iterator().next().getValue().getJavaClassName();
	}
}
