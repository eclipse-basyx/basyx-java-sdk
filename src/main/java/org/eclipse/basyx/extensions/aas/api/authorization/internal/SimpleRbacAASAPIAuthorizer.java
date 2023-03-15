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
package org.eclipse.basyx.extensions.aas.api.authorization.internal;

import java.util.function.Supplier;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.extensions.aas.api.authorization.AASAPIScopes;
import org.eclipse.basyx.extensions.shared.authorization.internal.BaSyxObjectTargetInformation;
import org.eclipse.basyx.extensions.shared.authorization.internal.ElevatedCodeAuthentication;
import org.eclipse.basyx.extensions.shared.authorization.internal.IRbacRuleChecker;
import org.eclipse.basyx.extensions.shared.authorization.internal.IRoleAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.internal.IdHelper;
import org.eclipse.basyx.extensions.shared.authorization.internal.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.internal.SimpleRbacHelper;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;

/**
 * Simple role based implementation for {@link IAASAPIAuthorizer}.
 *
 * @author wege
 */
public class SimpleRbacAASAPIAuthorizer<SubjectInformationType> implements IAASAPIAuthorizer<SubjectInformationType> {
	protected IRbacRuleChecker rbacRuleChecker;
	protected IRoleAuthenticator<SubjectInformationType> roleAuthenticator;

	public SimpleRbacAASAPIAuthorizer(final IRbacRuleChecker rbacRuleChecker, final IRoleAuthenticator<SubjectInformationType> roleAuthenticator) {
		this.rbacRuleChecker = rbacRuleChecker;
		this.roleAuthenticator = roleAuthenticator;
	}

	@Override
	public IAssetAdministrationShell authorizeGetAAS(final SubjectInformationType subjectInformation, final Supplier<IAssetAdministrationShell> aasSupplier) throws InhibitException {
		final IIdentifier aasId = getAasId(aasSupplier);

		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, AASAPIScopes.READ_SCOPE, new BaSyxObjectTargetInformation(IdHelper.getIdentifierId(aasId), null, null, null));

		return aasSupplier.get();
	}

	@Override
	public void authorizeAddSubmodel(final SubjectInformationType subjectInformation, final Supplier<IAssetAdministrationShell> aasSupplier, final IReference smId) throws InhibitException {
		final IIdentifier aasId = getAasId(aasSupplier);

		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, AASAPIScopes.WRITE_SCOPE, new BaSyxObjectTargetInformation(IdHelper.getIdentifierId(aasId), IdHelper.getReferenceId(smId), null, null));
	}

	@Override
	public void authorizeRemoveSubmodel(final SubjectInformationType subjectInformation, final Supplier<IAssetAdministrationShell> aasSupplier, final String smIdShortPath) throws InhibitException {
		final IIdentifier aasId = getAasId(aasSupplier);

		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, AASAPIScopes.WRITE_SCOPE, new BaSyxObjectTargetInformation(IdHelper.getIdentifierId(aasId), smIdShortPath, null, null));
	}

	private IIdentifier getAasId(final Supplier<IAssetAdministrationShell> aasSupplier) {
		try (final var ignored = ElevatedCodeAuthentication.enterElevatedCodeAuthenticationArea()) {
			final IAssetAdministrationShell aas = aasSupplier.get();

			if (aas == null) {
				return null;
			}

			return aas.getIdentification();
		}
	}
}
