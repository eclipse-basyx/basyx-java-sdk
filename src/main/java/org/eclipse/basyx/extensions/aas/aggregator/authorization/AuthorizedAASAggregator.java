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
import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.NotAuthorized;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation variant for the AASAggregator that authorized each access
 *
 * @author wege
 */
public class AuthorizedAASAggregator implements IAASAggregator {
	private static final Logger logger = LoggerFactory.getLogger(AuthorizedAASAggregator.class);

	protected final IAASAggregator decoratedAasAggregator;
	protected final IAASAggregatorPep aasAggregatorPep;

	public AuthorizedAASAggregator(IAASAggregator decoratedAasAggregator, IAASAggregatorPep aasAggregatorPep) {
		this.decoratedAasAggregator = decoratedAasAggregator;
		this.aasAggregatorPep = aasAggregatorPep;
	}

	@Override
	public Collection<IAssetAdministrationShell> getAASList() {
		return enforceGetAASList();
	}

	protected Collection<IAssetAdministrationShell> enforceGetAASList() {
		return decoratedAasAggregator.getAASList().stream().map(aas -> {
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
	public IAssetAdministrationShell getAAS(IIdentifier shellId) throws ResourceNotFoundException {
		try {
			return enforceGetAAS(shellId);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
	}

	protected IAssetAdministrationShell enforceGetAAS(final IIdentifier aasId) throws InhibitException {
		return aasAggregatorPep.enforceGetAAS(
				aasId,
				decoratedAasAggregator.getAAS(aasId)
		);
	}

	@Override
	public IModelProvider getAASProvider(IIdentifier shellId) throws ResourceNotFoundException {
		try {
			return enforceGetAASProvider(shellId);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
	}

	protected IModelProvider enforceGetAASProvider(IIdentifier aasId) throws ResourceNotFoundException, InhibitException {
		// TODO: does this give access to everything? then we might need write and execute permissions too
		return aasAggregatorPep.enforceGetAASProvider(
				aasId,
				decoratedAasAggregator.getAASProvider(aasId)
		);
	}

	@Override
	public void createAAS(AssetAdministrationShell shell) {
		try {
			enforceCreateAAS(shell);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
		decoratedAasAggregator.createAAS(shell);
	}

	protected void enforceCreateAAS(final IAssetAdministrationShell aas) throws InhibitException {
		aasAggregatorPep.enforceCreateAAS(
				aas.getIdentification()
		);
	}

	@Override
	public void updateAAS(AssetAdministrationShell shell) throws ResourceNotFoundException {
		try {
			enforceUpdateAAS(shell);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
		decoratedAasAggregator.updateAAS(shell);
	}

	protected void enforceUpdateAAS(final IAssetAdministrationShell aas) throws InhibitException {
		aasAggregatorPep.enforceUpdateAAS(
				aas.getIdentification()
		);
	}

	@Override
	public void deleteAAS(IIdentifier shellId) {
		try {
			enforceDeleteAAS(shellId);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
		decoratedAasAggregator.deleteAAS(shellId);
	}

	protected void enforceDeleteAAS(final IIdentifier aasId) throws InhibitException {
		aasAggregatorPep.enforceDeleteAAS(
				aasId
		);
	}
}
