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
package org.eclipse.basyx.extensions.submodel.authorization.internal;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.extensions.shared.authorization.internal.ElevatedCodeAuthentication;
import org.eclipse.basyx.extensions.shared.authorization.internal.ISubjectInformationProvider;
import org.eclipse.basyx.extensions.shared.authorization.internal.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.internal.NotAuthorized;
import org.eclipse.basyx.extensions.submodel.authorization.SubmodelAPIScopes;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation variant for the SubmodelAggregator that authorizes each access
 *
 * @author espen
 */
public class AuthorizedSubmodelAPI<SubjectInformationType> implements ISubmodelAPI {
	private static final String SCOPE_AUTHORITY_PREFIX = "SCOPE_";
	public static final String READ_AUTHORITY = SCOPE_AUTHORITY_PREFIX + SubmodelAPIScopes.READ_SCOPE;
	public static final String WRITE_AUTHORITY = SCOPE_AUTHORITY_PREFIX + SubmodelAPIScopes.WRITE_SCOPE;
	public static final String EXECUTE_AUTHORITY = SCOPE_AUTHORITY_PREFIX + SubmodelAPIScopes.EXECUTE_SCOPE;

	private static final Logger logger = LoggerFactory.getLogger(AuthorizedSubmodelAPI.class);

	protected final IAssetAdministrationShell aas;
	protected final ISubmodelAPI decoratedSubmodelAPI;
	protected final ISubmodelAPIAuthorizer<SubjectInformationType> submodelAPIAuthorizer;
	protected final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider;

	public AuthorizedSubmodelAPI(final ISubmodelAPI decoratedSubmodelAPI, final ISubmodelAPIAuthorizer<SubjectInformationType> submodelAPIAuthorizer, final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider) {
		this(null, decoratedSubmodelAPI, submodelAPIAuthorizer, subjectInformationProvider);
	}

	public AuthorizedSubmodelAPI(final IAssetAdministrationShell aas, final ISubmodelAPI decoratedSubmodelAPI, final ISubmodelAPIAuthorizer<SubjectInformationType> submodelAPIAuthorizer,
			final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider) {
		this.aas = aas;
		this.decoratedSubmodelAPI = decoratedSubmodelAPI;
		this.submodelAPIAuthorizer = submodelAPIAuthorizer;
		this.subjectInformationProvider = subjectInformationProvider;
	}

	@Override
	public ISubmodel getSubmodel() {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedSubmodelAPI.getSubmodel();
		}

		try {
			return authorizeGetSubmodel();
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected ISubmodel authorizeGetSubmodel() throws InhibitException {
		final IIdentifier smId = getSmIdUnsecured();
		return submodelAPIAuthorizer.authorizeGetSubmodel(subjectInformationProvider.get(), aas, smId, decoratedSubmodelAPI::getSubmodel);
	}

	@Override
	public void addSubmodelElement(final ISubmodelElement elem) {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			decoratedSubmodelAPI.addSubmodelElement(elem);
			return;
		}

		try {
			authorizeAddSubmodelElement(elem.getIdShort());
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedSubmodelAPI.addSubmodelElement(elem);
	}

	@Override
	public void addSubmodelElement(final String idShortPath, final ISubmodelElement elem) {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			decoratedSubmodelAPI.addSubmodelElement(idShortPath, elem);
			return;
		}

		try {
			authorizeAddSubmodelElement(idShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedSubmodelAPI.addSubmodelElement(idShortPath, elem);
	}

	protected void authorizeAddSubmodelElement(final String smElIdShortPath) throws InhibitException {
		final IIdentifier smId = getSmIdUnsecured();
		submodelAPIAuthorizer.authorizeAddSubmodelElement(subjectInformationProvider.get(), aas, smId, smElIdShortPath);
	}

	@Override
	public ISubmodelElement getSubmodelElement(final String idShortPath) {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedSubmodelAPI.getSubmodelElement(idShortPath);
		}

		try {
			return authorizeGetSubmodelElement(idShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected ISubmodelElement authorizeGetSubmodelElement(final String smElIdShortPath) throws InhibitException {
		final IIdentifier smId = getSmIdUnsecured();
		return submodelAPIAuthorizer.authorizeGetSubmodelElement(subjectInformationProvider.get(), aas, smId, smElIdShortPath, () -> decoratedSubmodelAPI.getSubmodelElement(smElIdShortPath));
	}

	@Override
	public void deleteSubmodelElement(final String idShortPath) {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			decoratedSubmodelAPI.deleteSubmodelElement(idShortPath);
			return;
		}

		try {
			authorizeDeleteSubmodelElement(idShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedSubmodelAPI.deleteSubmodelElement(idShortPath);
	}

	protected void authorizeDeleteSubmodelElement(final String smElIdShortPath) throws InhibitException {
		final IIdentifier smId = getSmIdUnsecured();
		submodelAPIAuthorizer.authorizeDeleteSubmodelElement(subjectInformationProvider.get(), aas, smId, smElIdShortPath);
	}

	@Override
	public Collection<IOperation> getOperations() {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedSubmodelAPI.getOperations();
		}

		try {
			return authorizeGetOperations();
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected Collection<IOperation> authorizeGetOperations() throws InhibitException {
		return getOperationListOnly().stream().map(operation -> {
			try {
				return (IOperation) authorizeGetSubmodelElement(operation.getIdShort());
			} catch (final InhibitException e) {
				// log and leave out operation if authorization was unsuccessful
				logger.info(e.getMessage(), e);
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	private Collection<IOperation> getOperationListOnly() throws InhibitException {
		return submodelAPIAuthorizer.authorizeGetOperations(subjectInformationProvider.get(), aas, decoratedSubmodelAPI::getSubmodel, decoratedSubmodelAPI::getOperations);
	}

	@Override
	public Collection<ISubmodelElement> getSubmodelElements() {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedSubmodelAPI.getSubmodelElements();
		}

		try {
			return authorizeGetSubmodelElements();
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected Collection<ISubmodelElement> authorizeGetSubmodelElements() throws InhibitException {
		return getSubmodelElementListOnly().stream().map(smEl -> {
			try {
				return authorizeGetSubmodelElement(smEl.getIdShort());
			} catch (final InhibitException e) {
				// log and leave out submodelElement if authorization was unsuccessful
				logger.info(e.getMessage(), e);
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	private Collection<ISubmodelElement> getSubmodelElementListOnly() throws InhibitException {
		return submodelAPIAuthorizer.authorizeGetSubmodelElements(subjectInformationProvider.get(), aas, decoratedSubmodelAPI::getSubmodel, decoratedSubmodelAPI::getSubmodelElements);
	}

	@Override
	public void updateSubmodelElement(final String idShortPath, final Object newValue) {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			decoratedSubmodelAPI.updateSubmodelElement(idShortPath, newValue);
			return;
		}

		try {
			authorizeUpdateSubmodelElement(idShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedSubmodelAPI.updateSubmodelElement(idShortPath, newValue);
	}

	protected void authorizeUpdateSubmodelElement(final String smElIdShortPath) throws InhibitException {
		final IIdentifier smId = getSmIdUnsecured();
		submodelAPIAuthorizer.authorizeUpdateSubmodelElement(subjectInformationProvider.get(), aas, smId, smElIdShortPath);
	}

	@Override
	public Object getSubmodelElementValue(final String idShortPath) {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedSubmodelAPI.getSubmodelElementValue(idShortPath);
		}

		try {
			return authorizeGetSubmodelElementValue(idShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected Object authorizeGetSubmodelElementValue(final String smElIdShortPath) throws InhibitException {
		final IIdentifier smId = getSmIdUnsecured();
		return submodelAPIAuthorizer.authorizeGetSubmodelElementValue(subjectInformationProvider.get(), aas, smId, smElIdShortPath, () -> decoratedSubmodelAPI.getSubmodelElementValue(smElIdShortPath));
	}

	@Override
	public Object invokeOperation(final String inputIdShortPath, final Object... params) {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedSubmodelAPI.invokeOperation(inputIdShortPath, params);
		}

		final String idShortPath = fixInvokeIdShortPath(inputIdShortPath);
		try {
			authorizeInvokeOperation(idShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		return decoratedSubmodelAPI.invokeOperation(idShortPath, params);
	}

	@Override
	public Object invokeAsync(final String idShortPath, final Object... params) {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedSubmodelAPI.invokeAsync(idShortPath, params);
		}

		try {
			authorizeInvokeOperation(idShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		return decoratedSubmodelAPI.invokeAsync(idShortPath, params);
	}

	// https://github.com/eclipse-basyx/basyx-java-sdk/issues/60
	protected String fixInvokeIdShortPath(final String inputIdShortPath) {
		return inputIdShortPath.replaceFirst("/invoke$", "");
	}

	protected void authorizeInvokeOperation(final String smElIdShortPath) throws InhibitException {
		final IIdentifier smId = getSmIdUnsecured();
		submodelAPIAuthorizer.authorizeInvokeOperation(subjectInformationProvider.get(), aas, smId, smElIdShortPath);
	}

	@Override
	public Object getOperationResult(final String smElIdShortPath, final String requestId) {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedSubmodelAPI.getOperationResult(smElIdShortPath, requestId);
		}

		try {
			return authorizeGetOperationResult(smElIdShortPath, requestId);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected Object authorizeGetOperationResult(final String smElIdShortPath, final String requestId) throws InhibitException {
		final IIdentifier smId = getSmIdUnsecured();
		return submodelAPIAuthorizer.authorizeGetOperationResult(subjectInformationProvider.get(), aas, smId, smElIdShortPath, requestId, () -> decoratedSubmodelAPI.getOperationResult(smElIdShortPath, requestId));
	}

	private IIdentifier getSmIdUnsecured() throws ResourceNotFoundException {
		final ISubmodel sm = decoratedSubmodelAPI.getSubmodel();

		if (sm == null) {
			return null;
		}

		return sm.getIdentification();
	}
}
