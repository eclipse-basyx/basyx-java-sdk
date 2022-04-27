package org.eclipse.basyx.extensions.submodel.storage.aggregator;

import java.util.Collection;
import java.util.Map;

import org.eclipse.basyx.extensions.submodel.storage.StorageSubmodelElementComponent;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;

import jakarta.persistence.EntityManager;

public class StorageSubmodelAggregator implements ISubmodelAggregator {
	protected StorageSubmodelElementComponent submodelElementStorageComponent;
	private ISubmodelAggregator decoratedSubmodelAggregator;

	public StorageSubmodelAggregator(ISubmodelAggregator decoratedSubmodelAggregator, EntityManager entityManager) {
		this.decoratedSubmodelAggregator = decoratedSubmodelAggregator;
		submodelElementStorageComponent = new StorageSubmodelElementComponent(entityManager);
	}

	public StorageSubmodelAggregator(ISubmodelAggregator decoratedSubmodelAggregator, EntityManager entityManager, String submodelElementStorageOption) {
		this.decoratedSubmodelAggregator = decoratedSubmodelAggregator;
		submodelElementStorageComponent = new StorageSubmodelElementComponent(entityManager, submodelElementStorageOption);
	}

	@Override
	public Collection<ISubmodel> getSubmodelList() {
		return decoratedSubmodelAggregator.getSubmodelList();
	}

	@Override
	public ISubmodel getSubmodel(IIdentifier identifier) throws ResourceNotFoundException {
		return decoratedSubmodelAggregator.getSubmodel(identifier);
	}

	@Override
	public ISubmodel getSubmodelbyIdShort(String idShort) throws ResourceNotFoundException {
		return decoratedSubmodelAggregator.getSubmodelbyIdShort(idShort);
	}

	@Override
	public ISubmodelAPI getSubmodelAPIById(IIdentifier identifier) throws ResourceNotFoundException {
		return decoratedSubmodelAggregator.getSubmodelAPIById(identifier);
	}

	@Override
	public ISubmodelAPI getSubmodelAPIByIdShort(String idShort) throws ResourceNotFoundException {
		return decoratedSubmodelAggregator.getSubmodelAPIByIdShort(idShort);
	}

	@Override
	public void createSubmodel(ISubmodelAPI submodelAPI) {
		ISubmodel submodel = submodelAPI.getSubmodel();
		Map<String, ISubmodelElement> submodelElementMap = submodel.getSubmodelElements();
		try {
			submodelElementStorageComponent.beginTransaction();
			decoratedSubmodelAggregator.createSubmodel(submodelAPI);
			submodelElementMap.forEach((k, v) -> submodelElementStorageComponent.persistStorageElementCreation(submodel, v.getIdShort(), v.getLocalCopy()));
			submodelElementStorageComponent.commitTransaction();
		} catch (Exception e) {
			submodelElementStorageComponent.rollbackTransaction();
			throw e;
		}

	}

	@Override
	public void createSubmodel(Submodel submodel) {
		Map<String, ISubmodelElement> submodelElementMap = submodel.getSubmodelElements();
		try {
			submodelElementStorageComponent.beginTransaction();
			decoratedSubmodelAggregator.createSubmodel(submodel);
			submodelElementMap.forEach((k, v) -> submodelElementStorageComponent.persistStorageElementCreation(submodel, v.getIdShort(), v.getLocalCopy()));
			submodelElementStorageComponent.commitTransaction();
		} catch (Exception e) {
			submodelElementStorageComponent.rollbackTransaction();
			throw e;
		}
	}

	@Override
	public void updateSubmodel(Submodel submodel) throws ResourceNotFoundException {
		Map<String, ISubmodelElement> submodelElementMap = submodel.getSubmodelElements();
		try {
			submodelElementStorageComponent.beginTransaction();
			decoratedSubmodelAggregator.updateSubmodel(submodel);
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
			decoratedSubmodelAggregator.deleteSubmodelByIdentifier(identifier);
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
			decoratedSubmodelAggregator.deleteSubmodelByIdShort(idShort);
			submodelElementMap.forEach((k, v) -> submodelElementStorageComponent.persistStorageElementDeletion(submodel, v.getIdShort()));
			submodelElementStorageComponent.commitTransaction();
		} catch (Exception e) {
			submodelElementStorageComponent.rollbackTransaction();
			throw e;
		}
	}

}
