/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.extensions.submodel.authorization;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.NotAuthorized;
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
public class AuthorizedSubmodelAPI implements ISubmodelAPI {
	private static final Logger logger = LoggerFactory.getLogger(AuthorizedSubmodelAPI.class);

	protected final IIdentifier aasId;
	protected final ISubmodelAPI decoratedSubmodelAPI;
	protected final ISubmodelAPIAuthorizer submodelAPIAuthorizer;

	public AuthorizedSubmodelAPI(ISubmodelAPI decoratedSubmodelAPI, ISubmodelAPIAuthorizer submodelAPIAuthorizer) {
		this(null, decoratedSubmodelAPI, submodelAPIAuthorizer);
	}

	public AuthorizedSubmodelAPI(IAssetAdministrationShell aas, ISubmodelAPI decoratedSubmodelAPI, ISubmodelAPIAuthorizer submodelAPIAuthorizer) {
		this.aasId = aas != null ? aas.getIdentification() : null;
		this.decoratedSubmodelAPI = decoratedSubmodelAPI;
		this.submodelAPIAuthorizer = submodelAPIAuthorizer;
	}

	@Override
	public ISubmodel getSubmodel() {
		try {
			return enforceGetSubmodel();
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
	}

	protected ISubmodel enforceGetSubmodel() throws InhibitException {
		final ISubmodel sm = decoratedSubmodelAPI.getSubmodel();
		final IIdentifier smId = Optional.ofNullable(sm).map(IIdentifiable::getIdentification).orElse(null);
		return submodelAPIAuthorizer.enforceGetSubmodel(
				aasId,
				smId,
				sm
		);
	}

	@Override
	public void addSubmodelElement(ISubmodelElement elem) {
		try {
			enforceAddSubmodelElement(elem.getIdShort());
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
		decoratedSubmodelAPI.addSubmodelElement(elem);
	}

	@Override
	public void addSubmodelElement(String idShortPath, ISubmodelElement elem) {
		try {
			enforceAddSubmodelElement(idShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
		decoratedSubmodelAPI.addSubmodelElement(idShortPath, elem);
	}

	protected void enforceAddSubmodelElement(final String smElIdShortPath) throws InhibitException {
		final ISubmodel sm = decoratedSubmodelAPI.getSubmodel();
		final IIdentifier smId = Optional.ofNullable(sm).map(IIdentifiable::getIdentification).orElse(null);
		submodelAPIAuthorizer.enforceAddSubmodelElement(
				aasId,
				smId,
				smElIdShortPath
		);
	}

	@Override
	public ISubmodelElement getSubmodelElement(String idShortPath) {
		try {
			return enforceGetSubmodelElement(idShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
	}

	protected ISubmodelElement enforceGetSubmodelElement(final String smElIdShortPath) throws InhibitException {
		final ISubmodel sm = decoratedSubmodelAPI.getSubmodel();
		final IIdentifier smId = Optional.ofNullable(sm).map(IIdentifiable::getIdentification).orElse(null);
		final ISubmodelElement smEl = decoratedSubmodelAPI.getSubmodelElement(smElIdShortPath);
		return submodelAPIAuthorizer.enforceGetSubmodelElement(
				aasId,
				smId,
				smElIdShortPath,
				smEl
		);
	}

	@Override
	public void deleteSubmodelElement(String idShortPath) {
		try {
			enforceDeleteSubmodelElement(idShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
		decoratedSubmodelAPI.deleteSubmodelElement(idShortPath);
	}

	protected void enforceDeleteSubmodelElement(final String smElIdShortPath) throws InhibitException {
		final ISubmodel sm = decoratedSubmodelAPI.getSubmodel();
		final IIdentifier smId = Optional.ofNullable(sm).map(IIdentifiable::getIdentification).orElse(null);
		submodelAPIAuthorizer.enforceDeleteSubmodelElement(
				aasId,
				smId,
				smElIdShortPath
		);
	}

	@Override
	public Collection<IOperation> getOperations() {
		return enforceGetOperations();
	}

	protected Collection<IOperation> enforceGetOperations() {
		return decoratedSubmodelAPI.getOperations().stream().map(operation -> {
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
		return enforceGetSubmodelElements();
	}

	protected Collection<ISubmodelElement> enforceGetSubmodelElements() {
		// TODO check whether additional check for submodel access is necessary
		return decoratedSubmodelAPI.getSubmodelElements().stream().map(smEl -> {
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
	public void updateSubmodelElement(String idShortPath, Object newValue) {
		try {
			enforceUpdateSubmodelElement(idShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
		decoratedSubmodelAPI.updateSubmodelElement(idShortPath, newValue);
	}

	protected void enforceUpdateSubmodelElement(final String smElIdShortPath) throws InhibitException {
		final ISubmodel sm = decoratedSubmodelAPI.getSubmodel();
		final IIdentifier smId = Optional.ofNullable(sm).map(IIdentifiable::getIdentification).orElse(null);
		submodelAPIAuthorizer.enforceUpdateSubmodelElement(
				aasId,
				smId,
				smElIdShortPath
		);
	}

	@Override
	public Object getSubmodelElementValue(String idShortPath) {
		try {
			return enforceGetSubmodelElementValue(idShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
	}

	protected Object enforceGetSubmodelElementValue(final String smElIdShortPath) throws InhibitException {
		final ISubmodel sm = decoratedSubmodelAPI.getSubmodel();
		final IIdentifier smId = Optional.ofNullable(sm).map(IIdentifiable::getIdentification).orElse(null);
		final Object value = decoratedSubmodelAPI.getSubmodelElementValue(smElIdShortPath);
		return submodelAPIAuthorizer.enforceGetSubmodelElementValue(
				aasId,
				smId,
				smElIdShortPath,
				value
		);
	}

	@Override
	public Object invokeOperation(String inputIdShortPath, Object... params) {
		final String idShortPath = fixInvokeIdShortPath(inputIdShortPath);
		try {
			enforceInvokeOperation(idShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
		return decoratedSubmodelAPI.invokeOperation(idShortPath, params);
	}

	@Override
	public Object invokeAsync(String idShortPath, Object... params) {
		try {
			enforceInvokeOperation(idShortPath);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
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
				aasId,
				smId,
				smElIdShortPath
		);
	}

	@Override
	public Object getOperationResult(String idShort, String requestId) {
		try {
			return enforceGetOperationResult(idShort, requestId);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
	}

	protected Object enforceGetOperationResult(final String smElIdShortPath, final String requestId) throws InhibitException {
		final ISubmodel sm = decoratedSubmodelAPI.getSubmodel();
		final IIdentifier smId = Optional.ofNullable(sm).map(IIdentifiable::getIdentification).orElse(null);
		final Object operationResult = decoratedSubmodelAPI.getOperationResult(smElIdShortPath, requestId);
		return submodelAPIAuthorizer.enforceGetOperationResult(
				aasId,
				smId,
				smElIdShortPath,
				requestId,
				operationResult
		);
	}
}
