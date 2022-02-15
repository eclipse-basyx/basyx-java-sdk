package org.eclipse.basyx.extensions.submodel.storage;

import org.eclipse.basyx.extensions.submodel.storage.StorageSubmodelAPI;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPI;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;

import jakarta.persistence.EntityManager;

public class StorageSubmodelAPIFactory implements ISubmodelAPIFactory {
	private EntityManager entityManager;

	public StorageSubmodelAPIFactory(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public ISubmodelAPI getSubmodelAPI(Submodel submodel) {
		return new StorageSubmodelAPI(new VABSubmodelAPI(new VABLambdaProvider(submodel)), entityManager);
	}
}
