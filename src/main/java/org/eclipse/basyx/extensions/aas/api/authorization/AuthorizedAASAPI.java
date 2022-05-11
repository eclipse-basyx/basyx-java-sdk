/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.api.authorization;

import java.util.Optional;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.NotAuthorized;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IIdentifiable;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;

/**
 * Implementation variant for the AASAPI that authorizes each access to the API
 *
 * @author espen
 */
public class AuthorizedAASAPI implements IAASAPI {
	private IAASAPI decoratedAASAPI;
	private IAASAPIAuthorizer aasAPIAuthorizer;

	public AuthorizedAASAPI(IAASAPI decoratedAASAPI, IAASAPIAuthorizer aasAPIAuthorizer) {
		this.decoratedAASAPI = decoratedAASAPI;
		this.aasAPIAuthorizer = aasAPIAuthorizer;
	}

	@Override
	public IAssetAdministrationShell getAAS() {
		try {
			return enforceGetAAS();
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
	}

	protected IAssetAdministrationShell enforceGetAAS() throws InhibitException {
		final IAssetAdministrationShell aas = decoratedAASAPI.getAAS();
		final IIdentifier aasId = Optional.ofNullable(aas).map(IIdentifiable::getIdentification).orElse(null);
		return aasAPIAuthorizer.enforceGetAAS(
				aasId,
				aas
		);
	}

	@Override
	public void addSubmodel(IReference submodel) {
		try {
			enforceAddSubmodel(submodel);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
		decoratedAASAPI.addSubmodel(submodel);
	}

	protected void enforceAddSubmodel(final IReference smId) throws InhibitException {
		final IAssetAdministrationShell aas = decoratedAASAPI.getAAS();
		final IIdentifier aasId = Optional.ofNullable(aas).map(IIdentifiable::getIdentification).orElse(null);
		aasAPIAuthorizer.enforceAddSubmodel(
				aasId,
				smId
		);
	}

	@Override
	public void removeSubmodel(String idShort) {
		try {
			enforceRemoveSubmodel(idShort);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
		decoratedAASAPI.removeSubmodel(idShort);
	}

	protected void enforceRemoveSubmodel(final String smIdShort) throws InhibitException {
		final IAssetAdministrationShell aas = decoratedAASAPI.getAAS();
		final IIdentifier aasId = Optional.ofNullable(aas).map(IIdentifiable::getIdentification).orElse(null);
		aasAPIAuthorizer.enforceRemoveSubmodel(
				aasId,
				smIdShort
		);
	}
}
