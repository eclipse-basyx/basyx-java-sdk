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
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * Api provider for constructing a new Submodel aggregator that is authorized
 * 
 * @author espen
 */
public class AuthorizedDecoratingSubmodelAggregatorFactory implements ISubmodelAggregatorFactory {
	private ISubmodelAggregatorFactory submodelAggregatorFactory;

	public AuthorizedDecoratingSubmodelAggregatorFactory(ISubmodelAggregatorFactory submodelAggregatorFactory) {
		this.submodelAggregatorFactory = submodelAggregatorFactory;
	}

	@Override
	public ISubmodelAggregator create() {
		return new AuthorizedSubmodelAggregator(submodelAggregatorFactory.create());
	}

	@Override
	public ISubmodelAggregator create(IIdentifier aasIdentifier) {
		return new AuthorizedSubmodelAggregator(submodelAggregatorFactory.create(aasIdentifier));
	}
}