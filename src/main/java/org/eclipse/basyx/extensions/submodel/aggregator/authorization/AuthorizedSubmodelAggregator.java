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
public class AuthorizedSubmodelAggregator implements ISubmodelAggregator {
	private static final Logger logger = LoggerFactory.getLogger(AuthorizedSubmodelAggregator.class);

	protected final ISubmodelAggregator decoratedSubmodelAggregator;
	protected final ISubmodelAggregatorAuthorizer submodelAggregatorAuthorizer;

	public AuthorizedSubmodelAggregator(ISubmodelAggregator decoratedSubmodelAggregator, ISubmodelAggregatorAuthorizer submodelAggregatorAuthorizer) {
		this.decoratedSubmodelAggregator = decoratedSubmodelAggregator;
		this.submodelAggregatorAuthorizer = submodelAggregatorAuthorizer;
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
		return submodelAggregatorAuthorizer.enforceGetSubmodel(
				smId,
				sm
		);
	}

	@Override
	public ISubmodel getSubmodel(IIdentifier identifier) throws ResourceNotFoundException {
		try {
			return enforceGetSubmodel(identifier);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
	}

	protected ISubmodel enforceGetSubmodel(IIdentifier smId) throws ResourceNotFoundException, InhibitException {
		return submodelAggregatorAuthorizer.enforceGetSubmodel(
				smId,
				decoratedSubmodelAggregator.getSubmodel(smId)
		);
	}

	@Override
	public ISubmodel getSubmodelbyIdShort(String idShort) throws ResourceNotFoundException {
		try {
			return enforceGetSubmodelbyIdShort(idShort);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
	}

	protected ISubmodel enforceGetSubmodelbyIdShort(String idShort) throws ResourceNotFoundException, InhibitException {
		final ISubmodel sm = decoratedSubmodelAggregator.getSubmodelbyIdShort(idShort);
		final IIdentifier smId = Optional.ofNullable(sm).map(IIdentifiable::getIdentification).orElse(null);
		return submodelAggregatorAuthorizer.enforceGetSubmodel(
				smId,
				sm
		);
	}

	@Override
	public ISubmodelAPI getSubmodelAPIById(IIdentifier identifier) throws ResourceNotFoundException {
		try {
			return enforceGetSubmodelAPIById(identifier);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
	}

	protected ISubmodelAPI enforceGetSubmodelAPIById(IIdentifier smId) throws ResourceNotFoundException, InhibitException {
		final ISubmodelAPI smAPI = decoratedSubmodelAggregator.getSubmodelAPIById(smId);
		return submodelAggregatorAuthorizer.enforceGetSubmodelAPI(
				smId,
				smAPI
		);
	}

	@Override
	public ISubmodelAPI getSubmodelAPIByIdShort(String idShort) throws ResourceNotFoundException {
		try {
			return enforceGetSubmodelAPIByIdShort(idShort);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
	}

	protected ISubmodelAPI enforceGetSubmodelAPIByIdShort(String idShort) throws ResourceNotFoundException, InhibitException {
		final ISubmodelAPI smAPI = decoratedSubmodelAggregator.getSubmodelAPIByIdShort(idShort);
		final IIdentifier smId = Optional.ofNullable(smAPI).map(ISubmodelAPI::getSubmodel).map(IIdentifiable::getIdentification).orElse(null);
		return submodelAggregatorAuthorizer.enforceGetSubmodelAPI(
				smId,
				smAPI
		);
	}

	@Override
	public void createSubmodel(Submodel submodel) {
		try {
			enforceCreateSubmodel(submodel.getIdentification());
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
		decoratedSubmodelAggregator.createSubmodel(submodel);
	}

	protected void enforceCreateSubmodel(final IIdentifier smId) throws InhibitException {
		submodelAggregatorAuthorizer.enforceCreateSubmodel(
				smId
		);
	}

	@Override
	public void createSubmodel(ISubmodelAPI submodelAPI) {
		try {
			enforceCreateSubmodel(submodelAPI);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
		decoratedSubmodelAggregator.createSubmodel(submodelAPI);
	}

	protected void enforceCreateSubmodel(ISubmodelAPI submodelAPI) throws InhibitException {
		final IIdentifier smId = Optional.ofNullable(submodelAPI).map(ISubmodelAPI::getSubmodel).map(IIdentifiable::getIdentification).orElse(null);
		submodelAggregatorAuthorizer.enforceCreateSubmodel(
				smId
		);
	}

	@Override
	public void updateSubmodel(Submodel submodel) throws ResourceNotFoundException {
		final IIdentifier smId = Optional.ofNullable(submodel).map(Submodel::getIdentification).orElse(null);
		try {
			enforceUpdateSubmodel(smId);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
		decoratedSubmodelAggregator.updateSubmodel(submodel);
	}

	protected void enforceUpdateSubmodel(final IIdentifier smId) throws InhibitException {
		submodelAggregatorAuthorizer.enforceUpdateSubmodel(smId);
	}

	@Override
	public void deleteSubmodelByIdentifier(IIdentifier identifier) {
		try {
			enforceDeleteSubmodelByIdentifier(identifier);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
		decoratedSubmodelAggregator.deleteSubmodelByIdentifier(identifier);
	}

	protected void enforceDeleteSubmodelByIdentifier(final IIdentifier smId) throws InhibitException {
		submodelAggregatorAuthorizer.enforceDeleteSubmodel(smId);
	}

	@Override
	public void deleteSubmodelByIdShort(String idShort) {
		try {
			enforceDeleteSubmodelByIdShort(idShort);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
		decoratedSubmodelAggregator.deleteSubmodelByIdShort(idShort);
	}

	protected void enforceDeleteSubmodelByIdShort(final String idShort) throws InhibitException {
		final IIdentifier smId = Optional.ofNullable(decoratedSubmodelAggregator.getSubmodelbyIdShort(idShort)).map(IIdentifiable::getIdentification).orElse(null);
		submodelAggregatorAuthorizer.enforceDeleteSubmodel(
				smId
		);
	}
}
