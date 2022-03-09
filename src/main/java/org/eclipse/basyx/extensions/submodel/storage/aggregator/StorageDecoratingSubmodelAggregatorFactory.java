package org.eclipse.basyx.extensions.submodel.storage.aggregator;

import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregatorFactory;

import jakarta.persistence.EntityManager;

public class StorageDecoratingSubmodelAggregatorFactory implements ISubmodelAggregatorFactory {
	private ISubmodelAggregatorFactory submodelAggregatorFactory;
	private EntityManager entityManager;
	private String submodelElementStorageOption;

	public StorageDecoratingSubmodelAggregatorFactory(ISubmodelAggregatorFactory submodelAggregatorFactory, EntityManager entityManager, String submodelElementStorageOption) {
		this.submodelAggregatorFactory = submodelAggregatorFactory;
		this.entityManager = entityManager;
		this.submodelElementStorageOption = submodelElementStorageOption;
	}

	@Override
	public ISubmodelAggregator create() {
		return new StorageSubmodelAggregator(submodelAggregatorFactory.create(), entityManager, submodelElementStorageOption);
	}

}
