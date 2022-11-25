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
import java.util.function.Supplier;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.extensions.shared.authorization.GrantedAuthorityHelper;
import org.eclipse.basyx.extensions.shared.authorization.IGrantedAuthorityAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;

/**
 * Scope based implementation for {@link ISubmodelAggregatorAuthorizer}.
 *
 * @author wege
 */
public class GrantedAuthoritySubmodelAggregatorAuthorizer<SubjectInformationType> implements ISubmodelAggregatorAuthorizer<SubjectInformationType> {
  protected IGrantedAuthorityAuthenticator<SubjectInformationType> grantedAuthorityAuthenticator;

  public GrantedAuthoritySubmodelAggregatorAuthorizer(final IGrantedAuthorityAuthenticator<SubjectInformationType> grantedAuthorityAuthenticator) {
    this.grantedAuthorityAuthenticator = grantedAuthorityAuthenticator;
  }

  @Override
  public Collection<ISubmodel> authorizeGetSubmodelList(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final Supplier<Collection<ISubmodel>> smListSupplier
  ) throws InhibitException {
    GrantedAuthorityHelper.checkAuthority(grantedAuthorityAuthenticator, subjectInformation, AuthorizedSubmodelAggregator.READ_AUTHORITY);

    return smListSupplier.get();
  }

  @Override
  public ISubmodel authorizeGetSubmodel(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId,
      final Supplier<ISubmodel> smSupplier
  ) throws InhibitException {
    GrantedAuthorityHelper.checkAuthority(grantedAuthorityAuthenticator, subjectInformation, AuthorizedSubmodelAggregator.READ_AUTHORITY);

    return smSupplier.get();
  }

  @Override
  public ISubmodel authorizeGetSubmodelbyIdShort(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final String smIdShortPath,
      final Supplier<ISubmodel> smSupplier
  ) throws InhibitException {
    GrantedAuthorityHelper.checkAuthority(grantedAuthorityAuthenticator, subjectInformation, AuthorizedSubmodelAggregator.READ_AUTHORITY);

    return smSupplier.get();
  }

  @Override
  public ISubmodelAPI authorizeGetSubmodelAPIById(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId,
      final Supplier<ISubmodelAPI> smAPISupplier
  ) throws InhibitException {
    GrantedAuthorityHelper.checkAuthority(grantedAuthorityAuthenticator, subjectInformation, AuthorizedSubmodelAggregator.READ_AUTHORITY);

    return smAPISupplier.get();
  }

  @Override
  public ISubmodelAPI authorizeGetSubmodelAPIByIdShort(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final String smIdShortPath,
      final Supplier<ISubmodelAPI> smAPISupplier
  ) throws InhibitException {
    GrantedAuthorityHelper.checkAuthority(grantedAuthorityAuthenticator, subjectInformation, AuthorizedSubmodelAggregator.READ_AUTHORITY);

    return smAPISupplier.get();
  }

  @Override
  public void authorizeCreateSubmodel(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId
  ) throws InhibitException {
    GrantedAuthorityHelper.checkAuthority(grantedAuthorityAuthenticator, subjectInformation, AuthorizedSubmodelAggregator.WRITE_AUTHORITY);
  }

  @Override
  public void authorizeUpdateSubmodel(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId
  ) throws InhibitException {
    GrantedAuthorityHelper.checkAuthority(grantedAuthorityAuthenticator, subjectInformation, AuthorizedSubmodelAggregator.WRITE_AUTHORITY);
  }

  @Override
  public void authorizeDeleteSubmodelByIdentifier(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId
  ) throws InhibitException {
    GrantedAuthorityHelper.checkAuthority(grantedAuthorityAuthenticator, subjectInformation, AuthorizedSubmodelAggregator.WRITE_AUTHORITY);
  }
}
