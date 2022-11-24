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
package org.eclipse.basyx.extensions.submodel.authorization;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationGrantedAuthorityAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.ISubjectInformationProvider;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;

/**
 * Api provider for constructing a new SubmodelAPI that is authorized
 * 
 * @author espen
 */
public class AuthorizedDecoratingSubmodelAPIFactory<SubjectInformationType> implements ISubmodelAPIFactory {
	protected final IAssetAdministrationShell aas;
	protected final ISubmodelAPIFactory submodelAPIFactory;
	protected final ISubmodelAPIAuthorizer<SubjectInformationType> submodelAPIAuthorizer;
	protected final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider;

	public AuthorizedDecoratingSubmodelAPIFactory(
			final IAssetAdministrationShell aas,
			final ISubmodelAPIFactory submodelAPIFactory,
			final ISubmodelAPIAuthorizer<SubjectInformationType> submodelAPIAuthorizer,
			final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider
	) {
		this.aas = aas;
		this.submodelAPIFactory = submodelAPIFactory;
		this.submodelAPIAuthorizer = submodelAPIAuthorizer;
		this.subjectInformationProvider = subjectInformationProvider;
	}

	/**
	 * @deprecated please use {@link AuthorizedDecoratingSubmodelAPIFactory#AuthorizedDecoratingSubmodelAPIFactory(IAssetAdministrationShell, ISubmodelAPIFactory, ISubmodelAPIAuthorizer, ISubjectInformationProvider)} instead, which uses more parameters for the authorization
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public AuthorizedDecoratingSubmodelAPIFactory(
			final ISubmodelAPIFactory submodelAPIFactory
	) {
		this(
			null,
			submodelAPIFactory,
			(ISubmodelAPIAuthorizer<SubjectInformationType>) new GrantedAuthoritySubmodelAPIAuthorizer<>(new AuthenticationGrantedAuthorityAuthenticator()),
			(ISubjectInformationProvider<SubjectInformationType>) new AuthenticationContextProvider()
		);
	}

	@Override
	public ISubmodelAPI getSubmodelAPI(final Submodel submodel) {
		return new AuthorizedSubmodelAPI<>(aas, submodelAPIFactory.create(submodel), submodelAPIAuthorizer, subjectInformationProvider);
	}
}