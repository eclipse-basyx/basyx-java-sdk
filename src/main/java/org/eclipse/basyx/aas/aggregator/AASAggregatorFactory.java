/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.aggregator;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregatorFactory;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.restapi.api.IAASAPIFactory;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregatorFactory;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;

/**
 * 
 * Factory that constructs a AASAggregator with the given API
 * 
 * @author fried
 *
 */
public class AASAggregatorFactory implements IAASAggregatorFactory {

	private IAASAggregator aggregator;
	private IAASAPIFactory aasApiFactory;
	private ISubmodelAggregatorFactory submodelAggregatorFactory;
	private IAASRegistry registry;
	private ISubmodelAPIFactory submodelApiFactory;

	public AASAggregatorFactory() {
	}

	public AASAggregatorFactory(IAASAPIFactory aasApiFactory, ISubmodelAggregatorFactory submodelAggregatorFactory) {
		this.aasApiFactory = aasApiFactory;
		this.submodelAggregatorFactory = submodelAggregatorFactory;
	}

	public AASAggregatorFactory(IAASAPIFactory aasApiFactory, ISubmodelAggregatorFactory submodelAggregatorFactory, IAASRegistry registry) {
		this.aasApiFactory = aasApiFactory;
		this.submodelAggregatorFactory = submodelAggregatorFactory;
		this.registry = registry;
	}

	public AASAggregatorFactory(IAASAPIFactory aasApiFactory, ISubmodelAPIFactory submodelApiFactory) {
		this.aasApiFactory = aasApiFactory;
		this.submodelApiFactory = submodelApiFactory;
	}

	public AASAggregatorFactory(IAASAPIFactory aasApiFactory, ISubmodelAPIFactory submodelApiFactory, IAASRegistry registry) {
		this.aasApiFactory = aasApiFactory;
		this.submodelApiFactory = submodelApiFactory;
		this.registry = registry;
	}

	public AASAggregatorFactory(IAASRegistry registry) {
		this.registry = registry;
	}

	@Override
	public IAASAggregator create() {
		return constructAASAggregatorWithGivenArguments();
	}

	private IAASAggregator constructAASAggregatorWithGivenArguments() {
		if (isRegistrySet() && isAasApiAndSubmodelAggregatorSet()) {
			return new AASAggregator(aasApiFactory, submodelAggregatorFactory, registry);
		} else if (isRegistrySet() && isAasApiAndSubmodelApiSet()) {
			return new AASAggregator(aasApiFactory, submodelApiFactory, registry);
		} else if (isOnlyRegistrySet()) {
			return new AASAggregator(registry);
		} else if (isAasApiAndSubmodelAggregatorSet()) {
			return new AASAggregator(aasApiFactory, submodelAggregatorFactory);
		} else if (isAasApiAndSubmodelApiSet()) {
			return new AASAggregator(aasApiFactory, submodelApiFactory);
		} else {
			return new AASAggregator();
		}
	}

	private boolean isRegistrySet() {
		return registry != null;
	}

	private boolean isAasApiAndSubmodelAggregatorSet() {
		return aasApiFactory != null && submodelAggregatorFactory != null;
	}

	private boolean isAasApiAndSubmodelApiSet() {
		return aasApiFactory != null && submodelApiFactory != null;
	}

	private boolean isOnlyRegistrySet() {
		return isRegistrySet() && aasApiFactory == null && submodelAggregatorFactory == null && submodelApiFactory == null;
	}
}
