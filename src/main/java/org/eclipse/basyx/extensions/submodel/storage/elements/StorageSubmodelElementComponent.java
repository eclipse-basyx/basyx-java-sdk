package org.eclipse.basyx.extensions.submodel.storage.elements;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import org.eclipse.basyx.extensions.submodel.storage.retrieval.StorageSubmodelElementRetrievalAPI;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.persistence.jpa.JpaEntityManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class StorageSubmodelElementComponent {
	private EntityManager entityManager;
	private EntityTransaction transaction;

	public StorageSubmodelElementComponent(EntityManager entityManager) {
		this.entityManager = entityManager;
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
		if (StorageSubmodelElementComponentHelper.isElementStorable(submodelElement)) {
			persistCreation(submodel, idShortPath, submodelElement);
		}
	}

	private void persistCreation(ISubmodel submodel, String idShortPath, SubmodelElement submodelElement) {
		if (submodelElement instanceof SubmodelElementCollection) {
			persistCollectionCreation(submodel, idShortPath, submodelElement);
		} else {
			persistElementCreation(submodel, idShortPath, submodelElement);
		}
	}

	private void persistCollectionCreation(ISubmodel submodel, String idShortPath, SubmodelElement submodelElement) {
		SubmodelElementCollection collection = (SubmodelElementCollection) submodelElement;
		collection.getValue().forEach(element -> {
			String elementIdShortPath = VABPathTools.concatenatePaths(idShortPath, element.getIdShort());
			persistStorageElementCreation(submodel, elementIdShortPath, element.getLocalCopy());
		});
		if (collection.getValue().isEmpty()) {
			persistElementCreation(submodel, idShortPath, submodelElement);
		}
	}

	private void persistElementCreation(ISubmodel submodel, String idShortPath, SubmodelElement submodelElement) {
		IStorageSubmodelElement element = getShared(submodel, idShortPath);

		element.setOperation(StorageSubmodelElementOperations.CREATE);

		if (!(submodelElement instanceof SubmodelElementCollection)) {
			String serializedValue = submodelElement.getValue().toString();
			element.setSerializedElementValue(serializedValue);
		}

		element.setModelType(submodelElement.getModelType());
		element.setModelTypeSpecial(StorageSubmodelElementComponentHelper.getModelTypeSpecial(submodelElement));

		entityManager.persist(element);
	}

	public void persistStorageElementUpdate(ISubmodel submodel, String idShortPath, Object newValue) {
		if (newValue instanceof Collection<?>) {
			persistStorageElementCollectionUpdate(submodel, idShortPath, newValue);
		} else {
			persistStorageElementSingleUpdate(submodel, idShortPath, newValue);
		}
	}

	private void persistStorageElementSingleUpdate(ISubmodel submodel, String idShortPath, Object newValue) {
		if (wasElementPartOfSubmodel(submodel.getIdentification().getId(), idShortPath)) {
			persistUpdate(submodel, idShortPath, newValue);
		} else {
			persistStorageElementCreation(submodel, idShortPath, submodel.getSubmodelElement(idShortPath).getLocalCopy());
		}
	}

	private void persistStorageElementCollectionUpdate(ISubmodel submodel, String idShortPath, Object newValue) {
		@SuppressWarnings("unchecked")
		Collection<ISubmodelElement> collection = (Collection<ISubmodelElement>) newValue;
		collection.forEach(element -> {
			String elementIdShortPath = VABPathTools.concatenatePaths(idShortPath, element.getIdShort());
			if (wasElementPartOfSubmodel(submodel.getIdentification().getId(), elementIdShortPath)) {
				persistStorageElementUpdate(submodel, elementIdShortPath, element.getValue());
			} else {
				persistStorageElementCreation(submodel, elementIdShortPath, element.getLocalCopy());
			}
		});
	}

	private void persistUpdate(ISubmodel submodel, String idShortPath, Object newValue) {
		IStorageSubmodelElement element = getShared(submodel, idShortPath);

		element.setSerializedElementValue(newValue.toString());
		element.setOperation(StorageSubmodelElementOperations.UPDATE);
		element.setModelType(getSubmodelElement(submodel, idShortPath).getModelType());
		element.setModelTypeSpecial(StorageSubmodelElementComponentHelper.getModelTypeSpecial(getSubmodelElement(submodel, idShortPath)));

		entityManager.persist(element);
	}

	private boolean wasElementPartOfSubmodel(String submodelId, String elementIdShortPath) {
		StorageSubmodelElementRetrievalAPI retrievalAPI = new StorageSubmodelElementRetrievalAPI(entityManager);
		List<IStorageSubmodelElement> results = retrievalAPI.getSubmodelElementHistoricValues(submodelId, elementIdShortPath);
		return (!results.isEmpty());
	}

	private ISubmodelElement getSubmodelElement(ISubmodel submodel, String elementIdShortPath) {
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

	public void persistStorageElementDeletion(ISubmodel submodel, String idShortPath) {
		IStorageSubmodelElement element = getShared(submodel, idShortPath);

		element.setOperation(StorageSubmodelElementOperations.DELETE);

		entityManager.persist(element);
	}

	private IStorageSubmodelElement getShared(ISubmodel submodel, String idShortPath) {
		IStorageSubmodelElement element = StorageSubmodelElementFactory.create(getStorageClassname());

		element.setSubmodelId(submodel.getIdentification().getId());
		element.setElementIdShortPath(idShortPath);
		element.setTimestamp(new Timestamp(System.currentTimeMillis()));

		return element;
	}

	private String getStorageClassname() {
		return entityManager.unwrap(JpaEntityManager.class).getServerSession().getDescriptors().entrySet().iterator().next().getValue().getJavaClassName();
	}

	public static class StorageSubmodelElementOperations {
		public static String CREATE = "CREATE";
		public static String UPDATE = "UPDATE";
		public static String DELETE = "DELETE";
	}
}
