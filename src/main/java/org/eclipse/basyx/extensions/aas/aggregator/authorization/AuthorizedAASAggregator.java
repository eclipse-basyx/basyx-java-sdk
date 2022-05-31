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
package org.eclipse.basyx.extensions.aas.aggregator.authorization;

/**
 * An aggregator implementation that authorizes invocations before forwarding them to
 * an underlying aggregator implementation.
 *
 * @author jungjan, fried, fischer, wege
 * @see AASAggregatorScopes
 */

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eclipse.basyx.aas.aggregator.AASAggregator;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationGrantedAuthorityAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.ISubjectInformationProvider;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.NotAuthorized;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Implementation variant for the AASAggregator that authorized each access
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

	public AuthorizedAASAggregator(
			final IAASAggregator decoratedAasAggregator,
			final IAASAggregatorAuthorizer<SubjectInformationType> aasAggregatorAuthorizer,
			final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider
	) {
		this.decoratedAasAggregator = decoratedAasAggregator;
		this.aasAggregatorAuthorizer = aasAggregatorAuthorizer;
		this.subjectInformationProvider = subjectInformationProvider;
	}

	static void abc() {
		new AuthorizedAASAggregator<Jwt>(
				new AASAggregator(

				)
		);
	}

	public AuthorizedAASAggregator(final IAASAggregator decoratedAasAggregator) {
		this(
			decoratedAasAggregator,
			(IAASAggregatorAuthorizer<SubjectInformationType>) new GrantedAuthorityAASAggregatorAuthorizer<>(new AuthenticationGrantedAuthorityAuthenticator()),
			(ISubjectInformationProvider<SubjectInformationType>) new AuthenticationContextProvider()
		);
	}

	@Override
	public Collection<IAssetAdministrationShell> getAASList() {
		try {
			return enforceGetAASList();
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected Collection<IAssetAdministrationShell> enforceGetAASList() throws InhibitException {
		final Collection<IAssetAdministrationShell> enforcedAASList = aasAggregatorAuthorizer.enforceGetAASList(
				subjectInformationProvider.get(),
				decoratedAasAggregator::getAASList
		);

		return enforcedAASList.stream().map(aas -> {
			try {
				return enforceGetAAS(aas.getIdentification());
			} catch (final InhibitException e) {
				// leave out that aas
				logger.info(e.getMessage(), e);
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	@Override
	public IAssetAdministrationShell getAAS(final IIdentifier shellId) throws ResourceNotFoundException {
		try {
			return enforceGetAAS(shellId);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected IAssetAdministrationShell enforceGetAAS(final IIdentifier aasId) throws InhibitException {
		return aasAggregatorAuthorizer.enforceGetAAS(
				subjectInformationProvider.get(),
				aasId,
				() -> decoratedAasAggregator.getAAS(aasId)
		);
	}

	@Override
	public IModelProvider getAASProvider(final IIdentifier shellId) throws ResourceNotFoundException {
		try {
			return enforceGetAASProvider(shellId);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected IModelProvider enforceGetAASProvider(final IIdentifier aasId) throws ResourceNotFoundException, InhibitException {
		// TODO: does this give access to everything? then we might need write and execute permissions too
		return aasAggregatorAuthorizer.enforceGetAASProvider(
				subjectInformationProvider.get(),
				aasId,
				() -> decoratedAasAggregator.getAASProvider(aasId)
		);
	}

	@Override
	public void createAAS(final AssetAdministrationShell shell) {
		try {
			enforceCreateAAS(shell);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedAasAggregator.createAAS(shell);
	}

	protected void enforceCreateAAS(final IAssetAdministrationShell aas) throws InhibitException {
		final IIdentifier aasId = aas.getIdentification();
		aasAggregatorAuthorizer.enforceCreateAAS(
				subjectInformationProvider.get(),
				aasId
		);
	}

	@Override
	public void updateAAS(final AssetAdministrationShell shell) throws ResourceNotFoundException {
		try {
			enforceUpdateAAS(shell);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedAasAggregator.updateAAS(shell);
	}

	protected void enforceUpdateAAS(final IAssetAdministrationShell aas) throws InhibitException {
		final IIdentifier aasId = aas.getIdentification();
		aasAggregatorAuthorizer.enforceUpdateAAS(
				subjectInformationProvider.get(),
				aasId
		);
	}

	@Override
	public void deleteAAS(final IIdentifier shellId) {
		try {
			enforceDeleteAAS(shellId);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedAasAggregator.deleteAAS(shellId);
	}

	protected void enforceDeleteAAS(final IIdentifier aasId) throws InhibitException {
		aasAggregatorAuthorizer.enforceDeleteAAS(
				subjectInformationProvider.get(),
				aasId
		);
	}
}
