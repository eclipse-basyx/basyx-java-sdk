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
package org.eclipse.basyx.extensions.aas.aggregator.authorization.internal;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.extensions.shared.authorization.internal.AuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.internal.AuthenticationGrantedAuthorityAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.internal.ElevatedCodeAuthentication;
import org.eclipse.basyx.extensions.shared.authorization.internal.ISubjectInformationProvider;
import org.eclipse.basyx.extensions.shared.authorization.internal.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.internal.NotAuthorized;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IIdentifiable;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An aggregator implementation that authorizes invocations before forwarding them to * an underlying aggregator implementation.
 *
 * @author wege
 */
public class AuthorizedAASAggregator<SubjectInformationType> implements IAASAggregator {
	private static final String SCOPE_AUTHORITY_PREFIX = "SCOPE_";
	public static final String READ_AUTHORITY = SCOPE_AUTHORITY_PREFIX + AASAggregatorScopes.READ_SCOPE;
	public static final String WRITE_AUTHORITY = SCOPE_AUTHORITY_PREFIX + AASAggregatorScopes.WRITE_SCOPE;

	private static final Logger logger = LoggerFactory.getLogger(AuthorizedAASAggregator.class);

	protected final IAASAggregator decoratedAasAggregator;
	protected final IAASAggregatorAuthorizer<SubjectInformationType> aasAggregatorAuthorizer;
	protected final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider;

	public AuthorizedAASAggregator(final IAASAggregator decoratedAasAggregator, final IAASAggregatorAuthorizer<SubjectInformationType> aasAggregatorAuthorizer,
			final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider) {
		this.decoratedAasAggregator = decoratedAasAggregator;
		this.aasAggregatorAuthorizer = aasAggregatorAuthorizer;
		this.subjectInformationProvider = subjectInformationProvider;
	}

	/**
	 * @deprecated Please use {@link AuthorizedAASAggregator#AuthorizedAASAggregator(IAASAggregator, IAASAggregatorAuthorizer, ISubjectInformationProvider)} instead for more explicit parametrization.
	 */
	@Deprecated @SuppressWarnings("unchecked") public AuthorizedAASAggregator(final IAASAggregator decoratedAasAggregator) {
		this(decoratedAasAggregator, (IAASAggregatorAuthorizer<SubjectInformationType>) new GrantedAuthorityAASAggregatorAuthorizer<>(new AuthenticationGrantedAuthorityAuthenticator()),
				(ISubjectInformationProvider<SubjectInformationType>) new AuthenticationContextProvider());
	}

	@Override public Collection<IAssetAdministrationShell> getAASList() {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedAasAggregator.getAASList();
		}

		try {
			return authorizeGetAASList();
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected Collection<IAssetAdministrationShell> authorizeGetAASList() throws InhibitException {
		return getAASIdentifierList().stream().map(identifier -> {
			try {
				return authorizeGetAAS(identifier);
			} catch (final InhibitException e) {
				// leave out that aas
				logger.info(e.getMessage(), e);
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	private Collection<IIdentifier> getAASIdentifierList() throws InhibitException {
		final Collection<IAssetAdministrationShell> authorizedAASList = aasAggregatorAuthorizer.authorizeGetAASList(subjectInformationProvider.get(), decoratedAasAggregator::getAASList);
		return authorizedAASList.stream().map(IIdentifiable::getIdentification).collect(Collectors.toList());
	}

	@Override public IAssetAdministrationShell getAAS(final IIdentifier shellId) throws ResourceNotFoundException {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedAasAggregator.getAAS(shellId);
		}

		try {
			return authorizeGetAAS(shellId);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected IAssetAdministrationShell authorizeGetAAS(final IIdentifier aasId) throws InhibitException {
		return aasAggregatorAuthorizer.authorizeGetAAS(subjectInformationProvider.get(), aasId, () -> decoratedAasAggregator.getAAS(aasId));
	}

	@Override public IModelProvider getAASProvider(final IIdentifier shellId) throws ResourceNotFoundException {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedAasAggregator.getAASProvider(shellId);
		}

		try {
			return authorizeGetAASProvider(shellId);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected IModelProvider authorizeGetAASProvider(final IIdentifier aasId) throws ResourceNotFoundException, InhibitException {
		return aasAggregatorAuthorizer.authorizeGetAASProvider(subjectInformationProvider.get(), aasId, () -> decoratedAasAggregator.getAASProvider(aasId));
	}

	@Override public void createAAS(final AssetAdministrationShell shell) {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			decoratedAasAggregator.createAAS(shell);
			return;
		}

		try {
			authorizeCreateAAS(shell);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedAasAggregator.createAAS(shell);
	}

	protected void authorizeCreateAAS(final AssetAdministrationShell aas) throws InhibitException {
		aasAggregatorAuthorizer.authorizeCreateAAS(subjectInformationProvider.get(), aas);
	}

	@Override public void updateAAS(final AssetAdministrationShell shell) throws ResourceNotFoundException {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			decoratedAasAggregator.updateAAS(shell);
			return;
		}

		try {
			authorizeUpdateAAS(shell);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedAasAggregator.updateAAS(shell);
	}

	protected void authorizeUpdateAAS(final AssetAdministrationShell aas) throws InhibitException {
		aasAggregatorAuthorizer.authorizeUpdateAAS(subjectInformationProvider.get(), aas);
	}

	@Override public void deleteAAS(final IIdentifier shellId) {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			decoratedAasAggregator.deleteAAS(shellId);
			return;
		}

		try {
			authorizeDeleteAAS(shellId);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedAasAggregator.deleteAAS(shellId);
	}

	protected void authorizeDeleteAAS(final IIdentifier aasId) throws InhibitException {
		aasAggregatorAuthorizer.authorizeDeleteAAS(subjectInformationProvider.get(), aasId);
	}
}
