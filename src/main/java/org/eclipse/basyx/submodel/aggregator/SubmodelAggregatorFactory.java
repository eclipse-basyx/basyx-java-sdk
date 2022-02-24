/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/


package org.eclipse.basyx.submodel.aggregator;

import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregatorFactory;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPIFactory;

public class SubmodelAggregatorFactory implements ISubmodelAggregatorFactory {

	ISubmodelAPIFactory submodelAPIFactory;
	
	public SubmodelAggregatorFactory() {
		submodelAPIFactory = new VABSubmodelAPIFactory();
	}
	
	public SubmodelAggregatorFactory(ISubmodelAPIFactory submodelAPIFactory) {
		this.submodelAPIFactory = submodelAPIFactory;
	}

	@Override
	public ISubmodelAggregator create() {
		return new SubmodelAggregator(submodelAPIFactory);
	}

}
