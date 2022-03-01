/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.aggregator.authorization;

/**
 * An aggregator implementation that authorizes invocations before forwarding them to
 * an underlying aggregator implementation.
 * <p>
 * Implementation Note:
 * This implementation internally uses {@link SecurityContextHolder} to access the {@link SecurityContext} and its {@link Authentication}.
 * For read operations we require Read Scope Authority, for mutations we require Write Scope Authority.
 *
 * @author jungjan, fried, fischer
 * @see AASAggregatorScopes
 */
import java.util.Collection;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.extensions.shared.authorization.SecurityContextAuthorizer;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

public class AuthorizedAASAggregator implements IAASAggregator {
	private static final String SCOPE_AUTHORITY_PREFIX = "SCOPE_";
	public static final String READ_AUTHORITY = SCOPE_AUTHORITY_PREFIX + AASAggregatorScopes.READ_SCOPE;
	public static final String WRITE_AUTHORITY = SCOPE_AUTHORITY_PREFIX + AASAggregatorScopes.WRITE_SCOPE;

	private final IAASAggregator aasAggregator;
	private final SecurityContextAuthorizer authorizer = new SecurityContextAuthorizer();

	public AuthorizedAASAggregator(IAASAggregator aasAggregator) {
		this.aasAggregator = aasAggregator;
	}

	@Override
	public Collection<IAssetAdministrationShell> getAASList() {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(READ_AUTHORITY);
		return aasAggregator.getAASList();
	}

	@Override
	public IAssetAdministrationShell getAAS(IIdentifier shellId) throws ResourceNotFoundException {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(READ_AUTHORITY);
		return aasAggregator.getAAS(shellId);
	}

	@Override
	public IModelProvider getAASProvider(IIdentifier shellId) throws ResourceNotFoundException {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(READ_AUTHORITY);
		return aasAggregator.getAASProvider(shellId);
	}

	@Override
	public void createAAS(AssetAdministrationShell shell) {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(WRITE_AUTHORITY);
		aasAggregator.createAAS(shell);
	}

	@Override
	public void updateAAS(AssetAdministrationShell shell) throws ResourceNotFoundException {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(WRITE_AUTHORITY);
		aasAggregator.updateAAS(shell);
	}

	@Override
	public void deleteAAS(IIdentifier shellId) {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(WRITE_AUTHORITY);
		aasAggregator.deleteAAS(shellId);
	}
}
