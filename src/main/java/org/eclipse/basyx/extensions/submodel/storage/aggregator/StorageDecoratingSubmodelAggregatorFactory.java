package org.eclipse.basyx.extensions.submodel.storage.aggregator;

import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregatorFactory;

import jakarta.persistence.EntityManager;

public class StorageDecoratingSubmodelAggregatorFactory implements ISubmodelAggregatorFactory {
	private ISubmodelAggregatorFactory submodelAggregatorFactory;
	private EntityManager entityManager;

	public StorageDecoratingSubmodelAggregatorFactory(ISubmodelAggregatorFactory submodelAggregatorFactory, EntityManager entityManager) {
		this.submodelAggregatorFactory = submodelAggregatorFactory;
		this.entityManager = entityManager;
	}

	@Override
	public ISubmodelAggregator create() {
		return new StorageSubmodelAggregator(submodelAggregatorFactory.create(), entityManager);
	}

}
