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

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.extensions.aas.api.authorization.AASAPIScopes;
import org.eclipse.basyx.extensions.shared.authorization.internal.ElevatedCodeAuthentication;
import org.eclipse.basyx.extensions.shared.authorization.internal.ISubjectInformationProvider;
import org.eclipse.basyx.extensions.shared.authorization.internal.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.internal.NotAuthorizedException;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;

/**
 * Implementation variant for the AASAPI that authorizes each access to the API
 *
 * @author espen, wege
 */
public class AuthorizedAASAPI<SubjectInformationType> implements IAASAPI {
	public static final String SCOPE_AUTHORITY_PREFIX = "SCOPE_";
	public static final String READ_AUTHORITY = SCOPE_AUTHORITY_PREFIX + AASAPIScopes.READ_SCOPE;
	public static final String WRITE_AUTHORITY = SCOPE_AUTHORITY_PREFIX + AASAPIScopes.WRITE_SCOPE;

	protected final IAASAPI decoratedAASAPI;
	protected final IAASAPIAuthorizer<SubjectInformationType> aasAPIAuthorizer;
	protected final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider;

	public AuthorizedAASAPI(final IAASAPI decoratedAASAPI, final IAASAPIAuthorizer<SubjectInformationType> aasAPIAuthorizer, final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider) {
		this.decoratedAASAPI = decoratedAASAPI;
		this.aasAPIAuthorizer = aasAPIAuthorizer;
		this.subjectInformationProvider = subjectInformationProvider;
	}

	@Override
	public IAssetAdministrationShell getAAS() {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedAASAPI.getAAS();
		}

		try {
			return authorizeGetAAS();
		} catch (final InhibitException e) {
			throw new NotAuthorizedException(e);
		}
	}

	protected IAssetAdministrationShell authorizeGetAAS() throws InhibitException {
		return aasAPIAuthorizer.authorizeGetAAS(subjectInformationProvider.get(), decoratedAASAPI::getAAS);
	}

	@Override
	public void addSubmodel(final IReference submodel) {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			decoratedAASAPI.addSubmodel(submodel);
			return;
		}

		try {
			authorizeAddSubmodel(submodel);
		} catch (final InhibitException e) {
			throw new NotAuthorizedException(e);
		}
		decoratedAASAPI.addSubmodel(submodel);
	}

	protected void authorizeAddSubmodel(final IReference smId) throws InhibitException {
		aasAPIAuthorizer.authorizeAddSubmodel(subjectInformationProvider.get(), decoratedAASAPI::getAAS, smId);
	}

	@Override
	public void removeSubmodel(final String smIdShortPath) {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			decoratedAASAPI.removeSubmodel(smIdShortPath);
			return;
		}

		try {
			authorizeRemoveSubmodel(smIdShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorizedException(e);
		}
		decoratedAASAPI.removeSubmodel(smIdShortPath);
	}

	protected void authorizeRemoveSubmodel(final String smIdShortPath) throws InhibitException {
		aasAPIAuthorizer.authorizeRemoveSubmodel(subjectInformationProvider.get(), decoratedAASAPI::getAAS, smIdShortPath);
	}
}
