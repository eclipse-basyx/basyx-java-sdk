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
import org.eclipse.basyx.extensions.shared.authorization.BaSyxObjectTargetInformation;
import org.eclipse.basyx.extensions.shared.authorization.ElevatedCodeAuthentication;
import org.eclipse.basyx.extensions.shared.authorization.ElevatedCodeAuthentication.ElevatedCodeAuthenticationAreaHandler;
import org.eclipse.basyx.extensions.shared.authorization.IRbacRuleChecker;
import org.eclipse.basyx.extensions.shared.authorization.IRoleAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.IdHelper;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.SimpleRbacHelper;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;

/**
 * Simple role based implementation for {@link ISubmodelAggregatorAuthorizer}.
 *
 * @author wege
 */
public class SimpleRbacSubmodelAggregatorAuthorizer<SubjectInformationType> implements ISubmodelAggregatorAuthorizer<SubjectInformationType> {
  protected IRbacRuleChecker rbacRuleChecker;
  protected IRoleAuthenticator<SubjectInformationType> roleAuthenticator;

  public SimpleRbacSubmodelAggregatorAuthorizer(final IRbacRuleChecker rbacRuleChecker, final IRoleAuthenticator<SubjectInformationType> roleAuthenticator) {
    this.rbacRuleChecker = rbacRuleChecker;
    this.roleAuthenticator = roleAuthenticator;
  }

  @Override
  public Collection<ISubmodel> authorizeGetSubmodelList(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final Supplier<Collection<ISubmodel>> smListSupplier
  ) throws InhibitException {
    return smListSupplier.get();
  }

  @Override
  public ISubmodel authorizeGetSubmodel(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId,
      final Supplier<ISubmodel> smSupplier
  ) throws InhibitException {
    final IIdentifier aasId = getAASId(aas);

    SimpleRbacHelper
        .checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, SubmodelAggregatorScopes.READ_SCOPE, new BaSyxObjectTargetInformation(
        IdHelper.getIdentifierId(aasId), IdHelper.getIdentifierId(smId), null));

    return smSupplier.get();
  }

  @Override
  public ISubmodel authorizeGetSubmodelbyIdShort(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final String smIdShortPath,
      final Supplier<ISubmodel> smSupplier
  ) throws InhibitException {
    final IIdentifier aasId = getAASId(aas);
    final IIdentifier smId = getSmIdUnsecured(smSupplier);

    SimpleRbacHelper
        .checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, SubmodelAggregatorScopes.READ_SCOPE, new BaSyxObjectTargetInformation(
        IdHelper.getIdentifierId(aasId), IdHelper.getIdentifierId(smId), null));

    return smSupplier.get();
  }

  @Override
  public ISubmodelAPI authorizeGetSubmodelAPIById(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId,
      final Supplier<ISubmodelAPI> smAPISupplier
  ) throws InhibitException {
    final IIdentifier aasId = getAASId(aas);

    SimpleRbacHelper
        .checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, SubmodelAggregatorScopes.READ_SCOPE, new BaSyxObjectTargetInformation(
        IdHelper.getIdentifierId(aasId), IdHelper.getIdentifierId(smId), null));

    return smAPISupplier.get();
  }

  @Override
  public ISubmodelAPI authorizeGetSubmodelAPIByIdShort(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final String smIdShortPath,
      final Supplier<ISubmodelAPI> smAPISupplier
  ) throws InhibitException {
    final IIdentifier aasId = getAASId(aas);
    final IIdentifier smId = getSmIdUnsecuredByAPI(smAPISupplier);

    SimpleRbacHelper
        .checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, SubmodelAggregatorScopes.READ_SCOPE, new BaSyxObjectTargetInformation(
        IdHelper.getIdentifierId(aasId), IdHelper.getIdentifierId(smId), null));

    return smAPISupplier.get();
  }

  @Override
  public void authorizeCreateSubmodel(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId
  ) throws InhibitException {
    final IIdentifier aasId = getAASId(aas);

    SimpleRbacHelper
        .checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, SubmodelAggregatorScopes.WRITE_SCOPE, new BaSyxObjectTargetInformation(
        IdHelper.getIdentifierId(aasId), IdHelper.getIdentifierId(smId), null));
  }

  @Override
  public void authorizeUpdateSubmodel(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId
  ) throws InhibitException {
    final IIdentifier aasId = getAASId(aas);

    SimpleRbacHelper
        .checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, SubmodelAggregatorScopes.WRITE_SCOPE, new BaSyxObjectTargetInformation(
        IdHelper.getIdentifierId(aasId), IdHelper.getIdentifierId(smId), null));
  }

  @Override
  public void authorizeDeleteSubmodelByIdentifier(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId
  ) throws InhibitException {
    final IIdentifier aasId = getAASId(aas);

    SimpleRbacHelper
        .checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, SubmodelAggregatorScopes.WRITE_SCOPE, new BaSyxObjectTargetInformation(
            IdHelper.getIdentifierId(aasId), IdHelper.getIdentifierId(smId), null));
  }

  private IIdentifier getAASId(final IAssetAdministrationShell aas) {
    return aas != null ? aas.getIdentification() : null;
  }

  private IIdentifier getSmIdUnsecured(final Supplier<ISubmodel> smSupplier) {
    try (final ElevatedCodeAuthenticationAreaHandler ignored = ElevatedCodeAuthentication.enterElevatedCodeAuthenticationArea()) {
      final ISubmodel sm = smSupplier.get();

      if (sm == null) {
        return null;
      }

      return sm.getIdentification();
    }
  }

  private IIdentifier getSmIdUnsecuredByAPI(final Supplier<ISubmodelAPI> smAPISupplier) {
    try (final ElevatedCodeAuthenticationAreaHandler ignored = ElevatedCodeAuthentication.enterElevatedCodeAuthenticationArea()) {
      final ISubmodelAPI smAPI = smAPISupplier.get();

      if (smAPI == null) {
        return null;
      }

      final ISubmodel sm = smAPI.getSubmodel();

      if (sm == null) {
        return null;
      }

      return sm.getIdentification();
    }
  }
}
