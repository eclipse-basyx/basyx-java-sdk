package org.eclipse.basyx.extensions.submodel.storage;

import java.sql.Timestamp;

import org.eclipse.basyx.extensions.submodel.storage.elements.IStorageSubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.persistence.jpa.JpaEntityManager;

import jakarta.persistence.EntityManager;

public class SubmodelElementStorageComponent {
	private EntityManager entityManager;

	public SubmodelElementStorageComponent(EntityManager entityManager) {
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

	public void persistStorageElementCreation(ISubmodel submodel, String idShortPath, ISubmodelElement elem) {
		IStorageSubmodelElement element = getShared(submodel, idShortPath);

		element.setOperation(StorageSubmodelElementOperations.CREATE);
		element.setSerializedElementValue(elem.getValue().toString());

		entityManager.persist(element);
	}

	public void persistStorageElementUpdate(ISubmodel submodel, String idShortPath, Object newValue) {
		IStorageSubmodelElement element = getShared(submodel, idShortPath);

		element.setOperation(StorageSubmodelElementOperations.UPDATE);
		element.setSerializedElementValue(newValue.toString());

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
		element.setIdShort(idShortPath);
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
