/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.aggregator.authorization;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregatorFactory;

/**
 * Factory decorating AASAggregator for authorization
 * 
 * @author fischer, fried
 */
public class AuthorizedDecoratingAASAggregatorFactory implements IAASAggregatorFactory {
	private IAASAggregatorFactory apiFactory;

	public AuthorizedDecoratingAASAggregatorFactory(IAASAggregatorFactory factoryToBeDecorated) {
		this.apiFactory = factoryToBeDecorated;
	}

	@Override
	public IAASAggregator create() {
		IAASAggregator aggregator = apiFactory.create();
		aggregator = new AuthorizedAASAggregator(aggregator);
		return aggregator;
	}

}
