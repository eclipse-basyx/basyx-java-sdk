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
package org.eclipse.basyx.extensions.submodel.aggregator.authorization;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationGrantedAuthorityAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.NotAuthorized;
import org.eclipse.basyx.extensions.shared.authorization.ISubjectInformationProvider;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IIdentifiable;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation variant for the SubmodelAggregator that authorized each access
 *
 * @author espen, wege
 *
 */
public class AuthorizedSubmodelAggregator<SubjectInformationType> implements ISubmodelAggregator {
	private static final String SCOPE_AUTHORITY_PREFIX = "SCOPE_";
	public static final String READ_AUTHORITY = SCOPE_AUTHORITY_PREFIX + SubmodelAggregatorScopes.READ_SCOPE;
	public static final String WRITE_AUTHORITY = SCOPE_AUTHORITY_PREFIX + SubmodelAggregatorScopes.WRITE_SCOPE;

	private static final Logger logger = LoggerFactory.getLogger(AuthorizedSubmodelAggregator.class);

	protected final IIdentifier aasId;
	protected final ISubmodelAggregator decoratedSubmodelAggregator;
	protected final ISubmodelAggregatorAuthorizer<SubjectInformationType> submodelAggregatorAuthorizer;
	protected final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider;

	public AuthorizedSubmodelAggregator(
			final IAssetAdministrationShell aas,
			final ISubmodelAggregator decoratedSubmodelAggregator,
			final ISubmodelAggregatorAuthorizer<SubjectInformationType> submodelAggregatorAuthorizer,
			final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider
	) {
		this.aasId = aas != null ? aas.getIdentification() : null;
		this.decoratedSubmodelAggregator = decoratedSubmodelAggregator;
		this.submodelAggregatorAuthorizer = submodelAggregatorAuthorizer;
		this.subjectInformationProvider = subjectInformationProvider;
	}

	public AuthorizedSubmodelAggregator(final ISubmodelAggregator decoratedSubmodelAggregator, final ISubmodelAggregatorAuthorizer<SubjectInformationType> submodelAggregatorAuthorizer, final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider) {
		this(
			null,
			decoratedSubmodelAggregator,
			submodelAggregatorAuthorizer,
			subjectInformationProvider
		);
	}

	public AuthorizedSubmodelAggregator(final ISubmodelAggregator decoratedSubmodelAggregator) {
		this(
			decoratedSubmodelAggregator,
			(ISubmodelAggregatorAuthorizer<SubjectInformationType>) new GrantedAuthoritySubmodelAggregatorAuthorizer<>(new AuthenticationGrantedAuthorityAuthenticator()),
			(ISubjectInformationProvider<SubjectInformationType>) new AuthenticationContextProvider()
		);
	}

	@Override
	public Collection<ISubmodel> getSubmodelList() {
		try {
			return enforceGetSubmodelList();
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected Collection<ISubmodel> enforceGetSubmodelList() throws InhibitException {
		final Collection<ISubmodel> enforcedSubmodels = submodelAggregatorAuthorizer.enforceGetSubmodelList(
				subjectInformationProvider.get(),
				aasId,
				decoratedSubmodelAggregator::getSubmodelList
		);
		return enforcedSubmodels.stream().map(submodel -> {
			try {
				return enforceGetSubmodel(submodel);
			} catch (final InhibitException e) {
				logger.info(e.getMessage(), e);
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	protected ISubmodel enforceGetSubmodel(final ISubmodel sm) throws ResourceNotFoundException, InhibitException {
		final IIdentifier smId = Optional.ofNullable(sm).map(IIdentifiable::getIdentification).orElse(null);
		return submodelAggregatorAuthorizer.enforceGetSubmodel(
				subjectInformationProvider.get(),
				aasId,
				smId,
				() -> sm
		);
	}

	@Override
	public ISubmodel getSubmodel(final IIdentifier identifier) throws ResourceNotFoundException {
		try {
			return enforceGetSubmodel(identifier);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected ISubmodel enforceGetSubmodel(final IIdentifier smId) throws ResourceNotFoundException, InhibitException {
		return submodelAggregatorAuthorizer.enforceGetSubmodel(
				subjectInformationProvider.get(),
				aasId,
				smId,
				() -> decoratedSubmodelAggregator.getSubmodel(smId)
		);
	}

	@Override
	public ISubmodel getSubmodelbyIdShort(final String smIdShortPath) throws ResourceNotFoundException {
		try {
			return enforceGetSubmodelbyIdShort(smIdShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected ISubmodel enforceGetSubmodelbyIdShort(final String smIdShortPath) throws ResourceNotFoundException, InhibitException {
		final ISubmodel sm = decoratedSubmodelAggregator.getSubmodelbyIdShort(smIdShortPath);
		final IIdentifier smId = Optional.ofNullable(sm).map(IIdentifiable::getIdentification).orElse(null);
		return submodelAggregatorAuthorizer.enforceGetSubmodel(
				subjectInformationProvider.get(),
				aasId,
				smId,
				() -> sm
		);
	}

	@Override
	public ISubmodelAPI getSubmodelAPIById(final IIdentifier identifier) throws ResourceNotFoundException {
		try {
			return enforceGetSubmodelAPIById(identifier);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected ISubmodelAPI enforceGetSubmodelAPIById(final IIdentifier smId) throws ResourceNotFoundException, InhibitException {
		return submodelAggregatorAuthorizer.enforceGetSubmodelAPI(
				subjectInformationProvider.get(),
				aasId,
				smId,
				() -> decoratedSubmodelAggregator.getSubmodelAPIById(smId)
		);
	}

	@Override
	public ISubmodelAPI getSubmodelAPIByIdShort(final String smIdShortPath) throws ResourceNotFoundException {
		try {
			return enforceGetSubmodelAPIByIdShort(smIdShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected ISubmodelAPI enforceGetSubmodelAPIByIdShort(final String smIdShortPath) throws ResourceNotFoundException, InhibitException {
		final ISubmodelAPI smAPI = decoratedSubmodelAggregator.getSubmodelAPIByIdShort(smIdShortPath);
		final IIdentifier smId = Optional.ofNullable(smAPI).map(ISubmodelAPI::getSubmodel).map(IIdentifiable::getIdentification).orElse(null);
		return submodelAggregatorAuthorizer.enforceGetSubmodelAPI(
				subjectInformationProvider.get(),
				aasId,
				smId,
				() -> smAPI
		);
	}

	@Override
	public void createSubmodel(final Submodel submodel) {
		try {
			enforceCreateSubmodel(submodel.getIdentification());
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedSubmodelAggregator.createSubmodel(submodel);
	}

	protected void enforceCreateSubmodel(final IIdentifier smId) throws InhibitException {
		submodelAggregatorAuthorizer.enforceCreateSubmodel(
				subjectInformationProvider.get(),
				aasId,
				smId
		);
	}

	@Override
	public void createSubmodel(final ISubmodelAPI submodelAPI) {
		try {
			enforceCreateSubmodel(submodelAPI);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedSubmodelAggregator.createSubmodel(submodelAPI);
	}

	protected void enforceCreateSubmodel(final ISubmodelAPI submodelAPI) throws InhibitException {
		final IIdentifier smId = Optional.ofNullable(submodelAPI).map(ISubmodelAPI::getSubmodel).map(IIdentifiable::getIdentification).orElse(null);
		submodelAggregatorAuthorizer.enforceCreateSubmodel(
				subjectInformationProvider.get(),
				aasId,
				smId
		);
	}

	@Override
	public void updateSubmodel(final Submodel submodel) throws ResourceNotFoundException {
		final IIdentifier smId = Optional.ofNullable(submodel).map(Submodel::getIdentification).orElse(null);
		try {
			enforceUpdateSubmodel(smId);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedSubmodelAggregator.updateSubmodel(submodel);
	}

	protected void enforceUpdateSubmodel(final IIdentifier smId) throws InhibitException {
		submodelAggregatorAuthorizer.enforceUpdateSubmodel(
				subjectInformationProvider.get(),
				aasId,
				smId
		);
	}

	@Override
	public void deleteSubmodelByIdentifier(final IIdentifier identifier) {
		try {
			enforceDeleteSubmodelByIdentifier(identifier);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedSubmodelAggregator.deleteSubmodelByIdentifier(identifier);
	}

	protected void enforceDeleteSubmodelByIdentifier(final IIdentifier smId) throws InhibitException {
		submodelAggregatorAuthorizer.enforceDeleteSubmodel(
				subjectInformationProvider.get(),
				aasId,
				smId
		);
	}

	@Override
	public void deleteSubmodelByIdShort(final String smIdShortPath) {
		try {
			enforceDeleteSubmodelByIdShort(smIdShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedSubmodelAggregator.deleteSubmodelByIdShort(smIdShortPath);
	}

	protected void enforceDeleteSubmodelByIdShort(final String smIdShortPath) throws InhibitException {
		final IIdentifier smId = Optional.ofNullable(decoratedSubmodelAggregator.getSubmodelbyIdShort(smIdShortPath)).map(IIdentifiable::getIdentification).orElse(null);
		submodelAggregatorAuthorizer.enforceDeleteSubmodel(
				subjectInformationProvider.get(),
				aasId,
				smId
		);
	}
}
