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
package org.eclipse.basyx.extensions.aas.api.authorization.internal;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.aas.restapi.api.IAASAPIFactory;
import org.eclipse.basyx.extensions.shared.authorization.internal.ISubjectInformationProvider;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * Api provider for constructing a new AAS API that is authorized
 *
 * @author wege
 */
public class AuthorizedDecoratingAASAPIFactory<SubjectInformationType> implements IAASAPIFactory {
	protected final IAASAPIFactory apiFactory;
	protected final IAASAPIAuthorizer<SubjectInformationType> aasAPIAuthorizer;
	protected final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider;

	public AuthorizedDecoratingAASAPIFactory(final IAASAPIFactory factoryToBeDecorated, final IAASAPIAuthorizer<SubjectInformationType> aasAPIAuthorizer,
			final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider) {
		this.apiFactory = factoryToBeDecorated;
		this.aasAPIAuthorizer = aasAPIAuthorizer;
		this.subjectInformationProvider = subjectInformationProvider;
	}

	@Override
	public IAASAPI getAASApi(final AssetAdministrationShell aas) {
		return new AuthorizedAASAPI<>(apiFactory.create(aas), aasAPIAuthorizer, subjectInformationProvider);
	}

	@Override
	public IAASAPI create(IIdentifier aasId) {
		return new AuthorizedAASAPI<>(apiFactory.create(aasId), aasAPIAuthorizer, subjectInformationProvider);
	}
}