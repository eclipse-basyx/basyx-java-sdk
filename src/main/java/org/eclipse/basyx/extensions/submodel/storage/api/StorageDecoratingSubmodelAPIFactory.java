package org.eclipse.basyx.extensions.submodel.storage.api;

import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;

import jakarta.persistence.EntityManager;

public class StorageDecoratingSubmodelAPIFactory implements ISubmodelAPIFactory {
	private ISubmodelAPIFactory apiFactory;
	private EntityManager entityManager;
	private String submodelElementStorageOption;

	public StorageDecoratingSubmodelAPIFactory(ISubmodelAPIFactory factoryToBeDecorated, EntityManager entityManager, String submodelElementStorageOption) {
		this.apiFactory = factoryToBeDecorated;
		this.entityManager = entityManager;
		this.submodelElementStorageOption = submodelElementStorageOption;
	}

	@Override
	public ISubmodelAPI getSubmodelAPI(Submodel submodel) {
		return new StorageSubmodelAPI(apiFactory.create(submodel), entityManager, submodelElementStorageOption);
	}

}
