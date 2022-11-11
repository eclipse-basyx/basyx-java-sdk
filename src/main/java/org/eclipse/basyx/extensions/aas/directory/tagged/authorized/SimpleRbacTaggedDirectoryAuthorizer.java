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
package org.eclipse.basyx.extensions.aas.directory.tagged.authorized;

import java.util.Set;
import java.util.function.Supplier;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedAASDescriptor;
import org.eclipse.basyx.extensions.aas.registration.authorization.AASRegistryScopes;
import org.eclipse.basyx.extensions.aas.registration.authorization.SimpleRbacAASRegistryAuthorizer;
import org.eclipse.basyx.extensions.shared.authorization.BaSyxObjectTargetInformation;
import org.eclipse.basyx.extensions.shared.authorization.EmptyTargetInformation;
import org.eclipse.basyx.extensions.shared.authorization.IRbacRuleChecker;
import org.eclipse.basyx.extensions.shared.authorization.IRoleAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.IdUtil;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.SimpleRbacUtil;
import org.eclipse.basyx.extensions.shared.authorization.TagTargetInformation;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * Simple attribute based implementation for {@link ITaggedDirectoryAuthorizer}.
 *
 * @author wege
 */
public class SimpleRbacTaggedDirectoryAuthorizer<SubjectInformationType> extends SimpleRbacAASRegistryAuthorizer<SubjectInformationType> implements ITaggedDirectoryAuthorizer<SubjectInformationType> {
  public SimpleRbacTaggedDirectoryAuthorizer(final IRbacRuleChecker rbacRuleChecker, final IRoleAuthenticator<SubjectInformationType> roleAuthenticator) {
    super(rbacRuleChecker, roleAuthenticator);
  }

  @Override
  public void enforceRegister(final SubjectInformationType subjectInformation, final IIdentifier aasId) throws InhibitException {
    SimpleRbacUtil.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, AASRegistryScopes.WRITE_SCOPE, new BaSyxObjectTargetInformation(IdUtil.getIdentifierId(aasId), null, null));
  }

  @Override
  public Set<TaggedAASDescriptor> enforceLookupTag(final SubjectInformationType subjectInformation, final String tag, final Supplier<Set<TaggedAASDescriptor>> taggedAASDescriptorsSupplier) throws InhibitException {
    SimpleRbacUtil.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, AASRegistryScopes.READ_SCOPE, new TagTargetInformation(tag));

    return taggedAASDescriptorsSupplier.get();
  }

  @Override
  public Set<TaggedAASDescriptor> enforceLookupTags(final SubjectInformationType subjectInformation, final Supplier<Set<TaggedAASDescriptor>> taggedAASDescriptorsSupplier) throws InhibitException {
    SimpleRbacUtil.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, AASRegistryScopes.READ_SCOPE, new EmptyTargetInformation());

    return taggedAASDescriptorsSupplier.get();
  }
}
