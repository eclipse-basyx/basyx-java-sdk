/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.extensions.submodel.aggregator.authorization;

import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregatorFactory;

/**
 * Api provider for constructing a new Submodel aggregator that is authorized
 * 
 * @author espen
 */
public class AuthorizedDecoratingSubmodelAggregatorFactory implements ISubmodelAggregatorFactory {
	protected final ISubmodelAggregatorFactory submodelAggregatorFactory;
	protected final ISubmodelAggregatorAuthorizer submodelAggregatorAuthorizer;

	public AuthorizedDecoratingSubmodelAggregatorFactory(ISubmodelAggregatorFactory submodelAggregatorFactory, ISubmodelAggregatorAuthorizer submodelAggregatorAuthorizer) {
		this.submodelAggregatorFactory = submodelAggregatorFactory;
		this.submodelAggregatorAuthorizer = submodelAggregatorAuthorizer;
	}

	@Override
	public ISubmodelAggregator create() {
		return new AuthorizedSubmodelAggregator(submodelAggregatorFactory.create(),
				submodelAggregatorAuthorizer);
	}
}