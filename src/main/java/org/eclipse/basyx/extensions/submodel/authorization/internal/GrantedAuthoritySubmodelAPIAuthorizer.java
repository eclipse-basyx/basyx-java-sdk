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

import java.util.Collection;
import java.util.function.Supplier;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.extensions.shared.authorization.internal.GrantedAuthorityHelper;
import org.eclipse.basyx.extensions.shared.authorization.internal.IGrantedAuthorityAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.internal.InhibitException;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;

/**
 * Scope based implementation for {@link ISubmodelAPIAuthorizer}.
 *
 * @author wege
 */
public class GrantedAuthoritySubmodelAPIAuthorizer<SubjectInformationType> implements ISubmodelAPIAuthorizer<SubjectInformationType> {
  protected IGrantedAuthorityAuthenticator<SubjectInformationType> grantedAuthorityAuthenticator;

  public GrantedAuthoritySubmodelAPIAuthorizer(
      final IGrantedAuthorityAuthenticator<SubjectInformationType> grantedAuthorityAuthenticator) {
    this.grantedAuthorityAuthenticator = grantedAuthorityAuthenticator;
  }

  @Override
  public Collection<ISubmodelElement> authorizeGetSubmodelElements(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final Supplier<ISubmodel> smSupplier,
      final Supplier<Collection<ISubmodelElement>> smElListSupplier
  ) throws InhibitException {
    GrantedAuthorityHelper
        .checkAuthority(grantedAuthorityAuthenticator, subjectInformation, AuthorizedSubmodelAPI.READ_AUTHORITY);

    return smElListSupplier.get();
  }

  @Override
  public ISubmodelElement authorizeGetSubmodelElement(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId,
      final String smElIdShortPath,
      final Supplier<ISubmodelElement> smElSupplier
  ) throws InhibitException {
    GrantedAuthorityHelper
        .checkAuthority(grantedAuthorityAuthenticator, subjectInformation, AuthorizedSubmodelAPI.READ_AUTHORITY);

    return smElSupplier.get();
  }

  @Override
  public ISubmodel authorizeGetSubmodel(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId,
      final Supplier<ISubmodel> smSupplier
  ) throws InhibitException {
    GrantedAuthorityHelper
        .checkAuthority(grantedAuthorityAuthenticator, subjectInformation, AuthorizedSubmodelAPI.READ_AUTHORITY);

    return smSupplier.get();
  }

  @Override
  public void authorizeAddSubmodelElement(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId,
      final String smElIdShortPath
  ) throws InhibitException {
    GrantedAuthorityHelper
        .checkAuthority(grantedAuthorityAuthenticator, subjectInformation, AuthorizedSubmodelAPI.WRITE_AUTHORITY);
  }

  @Override
  public void authorizeDeleteSubmodelElement(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId,
      final String smElIdShortPath
  ) throws InhibitException {
    GrantedAuthorityHelper
        .checkAuthority(grantedAuthorityAuthenticator, subjectInformation, AuthorizedSubmodelAPI.WRITE_AUTHORITY);
  }

  @Override
  public void authorizeUpdateSubmodelElement(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId,
      final String smElIdShortPath
  ) throws InhibitException {
    GrantedAuthorityHelper
        .checkAuthority(grantedAuthorityAuthenticator, subjectInformation, AuthorizedSubmodelAPI.WRITE_AUTHORITY);
  }

  @Override
  public Object authorizeGetSubmodelElementValue(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId,
      final String smElIdShortPath,
      final Supplier<Object> valueSupplier
  ) throws InhibitException {
    GrantedAuthorityHelper
        .checkAuthority(grantedAuthorityAuthenticator, subjectInformation, AuthorizedSubmodelAPI.READ_AUTHORITY);

    return valueSupplier.get();
  }

  @Override
  public Collection<IOperation> authorizeGetOperations(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final Supplier<ISubmodel> smSupplier,
      final Supplier<Collection<IOperation>> operationListSupplier
  ) throws InhibitException {
    GrantedAuthorityHelper
        .checkAuthority(grantedAuthorityAuthenticator, subjectInformation, AuthorizedSubmodelAPI.READ_AUTHORITY);

    return operationListSupplier.get();
  }

  @Override
  public void authorizeInvokeOperation(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId,
      final String smElIdShortPath
  ) throws InhibitException {
    GrantedAuthorityHelper
        .checkAuthority(grantedAuthorityAuthenticator, subjectInformation, AuthorizedSubmodelAPI.EXECUTE_AUTHORITY);
  }

  @Override
  public Object authorizeGetOperationResult(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId,
      final String smElIdShortPath,
      final String requestId,
      final Supplier<Object> operationResultSupplier
  ) throws InhibitException {
    GrantedAuthorityHelper
        .checkAuthority(grantedAuthorityAuthenticator, subjectInformation, AuthorizedSubmodelAPI.READ_AUTHORITY);

    return operationResultSupplier.get();
  }
}
