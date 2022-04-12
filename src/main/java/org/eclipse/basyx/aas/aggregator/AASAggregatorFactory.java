/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
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
