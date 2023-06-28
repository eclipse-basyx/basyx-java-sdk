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

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.extensions.shared.authorization.internal.ElevatedCodeAuthentication;
import org.eclipse.basyx.extensions.shared.authorization.internal.ElevatedCodeAuthentication.ElevatedCodeAuthenticationAreaHandler;
import org.eclipse.basyx.extensions.shared.authorization.internal.ISubjectInformationProvider;
import org.eclipse.basyx.extensions.shared.authorization.internal.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.internal.NotAuthorizedException;
import org.eclipse.basyx.extensions.submodel.aggregator.authorization.SubmodelAggregatorScopes;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IIdentifiable;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation variant for the SubmodelAggregator that authorized each access
 *
 * @author wege
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

	public AuthorizedSubmodelAggregator(final IAssetAdministrationShell aas, final ISubmodelAggregator decoratedSubmodelAggregator, final ISubmodelAggregatorAuthorizer<SubjectInformationType> submodelAggregatorAuthorizer,
			final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider) {
		this.aas = aas;
		this.decoratedSubmodelAggregator = decoratedSubmodelAggregator;
		this.submodelAggregatorAuthorizer = submodelAggregatorAuthorizer;
		this.subjectInformationProvider = subjectInformationProvider;
	}

	public AuthorizedSubmodelAggregator(final ISubmodelAggregator decoratedSubmodelAggregator, final ISubmodelAggregatorAuthorizer<SubjectInformationType> submodelAggregatorAuthorizer,
			final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider) {
		this(null, decoratedSubmodelAggregator, submodelAggregatorAuthorizer, subjectInformationProvider);
	}

	@Override
	public Collection<ISubmodel> getSubmodelList() {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedSubmodelAggregator.getSubmodelList();
		}

		try {
			return authorizeGetSubmodelList();
		} catch (final InhibitException e) {
			throw new NotAuthorizedException(e);
		}
	}

	protected Collection<ISubmodel> authorizeGetSubmodelList() throws InhibitException {
		return getSubmodelIdentifierList().stream().map(smId -> {
			try {
				return authorizeGetSubmodel(smId);
			} catch (final InhibitException e) {
				// log and leave out submodel if authorization was unsuccessful
				logger.info(e.getMessage(), e);
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	private Collection<IIdentifier> getSubmodelIdentifierList() throws InhibitException {
		final Collection<ISubmodel> authorizedSubmodelList = submodelAggregatorAuthorizer.authorizeGetSubmodelList(subjectInformationProvider.get(), aas, decoratedSubmodelAggregator::getSubmodelList);
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
			throw new NotAuthorizedException(e);
		}
	}

	protected ISubmodel authorizeGetSubmodel(final IIdentifier smId) throws ResourceNotFoundException, InhibitException {
		final ISubmodel sm = getSmUnsecured(smId);
		final IReference smSemanticId = getSmSemanticIdUnsecured(sm);
		return submodelAggregatorAuthorizer.authorizeGetSubmodel(subjectInformationProvider.get(), aas, smId, smSemanticId, () -> decoratedSubmodelAggregator.getSubmodel(smId));
	}

	@Override
	public ISubmodel getSubmodelbyIdShort(final String smIdShortPath) throws ResourceNotFoundException {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedSubmodelAggregator.getSubmodelbyIdShort(smIdShortPath);
		}

		try {
			return authorizeGetSubmodelbyIdShort(smIdShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorizedException(e);
		}
	}

	protected ISubmodel authorizeGetSubmodelbyIdShort(final String smIdShortPath) throws ResourceNotFoundException, InhibitException {
		final ISubmodel sm = getSmUnsecured(smIdShortPath);
		final IIdentifier smId = getSmIdUnsecured(sm);
		final IReference smSemanticId = getSmSemanticIdUnsecured(sm);
		return submodelAggregatorAuthorizer.authorizeGetSubmodelbyIdShort(subjectInformationProvider.get(), aas, smId, smSemanticId, () -> decoratedSubmodelAggregator.getSubmodelbyIdShort(smIdShortPath));
	}

	@Override
	public ISubmodelAPI getSubmodelAPIById(final IIdentifier identifier) throws ResourceNotFoundException {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedSubmodelAggregator.getSubmodelAPIById(identifier);
		}

		try {
			return authorizeGetSubmodelAPIById(identifier);
		} catch (final InhibitException e) {
			throw new NotAuthorizedException(e);
		}
	}

	protected ISubmodelAPI authorizeGetSubmodelAPIById(final IIdentifier smId) throws ResourceNotFoundException, InhibitException {
		final ISubmodel sm = getSmUnsecured(smId);
		final IReference smSemanticId = getSmSemanticIdUnsecured(sm);
		return submodelAggregatorAuthorizer.authorizeGetSubmodelAPIById(subjectInformationProvider.get(), aas, smId, smSemanticId, () -> decoratedSubmodelAggregator.getSubmodelAPIById(smId));
	}

	@Override
	public ISubmodelAPI getSubmodelAPIByIdShort(final String smIdShortPath) throws ResourceNotFoundException {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedSubmodelAggregator.getSubmodelAPIByIdShort(smIdShortPath);
		}

		try {
			return authorizeGetSubmodelAPIByIdShort(smIdShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorizedException(e);
		}
	}

	protected ISubmodelAPI authorizeGetSubmodelAPIByIdShort(final String smIdShortPath) throws ResourceNotFoundException, InhibitException {
		final ISubmodel sm = getSmUnsecured(smIdShortPath);
		final IIdentifier smId = getSmIdUnsecured(sm);
		final IReference smSemanticId = getSmSemanticIdUnsecured(sm);
		try {
			try {
				return submodelAggregatorAuthorizer.authorizeGetSubmodelAPIByIdShort(subjectInformationProvider.get(), aas, smId, smSemanticId, () -> decoratedSubmodelAggregator.getSubmodelAPIByIdShort(smIdShortPath));
			} catch (final InhibitException inhibitException) {
				throw inhibitException.reduceSmIdToSmIdShortPath(smIdShortPath);
			}
		} catch (final ResourceNotFoundException resourceNotFoundException) {
			// if the resource does not exist, check if we have permission to read every
			// submodel,
			// if no, the authorization will throw an InhibitException, otherwise we throw
			// the ResourceNotFoundException
			final IIdentifier anySmId = new ModelUrn("*");
			final IReference anySemanticSmId = new Reference(new Key(KeyElements.SUBMODEL, true, "*", IdentifierType.CUSTOM));
			submodelAggregatorAuthorizer.authorizeGetSubmodelAPIById(subjectInformationProvider.get(), aas, anySmId, anySemanticSmId, () -> null);

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
			throw new NotAuthorizedException(e);
		}
		decoratedSubmodelAggregator.createSubmodel(submodel);
	}

	protected void authorizeCreateSubmodel(final IIdentifier smId) throws InhibitException {
		final ISubmodel sm = getSmUnsecured(smId);
		final IReference smSemanticId = getSmSemanticIdUnsecured(sm);
		submodelAggregatorAuthorizer.authorizeCreateSubmodel(subjectInformationProvider.get(), aas, smId, smSemanticId);
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
			throw new NotAuthorizedException(e);
		}
		decoratedSubmodelAggregator.createSubmodel(submodelAPI);
	}

	protected void authorizeCreateSubmodel(final ISubmodelAPI submodelAPI) throws InhibitException {
		final IIdentifier smId = Optional.ofNullable(submodelAPI).map(ISubmodelAPI::getSubmodel).map(IIdentifiable::getIdentification).orElse(null);
		final ISubmodel sm = getSmUnsecured(smId);
		final IReference smSemanticId = getSmSemanticIdUnsecured(sm);
		submodelAggregatorAuthorizer.authorizeCreateSubmodel(subjectInformationProvider.get(), aas, smId, smSemanticId);
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
			throw new NotAuthorizedException(e);
		}
		decoratedSubmodelAggregator.updateSubmodel(submodel);
	}

	protected void authorizeUpdateSubmodel(final IIdentifier smId) throws InhibitException {
		final ISubmodel sm = getSmUnsecured(smId);
		final IReference smSemanticId = getSmSemanticIdUnsecured(sm);
		submodelAggregatorAuthorizer.authorizeUpdateSubmodel(subjectInformationProvider.get(), aas, smId, smSemanticId);
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
			throw new NotAuthorizedException(e);
		}
		decoratedSubmodelAggregator.deleteSubmodelByIdentifier(identifier);
	}

	protected void authorizeDeleteSubmodelByIdentifier(final IIdentifier smId) throws InhibitException {
		final ISubmodel sm = getSmUnsecured(smId);
		final IReference smSemanticId = getSmSemanticIdUnsecured(sm);
		submodelAggregatorAuthorizer.authorizeDeleteSubmodelByIdentifier(subjectInformationProvider.get(), aas, smId, smSemanticId);
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
			throw new NotAuthorizedException(e);
		}
		decoratedSubmodelAggregator.deleteSubmodelByIdShort(smIdShortPath);
	}

	protected void authorizeDeleteSubmodelByIdShort(final String smIdShortPath) throws InhibitException {
		final ISubmodel sm = getSmUnsecured(smIdShortPath);
		final IIdentifier smId = getSmIdUnsecured(sm);
		final IReference smSemanticId = getSmSemanticIdUnsecured(sm);
		submodelAggregatorAuthorizer.authorizeDeleteSubmodelByIdentifier(subjectInformationProvider.get(), aas, smId, smSemanticId);
	}

	private ISubmodel getSmUnsecured(final IIdentifier smId) throws ResourceNotFoundException {
		try (final ElevatedCodeAuthenticationAreaHandler ignored = ElevatedCodeAuthentication.enterElevatedCodeAuthenticationArea()) {
			return decoratedSubmodelAggregator.getSubmodel(smId);
		}
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

	private IReference getSmSemanticIdUnsecured(final ISubmodel sm) throws ResourceNotFoundException {
		if (sm == null) {
			return null;
		}

		try (final ElevatedCodeAuthenticationAreaHandler ignored = ElevatedCodeAuthentication.enterElevatedCodeAuthenticationArea()) {
			return sm.getSemanticId();
		}
	}
}
