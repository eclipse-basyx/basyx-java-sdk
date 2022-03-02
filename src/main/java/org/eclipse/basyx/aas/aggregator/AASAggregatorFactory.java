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

	public AASAggregatorFactory() {
		aggregator = new AASAggregator();
	}

	public AASAggregatorFactory(IAASAPIFactory aasApiFactory, ISubmodelAggregatorFactory submodelAggregatorFactory) {
		aggregator = new AASAggregator(aasApiFactory, submodelAggregatorFactory);
	}

	public AASAggregatorFactory(IAASAPIFactory aasApiFactory, ISubmodelAggregatorFactory submodelAggregatorFactory, IAASRegistry registry) {
		aggregator = new AASAggregator(aasApiFactory, submodelAggregatorFactory, registry);
	}

	public AASAggregatorFactory(IAASAPIFactory aasApiFactory, ISubmodelAPIFactory submodelApiFactory) {
		aggregator = new AASAggregator(aasApiFactory, submodelApiFactory);
	}

	public AASAggregatorFactory(IAASAPIFactory aasApiFactory, ISubmodelAPIFactory submodelApiFactory, IAASRegistry registry) {
		aggregator = new AASAggregator(aasApiFactory, submodelApiFactory, registry);
	}

	public AASAggregatorFactory(IAASRegistry registry) {
		aggregator = new AASAggregator(registry);
	}

	@Override
	public IAASAggregator create() {
		return aggregator;
	}
}
