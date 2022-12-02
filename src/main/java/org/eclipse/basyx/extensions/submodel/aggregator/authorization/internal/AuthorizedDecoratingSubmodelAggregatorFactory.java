/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.extensions.submodel.aggregator.authorization.internal;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.extensions.shared.authorization.internal.AuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.internal.AuthenticationGrantedAuthorityAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.internal.ISubjectInformationProvider;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregatorFactory;

/**
 * Api provider for constructing a new Submodel aggregator that is authorized
 * 
 * @author espen
 */
public class AuthorizedDecoratingSubmodelAggregatorFactory<SubjectInformationType> implements ISubmodelAggregatorFactory {
	protected final IAssetAdministrationShell aas;
	protected final ISubmodelAggregatorFactory submodelAggregatorFactory;
	protected final ISubmodelAggregatorAuthorizer<SubjectInformationType> submodelAggregatorAuthorizer;
	protected final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider;

	public AuthorizedDecoratingSubmodelAggregatorFactory(
			final IAssetAdministrationShell aas,
			final ISubmodelAggregatorFactory submodelAggregatorFactory,
			final ISubmodelAggregatorAuthorizer<SubjectInformationType> submodelAggregatorAuthorizer,
			final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider
	) {
		this.aas = aas;
		this.submodelAggregatorFactory = submodelAggregatorFactory;
		this.submodelAggregatorAuthorizer = submodelAggregatorAuthorizer;
		this.subjectInformationProvider = subjectInformationProvider;
	}

	/**
	 * @deprecated please use {@link AuthorizedDecoratingSubmodelAggregatorFactory#AuthorizedDecoratingSubmodelAggregatorFactory(IAssetAdministrationShell, ISubmodelAggregatorFactory, ISubmodelAggregatorAuthorizer, ISubjectInformationProvider)} instead, which uses more parameters for the authorization
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public AuthorizedDecoratingSubmodelAggregatorFactory(
			final ISubmodelAggregatorFactory submodelAggregatorFactory
	) {
		this(
				null,
				submodelAggregatorFactory,
				(ISubmodelAggregatorAuthorizer<SubjectInformationType>) new GrantedAuthoritySubmodelAggregatorAuthorizer<>(new AuthenticationGrantedAuthorityAuthenticator()),
				(ISubjectInformationProvider<SubjectInformationType>) new AuthenticationContextProvider()
		);
	}

	@Override
	public ISubmodelAggregator create() {
		return new AuthorizedSubmodelAggregator<>(aas, submodelAggregatorFactory.create(), submodelAggregatorAuthorizer, subjectInformationProvider);
	}
}