package org.eclipse.basyx.extensions.submodel.storage.elements;

import java.sql.Timestamp;
import java.util.Collection;

import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.persistence.jpa.JpaEntityManager;

import jakarta.persistence.EntityManager;

public class StorageSubmodelElementComponent {
	private EntityManager entityManager;

	public StorageSubmodelElementComponent(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void beginTransaction() {
		entityManager.getTransaction().begin();
	}

	public void commitTransaction() {
		entityManager.getTransaction().commit();
	}

	public void rollbackTransaction() {
		entityManager.getTransaction().rollback();
	}

	public void persistStorageElementCreation(ISubmodel submodel, String idShortPath, SubmodelElement submodelElement) {
		if (submodelElement instanceof SubmodelElementCollection) {
			SubmodelElementCollection collection = (SubmodelElementCollection) submodelElement;
			collection.getValue().forEach(element -> {
				String elementIdShortPath = VABPathTools.concatenatePaths(idShortPath, element.getIdShort());
				persistStorageElementCreation(submodel, elementIdShortPath, element.getLocalCopy());
			});
			if (collection.getValue().isEmpty()) {
				persistCreation(submodel, idShortPath, submodelElement);
			}
		} else {
			persistCreation(submodel, idShortPath, submodelElement);
		}
	}

	private void persistCreation(ISubmodel submodel, String idShortPath, SubmodelElement submodelElement) {
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
			Collection<ISubmodelElement> collection = (Collection<ISubmodelElement>) newValue;
			collection.forEach(element -> {
				String elementIdShortPath = VABPathTools.concatenatePaths(idShortPath, element.getIdShort());
				if (submodel.getSubmodelElements().containsKey(elementIdShortPath)) {
					persistStorageElementUpdate(submodel, elementIdShortPath, element.getValue());
				} else {
					persistStorageElementCreation(submodel, elementIdShortPath, element.getLocalCopy());
				}
			});
		} else {
			persistUpdate(submodel, idShortPath, newValue);
		}
	}

	private void persistUpdate(ISubmodel submodel, String idShortPath, Object newValue) {
		IStorageSubmodelElement element = getShared(submodel, idShortPath);

		element.setSerializedElementValue(newValue.toString());
		element.setOperation(StorageSubmodelElementOperations.UPDATE);
		element.setModelType(submodel.getSubmodelElement(idShortPath).getModelType());
		element.setModelTypeSpecial(StorageSubmodelElementComponentHelper.getModelTypeSpecial(submodel.getSubmodelElement(idShortPath)));

		entityManager.persist(element);
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
