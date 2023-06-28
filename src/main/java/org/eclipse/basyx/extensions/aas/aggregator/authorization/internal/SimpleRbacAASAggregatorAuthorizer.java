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
package org.eclipse.basyx.extensions.aas.aggregator.authorization.internal;

import java.util.Collection;
import java.util.function.Supplier;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.extensions.aas.aggregator.authorization.AASAggregatorScopes;
import org.eclipse.basyx.extensions.shared.authorization.internal.BaSyxObjectTargetInformation;
import org.eclipse.basyx.extensions.shared.authorization.internal.IRbacRuleChecker;
import org.eclipse.basyx.extensions.shared.authorization.internal.IRoleAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.internal.IdHelper;
import org.eclipse.basyx.extensions.shared.authorization.internal.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.internal.SimpleRbacHelper;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

/**
 * Simple role based implementation for {@link IAASAggregatorAuthorizer}.
 *
 * @author wege
 */
public class SimpleRbacAASAggregatorAuthorizer<SubjectInformationType> implements IAASAggregatorAuthorizer<SubjectInformationType> {
	protected IRbacRuleChecker rbacRuleChecker;
	protected IRoleAuthenticator<SubjectInformationType> roleAuthenticator;

	public SimpleRbacAASAggregatorAuthorizer(final IRbacRuleChecker rbacRuleChecker, final IRoleAuthenticator<SubjectInformationType> roleAuthenticator) {
		this.rbacRuleChecker = rbacRuleChecker;
		this.roleAuthenticator = roleAuthenticator;
	}

	@Override
	public Collection<IAssetAdministrationShell> authorizeGetAASList(final SubjectInformationType subjectInformation, final Supplier<Collection<IAssetAdministrationShell>> aasListSupplier) throws InhibitException {
		return aasListSupplier.get();
	}

	@Override
	public IAssetAdministrationShell authorizeGetAAS(final SubjectInformationType subjectInformation, final IIdentifier aasId, final Supplier<IAssetAdministrationShell> aasSupplier) throws InhibitException {
		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, AASAggregatorScopes.READ_SCOPE, new BaSyxObjectTargetInformation(IdHelper.getIdentifierId(aasId), null, null, null));

		return aasSupplier.get();
	}

	@Override
	public IModelProvider authorizeGetAASProvider(final SubjectInformationType subjectInformation, final IIdentifier aasId, final Supplier<IModelProvider> modelProviderSupplier) throws InhibitException {
		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, AASAggregatorScopes.READ_SCOPE, new BaSyxObjectTargetInformation(IdHelper.getIdentifierId(aasId), null, null, null));

		return modelProviderSupplier.get();
	}

	@Override
	public void authorizeCreateAAS(final SubjectInformationType subjectInformation, final AssetAdministrationShell aas) throws InhibitException {
		final IIdentifier aasId = aas.getIdentification();

		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, AASAggregatorScopes.WRITE_SCOPE, new BaSyxObjectTargetInformation(IdHelper.getIdentifierId(aasId), null, null, null));
	}

	@Override
	public void authorizeUpdateAAS(final SubjectInformationType subjectInformation, final AssetAdministrationShell aas) throws InhibitException {
		final IIdentifier aasId = aas.getIdentification();

		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, AASAggregatorScopes.WRITE_SCOPE, new BaSyxObjectTargetInformation(IdHelper.getIdentifierId(aasId), null, null, null));
	}

	@Override
	public void authorizeDeleteAAS(final SubjectInformationType subjectInformation, final IIdentifier aasId) throws InhibitException {
		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, AASAggregatorScopes.WRITE_SCOPE, new BaSyxObjectTargetInformation(IdHelper.getIdentifierId(aasId), null, null, null));
	}
}
