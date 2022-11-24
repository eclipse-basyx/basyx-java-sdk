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
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationGrantedAuthorityAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.ElevatedCodeAuthentication;
import org.eclipse.basyx.extensions.shared.authorization.ElevatedCodeAuthentication.ElevatedCodeAuthenticationAreaHandler;
import org.eclipse.basyx.extensions.shared.authorization.ISubjectInformationProvider;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.NotAuthorized;
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

	protected final IAssetAdministrationShell aas;
	protected final ISubmodelAggregator decoratedSubmodelAggregator;
	protected final ISubmodelAggregatorAuthorizer<SubjectInformationType> submodelAggregatorAuthorizer;
	protected final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider;

	public AuthorizedSubmodelAggregator(
			final IAssetAdministrationShell aas,
			final ISubmodelAggregator decoratedSubmodelAggregator,
			final ISubmodelAggregatorAuthorizer<SubjectInformationType> submodelAggregatorAuthorizer,
			final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider
	) {
		this.aas = aas;
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

	/**
	 * @deprecated Please use
	 * {@link AuthorizedSubmodelAggregator#AuthorizedSubmodelAggregator(IAssetAdministrationShell, ISubmodelAggregator, ISubmodelAggregatorAuthorizer, ISubjectInformationProvider)} or
	 * {@link AuthorizedSubmodelAggregator#AuthorizedSubmodelAggregator(ISubmodelAggregator, ISubmodelAggregatorAuthorizer, ISubjectInformationProvider)}
	 * instead for more explicit parametrization.
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public AuthorizedSubmodelAggregator(final ISubmodelAggregator decoratedSubmodelAggregator) {
		this(
			decoratedSubmodelAggregator,
			(ISubmodelAggregatorAuthorizer<SubjectInformationType>) new GrantedAuthoritySubmodelAggregatorAuthorizer<>(new AuthenticationGrantedAuthorityAuthenticator()),
			(ISubjectInformationProvider<SubjectInformationType>) new AuthenticationContextProvider()
		);
	}

	@Override
	public Collection<ISubmodel> getSubmodelList() {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedSubmodelAggregator.getSubmodelList();
		}

		try {
			return authorizeGetSubmodelList();
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected Collection<ISubmodel> authorizeGetSubmodelList() throws InhibitException {
		return getSubmodelIdentifierList().stream().map(smId -> {
			try {
				return authorizeGetSubmodel(smId);
			} catch (final InhibitException e) {
				// leave out that submodel
				logger.info(e.getMessage(), e);
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	private Collection<IIdentifier> getSubmodelIdentifierList() throws InhibitException {
		final Collection<ISubmodel> authorizedSubmodelList = submodelAggregatorAuthorizer.authorizeGetSubmodelList(
				subjectInformationProvider.get(),
				aas,
				decoratedSubmodelAggregator::getSubmodelList
		);
		return authorizedSubmodelList.stream().map(IIdentifiable::getIdentification).collect(Collectors.toList());
	}

	@Override
	public ISubmodel getSubmodel(final IIdentifier identifier) throws ResourceNotFoundException {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedSubmodelAggregator.getSubmodel(identifier);
		}

		try {
			return authorizeGetSubmodel(identifier);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected ISubmodel authorizeGetSubmodel(final IIdentifier smId) throws ResourceNotFoundException, InhibitException {
		return submodelAggregatorAuthorizer.authorizeGetSubmodel(
				subjectInformationProvider.get(),
				aas,
				smId,
				() -> decoratedSubmodelAggregator.getSubmodel(smId)
		);
	}

	@Override
	public ISubmodel getSubmodelbyIdShort(final String smIdShortPath) throws ResourceNotFoundException {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedSubmodelAggregator.getSubmodelbyIdShort(smIdShortPath);
		}

		try {
			return authorizeGetSubmodelbyIdShort(smIdShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected ISubmodel authorizeGetSubmodelbyIdShort(final String smIdShortPath) throws ResourceNotFoundException, InhibitException {
		return submodelAggregatorAuthorizer.authorizeGetSubmodelbyIdShort(
				subjectInformationProvider.get(),
				aas,
				smIdShortPath,
				() -> decoratedSubmodelAggregator.getSubmodelbyIdShort(smIdShortPath)
		);
	}

	@Override
	public ISubmodelAPI getSubmodelAPIById(final IIdentifier identifier) throws ResourceNotFoundException {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedSubmodelAggregator.getSubmodelAPIById(identifier);
		}

		try {
			return authorizeGetSubmodelAPIById(identifier);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected ISubmodelAPI authorizeGetSubmodelAPIById(final IIdentifier smId) throws ResourceNotFoundException, InhibitException {
		return submodelAggregatorAuthorizer.authorizeGetSubmodelAPIById(
				subjectInformationProvider.get(),
				aas,
				smId,
				() -> decoratedSubmodelAggregator.getSubmodelAPIById(smId)
		);
	}

	@Override
	public ISubmodelAPI getSubmodelAPIByIdShort(final String smIdShortPath) throws ResourceNotFoundException {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedSubmodelAggregator.getSubmodelAPIByIdShort(smIdShortPath);
		}

		try {
			return authorizeGetSubmodelAPIByIdShort(smIdShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected ISubmodelAPI authorizeGetSubmodelAPIByIdShort(final String smIdShortPath) throws ResourceNotFoundException, InhibitException {
		try {
			try {
				return submodelAggregatorAuthorizer.authorizeGetSubmodelAPIByIdShort(
						subjectInformationProvider.get(),
						aas,
						smIdShortPath,
						() -> decoratedSubmodelAggregator.getSubmodelAPIByIdShort(smIdShortPath)
				);
			} catch (final InhibitException inhibitException) {
				throw inhibitException.reduceSmIdToSmIdShortPath(smIdShortPath);
			}
		} catch (final ResourceNotFoundException resourceNotFoundException) {
			// if the resource does not exist, check if we have permission to read every submodel,
			// if no, the authorization will throw an InhibitException, otherwise we throw the ResourceNotFoundException
			submodelAggregatorAuthorizer.authorizeGetSubmodelAPI(
					subjectInformationProvider.get(),
					aas,
					new ModelUrn("*"),
					() -> null
			);

			throw resourceNotFoundException;
		}
	}

	@Override
	public void createSubmodel(final Submodel submodel) {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			decoratedSubmodelAggregator.createSubmodel(submodel);
			return;
		}

		try {
			authorizeCreateSubmodel(submodel.getIdentification());
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedSubmodelAggregator.createSubmodel(submodel);
	}

	protected void authorizeCreateSubmodel(final IIdentifier smId) throws InhibitException {
		submodelAggregatorAuthorizer.authorizeCreateSubmodel(
				subjectInformationProvider.get(),
				aas,
				smId
		);
	}

	@Override
	public void createSubmodel(final ISubmodelAPI submodelAPI) {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			decoratedSubmodelAggregator.createSubmodel(submodelAPI);
			return;
		}

		try {
			authorizeCreateSubmodel(submodelAPI);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedSubmodelAggregator.createSubmodel(submodelAPI);
	}

	protected void authorizeCreateSubmodel(final ISubmodelAPI submodelAPI) throws InhibitException {
		final IIdentifier smId = Optional.ofNullable(submodelAPI).map(ISubmodelAPI::getSubmodel).map(IIdentifiable::getIdentification).orElse(null);
		submodelAggregatorAuthorizer.authorizeCreateSubmodel(
				subjectInformationProvider.get(),
				aas,
				smId
		);
	}

	@Override
	public void updateSubmodel(final Submodel submodel) throws ResourceNotFoundException {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			decoratedSubmodelAggregator.updateSubmodel(submodel);
			return;
		}

		final IIdentifier smId = Optional.ofNullable(submodel).map(Submodel::getIdentification).orElse(null);
		try {
			authorizeUpdateSubmodel(smId);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedSubmodelAggregator.updateSubmodel(submodel);
	}

	protected void authorizeUpdateSubmodel(final IIdentifier smId) throws InhibitException {
		submodelAggregatorAuthorizer.authorizeUpdateSubmodel(
				subjectInformationProvider.get(),
				aas,
				smId
		);
	}

	@Override
	public void deleteSubmodelByIdentifier(final IIdentifier identifier) {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			decoratedSubmodelAggregator.deleteSubmodelByIdentifier(identifier);
			return;
		}

		try {
			authorizeDeleteSubmodelByIdentifier(identifier);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedSubmodelAggregator.deleteSubmodelByIdentifier(identifier);
	}

	protected void authorizeDeleteSubmodelByIdentifier(final IIdentifier smId) throws InhibitException {
		submodelAggregatorAuthorizer.authorizeDeleteSubmodel(
				subjectInformationProvider.get(),
				aas,
				smId
		);
	}

	@Override
	public void deleteSubmodelByIdShort(final String smIdShortPath) {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			decoratedSubmodelAggregator.deleteSubmodelByIdShort(smIdShortPath);
			return;
		}

		try {
			authorizeDeleteSubmodelByIdShort(smIdShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedSubmodelAggregator.deleteSubmodelByIdShort(smIdShortPath);
	}

	protected void authorizeDeleteSubmodelByIdShort(final String smIdShortPath) throws InhibitException {
		final ISubmodel sm = getSmUnsecured(smIdShortPath);
		final IIdentifier smId = getSmIdUnsecured(sm);
		submodelAggregatorAuthorizer.authorizeDeleteSubmodel(
				subjectInformationProvider.get(),
				aas,
				smId
		);
	}

	private ISubmodel getSmUnsecured(final String smIdShortPath) throws ResourceNotFoundException {
		try (final ElevatedCodeAuthenticationAreaHandler ignored = ElevatedCodeAuthentication.enterElevatedCodeAuthenticationArea()) {
			return decoratedSubmodelAggregator.getSubmodelbyIdShort(smIdShortPath);
		}
	}

	private IIdentifier getSmIdUnsecured(final ISubmodel sm) throws ResourceNotFoundException {
		if (sm == null) {
			return null;
		}

		try (final ElevatedCodeAuthenticationAreaHandler ignored = ElevatedCodeAuthentication.enterElevatedCodeAuthenticationArea()) {
			return sm.getIdentification();
		}
	}
}
