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
package org.eclipse.basyx.extensions.aas.registration.authorization.internal;

import java.util.List;
import java.util.function.Supplier;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.extensions.aas.registration.authorization.AASRegistryScopes;
import org.eclipse.basyx.extensions.shared.authorization.internal.BaSyxObjectTargetInformation;
import org.eclipse.basyx.extensions.shared.authorization.internal.IRbacRuleChecker;
import org.eclipse.basyx.extensions.shared.authorization.internal.IRoleAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.internal.IdHelper;
import org.eclipse.basyx.extensions.shared.authorization.internal.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.internal.SimpleRbacHelper;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * Simple role based implementation for {@link IAASRegistryAuthorizer}.
 *
 * @author wege
 */
public class SimpleRbacAASRegistryAuthorizer<SubjectInformationType> implements IAASRegistryAuthorizer<SubjectInformationType> {
	protected IRbacRuleChecker rbacRuleChecker;
	protected IRoleAuthenticator<SubjectInformationType> roleAuthenticator;

	public SimpleRbacAASRegistryAuthorizer(final IRbacRuleChecker rbacRuleChecker, final IRoleAuthenticator<SubjectInformationType> roleAuthenticator) {
		this.rbacRuleChecker = rbacRuleChecker;
		this.roleAuthenticator = roleAuthenticator;
	}

	@Override
	public void authorizeRegisterAas(final SubjectInformationType subjectInformation, final AASDescriptor aasDescriptor) throws InhibitException {
		final IIdentifier aasId = aasDescriptor.getIdentifier();

		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, AASRegistryScopes.WRITE_SCOPE, new BaSyxObjectTargetInformation(IdHelper.getIdentifierId(aasId), null, null));
	}

	@Override
	public void authorizeRegisterSubmodel(final SubjectInformationType subjectInformation, final IIdentifier aasId, final SubmodelDescriptor smDescriptor) throws InhibitException {
		final IIdentifier smId = smDescriptor.getIdentifier();

		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, AASRegistryScopes.WRITE_SCOPE, new BaSyxObjectTargetInformation(IdHelper.getIdentifierId(aasId), IdHelper.getIdentifierId(smId), null));
	}

	@Override
	public void authorizeUnregisterAas(final SubjectInformationType subjectInformation, final IIdentifier aasId) throws InhibitException {
		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, AASRegistryScopes.WRITE_SCOPE, new BaSyxObjectTargetInformation(IdHelper.getIdentifierId(aasId), null, null));
	}

	@Override
	public void authorizeUnregisterSubmodel(final SubjectInformationType subjectInformation, final IIdentifier aasId, final IIdentifier smId) throws InhibitException {
		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, AASRegistryScopes.WRITE_SCOPE, new BaSyxObjectTargetInformation(IdHelper.getIdentifierId(aasId), IdHelper.getIdentifierId(smId), null));
	}

	@Override
	public AASDescriptor authorizeLookupAAS(final SubjectInformationType subjectInformation, final IIdentifier aasId, final Supplier<AASDescriptor> aasSupplier) throws InhibitException {
		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, AASRegistryScopes.READ_SCOPE, new BaSyxObjectTargetInformation(IdHelper.getIdentifierId(aasId), null, null));

		return aasSupplier.get();
	}

	@Override
	public List<AASDescriptor> authorizeLookupAll(final SubjectInformationType subjectInformation, final Supplier<List<AASDescriptor>> aasDescriptorsSupplier) throws InhibitException {
		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, AASRegistryScopes.READ_SCOPE, new BaSyxObjectTargetInformation(null, null, null));

		return aasDescriptorsSupplier.get();
	}

	@Override
	public List<SubmodelDescriptor> authorizeLookupSubmodels(final SubjectInformationType subjectInformation, final IIdentifier aasId, final Supplier<List<SubmodelDescriptor>> submodelDescriptorsSupplier) throws InhibitException {
		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, AASRegistryScopes.READ_SCOPE, new BaSyxObjectTargetInformation(IdHelper.getIdentifierId(aasId), null, null));

		return submodelDescriptorsSupplier.get();
	}

	@Override
	public SubmodelDescriptor authorizeLookupSubmodel(final SubjectInformationType subjectInformation, final IIdentifier aasId, final IIdentifier smId, final Supplier<SubmodelDescriptor> smSupplier) throws InhibitException {
		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, AASRegistryScopes.READ_SCOPE, new BaSyxObjectTargetInformation(IdHelper.getIdentifierId(aasId), IdHelper.getIdentifierId(smId), null));

		return smSupplier.get();
	}
}
