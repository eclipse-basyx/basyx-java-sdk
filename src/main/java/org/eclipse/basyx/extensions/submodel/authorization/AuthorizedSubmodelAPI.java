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
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IIdentifiable;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation variant for the SubmodelAggregator that authorizes each access
 *
 * @author espen
 *
 */
public class AuthorizedSubmodelAPI<SubjectInformationType> implements ISubmodelAPI {
	private static final String SCOPE_AUTHORITY_PREFIX = "SCOPE_";
	public static final String READ_AUTHORITY = SCOPE_AUTHORITY_PREFIX + SubmodelAPIScopes.READ_SCOPE;
	public static final String WRITE_AUTHORITY = SCOPE_AUTHORITY_PREFIX + SubmodelAPIScopes.WRITE_SCOPE;

	private static final Logger logger = LoggerFactory.getLogger(AuthorizedSubmodelAPI.class);

	protected final IIdentifier aasId;
	protected final ISubmodelAPI decoratedSubmodelAPI;
	protected final ISubmodelAPIAuthorizer<SubjectInformationType> submodelAPIAuthorizer;
	protected final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider;

	public AuthorizedSubmodelAPI(
			final ISubmodelAPI decoratedSubmodelAPI,
			final ISubmodelAPIAuthorizer<SubjectInformationType> submodelAPIAuthorizer,
			final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider
	) {
		this(null, decoratedSubmodelAPI, submodelAPIAuthorizer, subjectInformationProvider);
	}

	public AuthorizedSubmodelAPI(
			final IAssetAdministrationShell aas,
			final ISubmodelAPI decoratedSubmodelAPI,
			final ISubmodelAPIAuthorizer<SubjectInformationType> submodelAPIAuthorizer,
			final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider
	) {
		this.aasId = aas != null ? aas.getIdentification() : null;
		this.decoratedSubmodelAPI = decoratedSubmodelAPI;
		this.submodelAPIAuthorizer = submodelAPIAuthorizer;
		this.subjectInformationProvider = subjectInformationProvider;
	}

	/**
	 * @deprecated
	 */
	public AuthorizedSubmodelAPI(final ISubmodelAPI decoratedSubmodelAPI) {
		this(
			decoratedSubmodelAPI,
			(ISubmodelAPIAuthorizer<SubjectInformationType>) new GrantedAuthoritySubmodelAPIAuthorizer<>(new AuthenticationGrantedAuthorityAuthenticator()),
			(ISubjectInformationProvider<SubjectInformationType>) new AuthenticationContextProvider()
		);
	}

	@Override
	public ISubmodel getSubmodel() {
		try {
			return enforceGetSubmodel();
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected ISubmodel enforceGetSubmodel() throws InhibitException {
		final ISubmodel sm = decoratedSubmodelAPI.getSubmodel();
		final IIdentifier smId = Optional.ofNullable(sm).map(IIdentifiable::getIdentification).orElse(null);
		return submodelAPIAuthorizer.enforceGetSubmodel(
				subjectInformationProvider.get(),
				aasId,
				smId,
				() -> sm
		);
	}

	@Override
	public void addSubmodelElement(final ISubmodelElement elem) {
		try {
			enforceAddSubmodelElement(elem.getIdShort());
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedSubmodelAPI.addSubmodelElement(elem);
	}

	@Override
	public void addSubmodelElement(final String idShortPath, final ISubmodelElement elem) {
		try {
			enforceAddSubmodelElement(idShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedSubmodelAPI.addSubmodelElement(idShortPath, elem);
	}

	protected void enforceAddSubmodelElement(final String smElIdShortPath) throws InhibitException {
		final ISubmodel sm = decoratedSubmodelAPI.getSubmodel();
		final IIdentifier smId = Optional.ofNullable(sm).map(IIdentifiable::getIdentification).orElse(null);
		submodelAPIAuthorizer.enforceAddSubmodelElement(
				subjectInformationProvider.get(),
				aasId,
				smId,
				smElIdShortPath
		);
	}

	@Override
	public ISubmodelElement getSubmodelElement(final String idShortPath) {
		try {
			return enforceGetSubmodelElement(idShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected ISubmodelElement enforceGetSubmodelElement(final String smElIdShortPath) throws InhibitException {
		final ISubmodel sm = decoratedSubmodelAPI.getSubmodel();
		final IIdentifier smId = Optional.ofNullable(sm).map(IIdentifiable::getIdentification).orElse(null);
		return submodelAPIAuthorizer.enforceGetSubmodelElement(
				subjectInformationProvider.get(),
				aasId,
				smId,
				smElIdShortPath,
				() -> decoratedSubmodelAPI.getSubmodelElement(smElIdShortPath)
		);
	}

	@Override
	public void deleteSubmodelElement(final String idShortPath) {
		try {
			enforceDeleteSubmodelElement(idShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedSubmodelAPI.deleteSubmodelElement(idShortPath);
	}

	protected void enforceDeleteSubmodelElement(final String smElIdShortPath) throws InhibitException {
		final ISubmodel sm = decoratedSubmodelAPI.getSubmodel();
		final IIdentifier smId = Optional.ofNullable(sm).map(IIdentifiable::getIdentification).orElse(null);
		submodelAPIAuthorizer.enforceDeleteSubmodelElement(
				subjectInformationProvider.get(),
				aasId,
				smId,
				smElIdShortPath
		);
	}

	@Override
	public Collection<IOperation> getOperations() {
		try {
			return enforceGetOperations();
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected Collection<IOperation> enforceGetOperations() throws InhibitException {
		final ISubmodel sm = decoratedSubmodelAPI.getSubmodel();
		final IIdentifier smId = Optional.ofNullable(sm).map(IIdentifiable::getIdentification).orElse(null);
		final Collection<IOperation> enforcedOperations = submodelAPIAuthorizer.enforceGetOperations(
				subjectInformationProvider.get(),
				aasId,
				smId,
				decoratedSubmodelAPI::getOperations
		);
		return enforcedOperations.stream().map(operation -> {
			try {
				return (IOperation) enforceGetSubmodelElement(operation.getIdShort());
			} catch (final InhibitException e) {
				// leave out that operation
				logger.info(e.getMessage(), e);
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	@Override
	public Collection<ISubmodelElement> getSubmodelElements() {
		try {
			return enforceGetSubmodelElements();
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected Collection<ISubmodelElement> enforceGetSubmodelElements() throws InhibitException {
		// TODO check whether additional check for submodel access is necessary
		final ISubmodel sm = decoratedSubmodelAPI.getSubmodel();
		final IIdentifier smId = Optional.ofNullable(sm).map(IIdentifiable::getIdentification).orElse(null);
		final Collection<ISubmodelElement> enforcedSubmodelElements = submodelAPIAuthorizer.enforceGetSubmodelElements(
				subjectInformationProvider.get(),
				aasId,
				smId,
				decoratedSubmodelAPI::getSubmodelElements
		);
		return enforcedSubmodelElements.stream().map(smEl -> {
			try {
				return enforceGetSubmodelElement(smEl.getIdShort());
			} catch (final InhibitException e) {
				// leave out that submodelElement
				logger.info(e.getMessage(), e);
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	@Override
	public void updateSubmodelElement(final String idShortPath, final Object newValue) {
		try {
			enforceUpdateSubmodelElement(idShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedSubmodelAPI.updateSubmodelElement(idShortPath, newValue);
	}

	protected void enforceUpdateSubmodelElement(final String smElIdShortPath) throws InhibitException {
		final ISubmodel sm = decoratedSubmodelAPI.getSubmodel();
		final IIdentifier smId = Optional.ofNullable(sm).map(IIdentifiable::getIdentification).orElse(null);
		submodelAPIAuthorizer.enforceUpdateSubmodelElement(
				subjectInformationProvider.get(),
				aasId,
				smId,
				smElIdShortPath
		);
	}

	@Override
	public Object getSubmodelElementValue(final String idShortPath) {
		try {
			return enforceGetSubmodelElementValue(idShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected Object enforceGetSubmodelElementValue(final String smElIdShortPath) throws InhibitException {
		final ISubmodel sm = decoratedSubmodelAPI.getSubmodel();
		final IIdentifier smId = Optional.ofNullable(sm).map(IIdentifiable::getIdentification).orElse(null);
		return submodelAPIAuthorizer.enforceGetSubmodelElementValue(
				subjectInformationProvider.get(),
				aasId,
				smId,
				smElIdShortPath,
				() -> decoratedSubmodelAPI.getSubmodelElementValue(smElIdShortPath)
		);
	}

	@Override
	public Object invokeOperation(final String inputIdShortPath, final Object... params) {
		final String idShortPath = fixInvokeIdShortPath(inputIdShortPath);
		try {
			enforceInvokeOperation(idShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		return decoratedSubmodelAPI.invokeOperation(idShortPath, params);
	}

	@Override
	public Object invokeAsync(final String idShortPath, final Object... params) {
		try {
			enforceInvokeOperation(idShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		return decoratedSubmodelAPI.invokeAsync(idShortPath, params);
	}

	// https://github.com/eclipse-basyx/basyx-java-sdk/issues/60
	protected String fixInvokeIdShortPath(final String inputIdShortPath) {
		return inputIdShortPath.replaceFirst("/invoke$", "");
	}

	protected void enforceInvokeOperation(final String smElIdShortPath) throws InhibitException {
		final ISubmodel sm = decoratedSubmodelAPI.getSubmodel();
		final IIdentifier smId = Optional.ofNullable(sm).map(IIdentifiable::getIdentification).orElse(null);
		submodelAPIAuthorizer.enforceInvokeOperation(
				subjectInformationProvider.get(),
				aasId,
				smId,
				smElIdShortPath
		);
	}

	@Override
	public Object getOperationResult(final String smElIdShortPath, final String requestId) {
		try {
			return enforceGetOperationResult(smElIdShortPath, requestId);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected Object enforceGetOperationResult(final String smElIdShortPath, final String requestId) throws InhibitException {
		final ISubmodel sm = decoratedSubmodelAPI.getSubmodel();
		final IIdentifier smId = Optional.ofNullable(sm).map(IIdentifiable::getIdentification).orElse(null);
		return submodelAPIAuthorizer.enforceGetOperationResult(
				subjectInformationProvider.get(),
				aasId,
				smId,
				smElIdShortPath,
				requestId,
				() -> decoratedSubmodelAPI.getOperationResult(smElIdShortPath, requestId)
		);
	}
}
