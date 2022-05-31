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
package org.eclipse.basyx.extensions.aas.api.authorization;

import java.util.function.Supplier;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.extensions.shared.authorization.IGrantedAuthorityAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.springframework.security.core.GrantedAuthority;

/**
 * Scope based implementation for {@link IAASAPIAuthorizer}.
 *
 * @author wege
 */
public class GrantedAuthorityAASAPIAuthorizer<SubjectInformationType> implements IAASAPIAuthorizer<SubjectInformationType> {
  protected IGrantedAuthorityAuthenticator<SubjectInformationType> grantedAuthorityAuthenticator;

  public GrantedAuthorityAASAPIAuthorizer(final IGrantedAuthorityAuthenticator<SubjectInformationType> grantedAuthorityAuthenticator) {
    this.grantedAuthorityAuthenticator = grantedAuthorityAuthenticator;
  }

  @Override
  public IAssetAdministrationShell enforceGetAAS(final SubjectInformationType subjectInformation, final IIdentifier aasId, final Supplier<IAssetAdministrationShell> aasSupplier) throws InhibitException {
    if (grantedAuthorityAuthenticator.getAuthorities(subjectInformation).stream()
        .map(GrantedAuthority::getAuthority)
        .noneMatch(authority -> authority.equals(AuthorizedAASAPI.READ_AUTHORITY))) {
      throw new InhibitException();
    }
    return aasSupplier.get();
  }

  @Override
  public void enforceAddSubmodel(final SubjectInformationType subjectInformation, final IIdentifier aasId, final IReference smId) throws InhibitException {
    if (grantedAuthorityAuthenticator.getAuthorities(subjectInformation).stream()
        .map(GrantedAuthority::getAuthority)
        .noneMatch(authority -> authority.equals(AuthorizedAASAPI.WRITE_AUTHORITY))) {
      throw new InhibitException();
    }
  }

  @Override
  public void enforceRemoveSubmodel(final SubjectInformationType subjectInformation, final IIdentifier aasId, final String smIdShortPath) throws InhibitException {
    if (grantedAuthorityAuthenticator.getAuthorities(subjectInformation).stream()
        .map(GrantedAuthority::getAuthority)
        .noneMatch(authority -> authority.equals(AuthorizedAASAPI.WRITE_AUTHORITY))) {
      throw new InhibitException();
    }
  }
}
