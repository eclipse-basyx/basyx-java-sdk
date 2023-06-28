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

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.extensions.shared.authorization.internal.*;
import org.eclipse.basyx.extensions.submodel.authorization.SubmodelAPIScopes;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Simple role based implementation for {@link ISubmodelAPIAuthorizer}.
 *
 * @author wege
 */
public class SimpleRbacSubmodelAPIAuthorizer<SubjectInformationType> implements ISubmodelAPIAuthorizer<SubjectInformationType> {
	protected IRbacRuleChecker rbacRuleChecker;
	protected IRoleAuthenticator<SubjectInformationType> roleAuthenticator;

	public SimpleRbacSubmodelAPIAuthorizer(final IRbacRuleChecker rbacRuleChecker, final IRoleAuthenticator<SubjectInformationType> roleAuthenticator) {
		this.rbacRuleChecker = rbacRuleChecker;
		this.roleAuthenticator = roleAuthenticator;
	}

	@Override
	public Collection<ISubmodelElement> authorizeGetSubmodelElements(final SubjectInformationType subjectInformation, final IAssetAdministrationShell aas, final IIdentifier smId, final IReference smSemanticId, final Supplier<ISubmodel> smSupplier,
			final Supplier<Collection<ISubmodelElement>> smElListSupplier) throws InhibitException {
		final IIdentifier aasId = getAasId(aas);

		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, SubmodelAPIScopes.READ_SCOPE, new BaSyxObjectTargetInformation(IdHelper.getIdentifierId(aasId), IdHelper.getIdentifierId(smId), IdHelper.getReferenceId(smSemanticId), null));

		return smElListSupplier.get();
	}

	@Override
	public ISubmodelElement authorizeGetSubmodelElement(final SubjectInformationType subjectInformation, final IAssetAdministrationShell aas, final IIdentifier smId, final IReference smSemanticId, final String smElIdShortPath,
			final Supplier<ISubmodelElement> smElSupplier) throws InhibitException {
		final IIdentifier aasId = getAasId(aas);

		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, SubmodelAPIScopes.READ_SCOPE, new BaSyxObjectTargetInformation(IdHelper.getIdentifierId(aasId), IdHelper.getIdentifierId(smId), IdHelper.getReferenceId(smSemanticId), smElIdShortPath));

		return smElSupplier.get();
	}

	@Override
	public ISubmodel authorizeGetSubmodel(final SubjectInformationType subjectInformation, final IAssetAdministrationShell aas, final IIdentifier smId, final IReference smSemanticId, final Supplier<ISubmodel> smSupplier) throws InhibitException {
		final IIdentifier aasId = getAasId(aas);

		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, SubmodelAPIScopes.READ_SCOPE, new BaSyxObjectTargetInformation(IdHelper.getIdentifierId(aasId), IdHelper.getIdentifierId(smId), IdHelper.getReferenceId(smSemanticId), null));

		return smSupplier.get();
	}

	@Override
	public void authorizeAddSubmodelElement(final SubjectInformationType subjectInformation, final IAssetAdministrationShell aas, final IIdentifier smId, final IReference smSemanticId, final String smElIdShortPath) throws InhibitException {
		final IIdentifier aasId = getAasId(aas);

		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, SubmodelAPIScopes.WRITE_SCOPE, new BaSyxObjectTargetInformation(IdHelper.getIdentifierId(aasId), IdHelper.getIdentifierId(smId), IdHelper.getReferenceId(smSemanticId), smElIdShortPath));
	}

	@Override
	public void authorizeDeleteSubmodelElement(final SubjectInformationType subjectInformation, final IAssetAdministrationShell aas, final IIdentifier smId, final IReference smSemanticId, final String smElIdShortPath) throws InhibitException {
		final IIdentifier aasId = getAasId(aas);

		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, SubmodelAPIScopes.WRITE_SCOPE, new BaSyxObjectTargetInformation(IdHelper.getIdentifierId(aasId), IdHelper.getIdentifierId(smId), IdHelper.getReferenceId(smSemanticId), smElIdShortPath));
	}

	@Override
	public void authorizeUpdateSubmodelElement(final SubjectInformationType subjectInformation, final IAssetAdministrationShell aas, final IIdentifier smId, final IReference smSemanticId, final String smElIdShortPath) throws InhibitException {
		final IIdentifier aasId = getAasId(aas);

		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, SubmodelAPIScopes.WRITE_SCOPE, new BaSyxObjectTargetInformation(IdHelper.getIdentifierId(aasId), IdHelper.getIdentifierId(smId), IdHelper.getReferenceId(smSemanticId), smElIdShortPath));
	}

	@Override
	public Object authorizeGetSubmodelElementValue(final SubjectInformationType subjectInformation, final IAssetAdministrationShell aas, final IIdentifier smId, final IReference smSemanticId, final String smElIdShortPath, final Supplier<Object> valueSupplier)
			throws InhibitException {
		final IIdentifier aasId = getAasId(aas);

		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, SubmodelAPIScopes.READ_SCOPE, new BaSyxObjectTargetInformation(IdHelper.getIdentifierId(aasId), IdHelper.getIdentifierId(smId), IdHelper.getReferenceId(smSemanticId), smElIdShortPath));

		return valueSupplier.get();
	}

	@Override
	public Collection<IOperation> authorizeGetOperations(final SubjectInformationType subjectInformation, final IAssetAdministrationShell aas, final IIdentifier smId, final IReference smSemanticId, final Supplier<ISubmodel> smSupplier,
			final Supplier<Collection<IOperation>> operationListSupplier) throws InhibitException {
		final IIdentifier aasId = getAasId(aas);

		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, SubmodelAPIScopes.READ_SCOPE, new BaSyxObjectTargetInformation(IdHelper.getIdentifierId(aasId), IdHelper.getIdentifierId(smId), IdHelper.getReferenceId(smSemanticId), null));

		return operationListSupplier.get();
	}

	@Override
	public void authorizeInvokeOperation(final SubjectInformationType subjectInformation, final IAssetAdministrationShell aas, final IIdentifier smId, final IReference smSemanticId, final String smElIdShortPath) throws InhibitException {
		final IIdentifier aasId = getAasId(aas);

		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, SubmodelAPIScopes.EXECUTE_SCOPE, new BaSyxObjectTargetInformation(IdHelper.getIdentifierId(aasId), IdHelper.getIdentifierId(smId), IdHelper.getReferenceId(smSemanticId), smElIdShortPath));
	}

	@Override
	public Object authorizeGetOperationResult(final SubjectInformationType subjectInformation, final IAssetAdministrationShell aas, final IIdentifier smId, final IReference smSemanticId, final String smElIdShortPath, final String requestId,
			final Supplier<Object> operationResultSupplier) throws InhibitException {
		final IIdentifier aasId = getAasId(aas);

		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, SubmodelAPIScopes.READ_SCOPE, new BaSyxObjectTargetInformation(IdHelper.getIdentifierId(aasId), IdHelper.getIdentifierId(smId), IdHelper.getReferenceId(smSemanticId), smElIdShortPath));

		return operationResultSupplier.get();
	}

	private IIdentifier getAasId(final IAssetAdministrationShell aas) {
		return aas != null ? aas.getIdentification() : null;
	}
}
