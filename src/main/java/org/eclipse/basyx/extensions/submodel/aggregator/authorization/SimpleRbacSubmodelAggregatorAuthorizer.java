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
import org.eclipse.basyx.extensions.shared.authorization.BaSyxObjectTargetInformation;
import org.eclipse.basyx.extensions.shared.authorization.IRbacRuleChecker;
import org.eclipse.basyx.extensions.shared.authorization.IRoleAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.IdUtil;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.SimpleRbacUtil;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;

/**
 * Simple attribute based implementation for {@link ISubmodelAggregatorAuthorizer}.
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
  public Collection<ISubmodel> enforceGetSubmodelList(final SubjectInformationType subjectInformation, final IIdentifier aasId, final Supplier<Collection<ISubmodel>> smListSupplier) throws InhibitException {
    SimpleRbacUtil.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, SubmodelAggregatorScopes.READ_SCOPE, new BaSyxObjectTargetInformation(IdUtil.getIdentifierId(aasId), null, null));

    return smListSupplier.get();
  }

  @Override
  public ISubmodel enforceGetSubmodel(final SubjectInformationType subjectInformation, final IIdentifier aasId, final IIdentifier smId, final Supplier<ISubmodel> smSupplier) throws InhibitException {
    SimpleRbacUtil.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, SubmodelAggregatorScopes.READ_SCOPE, new BaSyxObjectTargetInformation(IdUtil.getIdentifierId(aasId), IdUtil.getIdentifierId(smId), null));

    return smSupplier.get();
  }

  @Override
  public ISubmodelAPI enforceGetSubmodelAPI(
      final SubjectInformationType subjectInformation,
      final IIdentifier aasId, final IIdentifier smId, final Supplier<ISubmodelAPI> smAPISupplier) throws InhibitException {
    SimpleRbacUtil.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, SubmodelAggregatorScopes.READ_SCOPE, new BaSyxObjectTargetInformation(IdUtil.getIdentifierId(aasId), IdUtil.getIdentifierId(smId), null));

    return smAPISupplier.get();
  }

  @Override
  public void enforceCreateSubmodel(final SubjectInformationType subjectInformation, final IIdentifier aasId, final IIdentifier smId) throws InhibitException {
    SimpleRbacUtil.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, SubmodelAggregatorScopes.WRITE_SCOPE, new BaSyxObjectTargetInformation(IdUtil.getIdentifierId(aasId), IdUtil.getIdentifierId(smId), null));
  }

  @Override
  public void enforceUpdateSubmodel(final SubjectInformationType subjectInformation, final IIdentifier aasId, final IIdentifier smId) throws InhibitException {
    SimpleRbacUtil.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, SubmodelAggregatorScopes.WRITE_SCOPE, new BaSyxObjectTargetInformation(IdUtil.getIdentifierId(aasId), IdUtil.getIdentifierId(smId), null));
  }

  @Override
  public void enforceDeleteSubmodel(final SubjectInformationType subjectInformation, final IIdentifier aasId, final IIdentifier smId) throws InhibitException {
    SimpleRbacUtil.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, SubmodelAggregatorScopes.WRITE_SCOPE, new BaSyxObjectTargetInformation(IdUtil.getIdentifierId(aasId), IdUtil.getIdentifierId(smId), null));
  }
}
