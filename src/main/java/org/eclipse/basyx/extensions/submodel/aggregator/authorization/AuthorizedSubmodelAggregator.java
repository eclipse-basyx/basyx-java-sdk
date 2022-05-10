/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.extensions.submodel.aggregator.authorization;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IIdentifiable;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation variant for the SubmodelAggregator that authorized each access
 *
 * @author espen, wege
 *
 */
public class AuthorizedSubmodelAggregator implements ISubmodelAggregator {
	private static final Logger logger = LoggerFactory.getLogger(AuthorizedSubmodelAggregator.class);

	protected final ISubmodelAggregator decoratedSubmodelAggregator;
	protected final ISubmodelAggregatorPep submodelAggregatorPep;

	public AuthorizedSubmodelAggregator(ISubmodelAggregator decoratedSubmodelAggregator, ISubmodelAggregatorPep submodelAggregatorPep) {
		this.decoratedSubmodelAggregator = decoratedSubmodelAggregator;
		this.submodelAggregatorPep = submodelAggregatorPep;
	}

	@Override
	public Collection<ISubmodel> getSubmodelList() {
		return enforceGetSubmodelList();
	}

	protected Collection<ISubmodel> enforceGetSubmodelList() {
		return decoratedSubmodelAggregator.getSubmodelList().stream().map(submodel -> {
			try {
				return enforceGetSubmodel(submodel);
			} catch (final InhibitException e) {
				logger.info(e.getMessage(), e);
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	protected ISubmodel enforceGetSubmodel(ISubmodel sm) throws ResourceNotFoundException, InhibitException {
		final IIdentifier smId = Optional.ofNullable(sm).map(IIdentifiable::getIdentification).orElse(null);
		return submodelAggregatorPep.enforceGetSubmodel(
				smId,
				sm
		);
	}

	@Override
	public ISubmodel getSubmodel(IIdentifier identifier) throws ResourceNotFoundException {
		try {
			return enforceGetSubmodel(identifier);
		} catch (final InhibitException e) {
			throw new ProviderException("no access");
		}
	}

	protected ISubmodel enforceGetSubmodel(IIdentifier smId) throws ResourceNotFoundException, InhibitException {
		return submodelAggregatorPep.enforceGetSubmodel(
				smId,
				decoratedSubmodelAggregator.getSubmodel(smId)
		);
	}

	@Override
	public ISubmodel getSubmodelbyIdShort(String idShort) throws ResourceNotFoundException {
		try {
			return enforceGetSubmodelbyIdShort(idShort);
		} catch (final InhibitException e) {
			throw new ProviderException("no access");
		}
	}

	protected ISubmodel enforceGetSubmodelbyIdShort(String idShort) throws ResourceNotFoundException, InhibitException {
		final ISubmodel sm = decoratedSubmodelAggregator.getSubmodelbyIdShort(idShort);
		final IIdentifier smId = Optional.ofNullable(sm).map(IIdentifiable::getIdentification).orElse(null);
		return submodelAggregatorPep.enforceGetSubmodel(
				smId,
				sm
		);
	}

	@Override
	public ISubmodelAPI getSubmodelAPIById(IIdentifier identifier) throws ResourceNotFoundException {
		try {
			return enforceGetSubmodelAPIById(identifier);
		} catch (final InhibitException e) {
			throw new ProviderException("no access");
		}
	}

	protected ISubmodelAPI enforceGetSubmodelAPIById(IIdentifier smId) throws ResourceNotFoundException, InhibitException {
		final ISubmodelAPI smAPI = decoratedSubmodelAggregator.getSubmodelAPIById(smId);
		return submodelAggregatorPep.enforceGetSubmodelAPI(
				smId,
				smAPI
		);
	}

	@Override
	public ISubmodelAPI getSubmodelAPIByIdShort(String idShort) throws ResourceNotFoundException {
		try {
			return enforceGetSubmodelAPIByIdShort(idShort);
		} catch (final InhibitException e) {
			throw new ProviderException("no access");
		}
	}

	protected ISubmodelAPI enforceGetSubmodelAPIByIdShort(String idShort) throws ResourceNotFoundException, InhibitException {
		final ISubmodelAPI smAPI = decoratedSubmodelAggregator.getSubmodelAPIByIdShort(idShort);
		final IIdentifier smId = Optional.ofNullable(smAPI).map(ISubmodelAPI::getSubmodel).map(IIdentifiable::getIdentification).orElse(null);
		return submodelAggregatorPep.enforceGetSubmodelAPI(
				smId,
				smAPI
		);
	}

	@Override
	public void createSubmodel(Submodel submodel) {
		try {
			enforceCreateSubmodel(submodel.getIdentification());
		} catch (final InhibitException e) {
			throw new ProviderException("no access");
		}
		decoratedSubmodelAggregator.createSubmodel(submodel);
	}

	protected void enforceCreateSubmodel(final IIdentifier smId) throws InhibitException {
		submodelAggregatorPep.enforceCreateSubmodel(
				smId
		);
	}

	@Override
	public void createSubmodel(ISubmodelAPI submodelAPI) {
		try {
			enforceCreateSubmodel(submodelAPI);
		} catch (final InhibitException e) {
			throw new ProviderException("no access");
		}
		decoratedSubmodelAggregator.createSubmodel(submodelAPI);
	}

	protected void enforceCreateSubmodel(ISubmodelAPI submodelAPI) throws InhibitException {
		final IIdentifier smId = Optional.ofNullable(submodelAPI).map(ISubmodelAPI::getSubmodel).map(IIdentifiable::getIdentification).orElse(null);
		submodelAggregatorPep.enforceCreateSubmodel(
				smId
		);
	}

	@Override
	public void updateSubmodel(Submodel submodel) throws ResourceNotFoundException {
		final IIdentifier smId = Optional.ofNullable(submodel).map(Submodel::getIdentification).orElse(null);
		try {
			enforceUpdateSubmodel(smId);
		} catch (final InhibitException e) {
			throw new ProviderException("no access");
		}
		decoratedSubmodelAggregator.updateSubmodel(submodel);
	}

	protected void enforceUpdateSubmodel(final IIdentifier smId) throws InhibitException {
		submodelAggregatorPep.enforceUpdateSubmodel(smId);
	}

	@Override
	public void deleteSubmodelByIdentifier(IIdentifier identifier) {
		try {
			enforceDeleteSubmodelByIdentifier(identifier);
		} catch (final InhibitException e) {
			throw new ProviderException("no access");
		}
		decoratedSubmodelAggregator.deleteSubmodelByIdentifier(identifier);
	}

	protected void enforceDeleteSubmodelByIdentifier(final IIdentifier smId) throws InhibitException {
		submodelAggregatorPep.enforceDeleteSubmodel(smId);
	}

	@Override
	public void deleteSubmodelByIdShort(String idShort) {
		try {
			enforceDeleteSubmodelByIdShort(idShort);
		} catch (final InhibitException e) {
			throw new ProviderException("no access");
		}
		decoratedSubmodelAggregator.deleteSubmodelByIdShort(idShort);
	}

	protected void enforceDeleteSubmodelByIdShort(final String idShort) throws InhibitException {
		final IIdentifier smId = Optional.ofNullable(decoratedSubmodelAggregator.getSubmodelbyIdShort(idShort)).map(IIdentifiable::getIdentification).orElse(null);
		submodelAggregatorPep.enforceDeleteSubmodel(
				smId
		);
	}
}
