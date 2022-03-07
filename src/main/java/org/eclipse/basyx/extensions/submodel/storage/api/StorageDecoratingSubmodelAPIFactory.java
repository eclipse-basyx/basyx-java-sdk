package org.eclipse.basyx.extensions.submodel.storage.api;

import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;

import jakarta.persistence.EntityManager;

public class StorageDecoratingSubmodelAPIFactory implements ISubmodelAPIFactory {
	private ISubmodelAPIFactory apiFactory;
	private EntityManager entityManager;

	public StorageDecoratingSubmodelAPIFactory(ISubmodelAPIFactory factoryToBeDecorated, EntityManager entityManager) {
		this.apiFactory = factoryToBeDecorated;
		this.entityManager = entityManager;
	}

	@Override
	public ISubmodelAPI getSubmodelAPI(Submodel submodel) {
		return new StorageSubmodelAPI(apiFactory.getSubmodelAPI(submodel), entityManager);
	}
}
