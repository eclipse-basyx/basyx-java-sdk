package org.eclipse.basyx.extensions.submodel.storage;

import java.util.Map;

import org.eclipse.basyx.extensions.submodel.storage.elements.StorageSubmodelElementComponent;
import org.eclipse.basyx.submodel.aggregator.SubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;

import jakarta.persistence.EntityManager;

public class StorageSubmodelAggregator extends SubmodelAggregator {
	protected StorageSubmodelElementComponent submodelElementStorageComponent;

	public StorageSubmodelAggregator(EntityManager entityManager) {
		smApiFactory = new StorageSubmodelAPIFactory(entityManager);
		submodelElementStorageComponent = new StorageSubmodelElementComponent(entityManager);
	}

	@Override
	public void createSubmodel(ISubmodelAPI submodelAPI) {
		ISubmodel submodel = submodelAPI.getSubmodel();
		Map<String, ISubmodelElement> submodelElementMap = submodel.getSubmodelElements();
		try {
			submodelElementStorageComponent.beginTransaction();
			addSubmodelToMap(submodelAPI);
			submodelElementMap.forEach((k, v) -> submodelElementStorageComponent.persistStorageElementCreation(submodel, v.getIdShort(), v.getLocalCopy()));
			submodelElementStorageComponent.commitTransaction();
		} catch (Exception e) {
			submodelElementStorageComponent.rollbackTransaction();
			throw e;
		}

	}

	@Override
	public void createSubmodel(Submodel submodel) {
		ISubmodelAPI submodelAPI = smApiFactory.getSubmodelAPI(submodel);
		createSubmodel(submodelAPI);
	}

	@Override
	public void updateSubmodel(Submodel submodel) throws ResourceNotFoundException {
		Map<String, ISubmodelElement> submodelElementMap = submodel.getSubmodelElements();
		try {
			submodelElementStorageComponent.beginTransaction();
			ISubmodelAPI submodelAPI = smApiFactory.getSubmodelAPI(submodel);
			addSubmodelToMap(submodelAPI);
			submodelElementMap.forEach((k, v) -> submodelElementStorageComponent.persistStorageElementUpdate(submodel, v.getIdShort(), v.getValue()));
			submodelElementStorageComponent.commitTransaction();
		} catch (Exception e) {
			submodelElementStorageComponent.rollbackTransaction();
			throw e;
		}
	}

	@Override
	public void deleteSubmodelByIdentifier(IIdentifier identifier) {
		ISubmodel submodel = getSubmodel(identifier);
		Map<String, ISubmodelElement> submodelElementMap = submodel.getSubmodelElements();
		try {
			submodelElementStorageComponent.beginTransaction();
			super.deleteSubmodelByIdentifier(identifier);
			submodelElementMap.forEach((k, v) -> submodelElementStorageComponent.persistStorageElementDeletion(submodel, v.getIdShort()));
			submodelElementStorageComponent.commitTransaction();
		} catch (Exception e) {
			submodelElementStorageComponent.rollbackTransaction();
			throw e;
		}
	}

	@Override
	public void deleteSubmodelByIdShort(String idShort) {
		ISubmodel submodel = getSubmodelbyIdShort(idShort);
		Map<String, ISubmodelElement> submodelElementMap = submodel.getSubmodelElements();
		try {
			submodelElementStorageComponent.beginTransaction();
			super.deleteSubmodelByIdShort(idShort);
			submodelElementMap.forEach((k, v) -> submodelElementStorageComponent.persistStorageElementDeletion(submodel, v.getIdShort()));
			submodelElementStorageComponent.commitTransaction();
		} catch (Exception e) {
			submodelElementStorageComponent.rollbackTransaction();
			throw e;
		}
	}
}
