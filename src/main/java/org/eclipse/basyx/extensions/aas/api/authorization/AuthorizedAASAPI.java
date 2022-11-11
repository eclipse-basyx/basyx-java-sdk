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
package org.eclipse.basyx.extensions.aas.api.authorization;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationGrantedAuthorityAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.CodeAuthentication;
import org.eclipse.basyx.extensions.shared.authorization.CodeAuthentication.CodeAuthenticationAreaHandler;
import org.eclipse.basyx.extensions.shared.authorization.ISubjectInformationProvider;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.NotAuthorized;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;

/**
 * Implementation variant for the AASAPI that authorizes each access to the API
 *
 * @author espen
 */
public class AuthorizedAASAPI<SubjectInformationType> implements IAASAPI {
	public static final String SCOPE_AUTHORITY_PREFIX = "SCOPE_";
	public static final String READ_AUTHORITY = SCOPE_AUTHORITY_PREFIX + AASAPIScopes.READ_SCOPE;
	public static final String WRITE_AUTHORITY = SCOPE_AUTHORITY_PREFIX + AASAPIScopes.WRITE_SCOPE;

	protected final IAASAPI decoratedAASAPI;
	protected final IAASAPIAuthorizer<SubjectInformationType> aasAPIAuthorizer;
	protected final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider;

	public AuthorizedAASAPI(
			final IAASAPI decoratedAASAPI,
			final IAASAPIAuthorizer<SubjectInformationType> aasAPIAuthorizer,
			final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider
	) {
		this.decoratedAASAPI = decoratedAASAPI;
		this.aasAPIAuthorizer = aasAPIAuthorizer;
		this.subjectInformationProvider = subjectInformationProvider;
	}

	/**
	 * @deprecated
	 */
	public AuthorizedAASAPI(final IAASAPI decoratedAASAPI) {
		this(
			decoratedAASAPI,
			(IAASAPIAuthorizer<SubjectInformationType>) new GrantedAuthorityAASAPIAuthorizer<>(new AuthenticationGrantedAuthorityAuthenticator()),
			(ISubjectInformationProvider<SubjectInformationType>) new AuthenticationContextProvider()
		);
	}

	@Override
	public IAssetAdministrationShell getAAS() {
		if (CodeAuthentication.isCodeAuthentication()) {
			return decoratedAASAPI.getAAS();
		}

		try {
			return enforceGetAAS();
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected IAssetAdministrationShell enforceGetAAS() throws InhibitException {
		final IIdentifier aasId = getAasIdUnsecured();
		return aasAPIAuthorizer.enforceGetAAS(
				subjectInformationProvider.get(),
				aasId,
				decoratedAASAPI::getAAS
		);
	}

	@Override
	public void addSubmodel(final IReference submodel) {
		if (CodeAuthentication.isCodeAuthentication()) {
			decoratedAASAPI.addSubmodel(submodel);
			return;
		}

		try {
			enforceAddSubmodel(submodel);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedAASAPI.addSubmodel(submodel);
	}

	protected void enforceAddSubmodel(final IReference smId) throws InhibitException {
		final IIdentifier aasId = getAasIdUnsecured();
		aasAPIAuthorizer.enforceAddSubmodel(
				subjectInformationProvider.get(),
				aasId,
				smId
		);
	}

	@Override
	public void removeSubmodel(final String smIdShortPath) {
		if (CodeAuthentication.isCodeAuthentication()) {
			decoratedAASAPI.removeSubmodel(smIdShortPath);
			return;
		}

		try {
			enforceRemoveSubmodel(smIdShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedAASAPI.removeSubmodel(smIdShortPath);
	}

	protected void enforceRemoveSubmodel(final String smIdShortPath) throws InhibitException {
		final IIdentifier aasId = getAasIdUnsecured();
		aasAPIAuthorizer.enforceRemoveSubmodel(
				subjectInformationProvider.get(),
				aasId,
				smIdShortPath
		);
	}

	private IIdentifier getAasIdUnsecured() throws ResourceNotFoundException {
		final IAssetAdministrationShell aas = decoratedAASAPI.getAAS();

		if (aas == null) {
			return null;
		}

		try (final CodeAuthenticationAreaHandler ignored = CodeAuthentication.enterCodeAuthenticationArea()) {
			return aas.getIdentification();
		}
	}
}
