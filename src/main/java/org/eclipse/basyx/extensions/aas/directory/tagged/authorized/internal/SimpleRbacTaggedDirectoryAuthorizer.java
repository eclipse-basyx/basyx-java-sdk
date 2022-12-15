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
package org.eclipse.basyx.extensions.aas.directory.tagged.authorized.internal;

import java.util.Set;
import java.util.function.Supplier;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedAASDescriptor;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedSubmodelDescriptor;
import org.eclipse.basyx.extensions.aas.registration.authorization.AASRegistryScopes;
import org.eclipse.basyx.extensions.aas.registration.authorization.internal.SimpleRbacAASRegistryAuthorizer;
import org.eclipse.basyx.extensions.shared.authorization.internal.BaSyxObjectTargetInformation;
import org.eclipse.basyx.extensions.shared.authorization.internal.IRbacRuleChecker;
import org.eclipse.basyx.extensions.shared.authorization.internal.IRoleAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.internal.IdHelper;
import org.eclipse.basyx.extensions.shared.authorization.internal.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.internal.SimpleRbacHelper;
import org.eclipse.basyx.extensions.shared.authorization.internal.TagTargetInformation;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * Simple role based implementation for {@link ITaggedDirectoryAuthorizer}.
 *
 * @author wege
 */
public class SimpleRbacTaggedDirectoryAuthorizer<SubjectInformationType> extends SimpleRbacAASRegistryAuthorizer<SubjectInformationType> implements ITaggedDirectoryAuthorizer<SubjectInformationType> {
	public SimpleRbacTaggedDirectoryAuthorizer(final IRbacRuleChecker rbacRuleChecker, final IRoleAuthenticator<SubjectInformationType> roleAuthenticator) {
		super(rbacRuleChecker, roleAuthenticator);
	}

	@Override
	public void authorizeRegister(final SubjectInformationType subjectInformation, final TaggedAASDescriptor taggedAASDescriptor) throws InhibitException {
		final IIdentifier aasId = taggedAASDescriptor.getIdentifier();

		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, AASRegistryScopes.WRITE_SCOPE, new BaSyxObjectTargetInformation(IdHelper.getIdentifierId(aasId), null, null));
	}

	@Override
	public Set<TaggedAASDescriptor> authorizeLookupTag(final SubjectInformationType subjectInformation, final String tag, final Supplier<Set<TaggedAASDescriptor>> taggedAASDescriptorsSupplier) throws InhibitException {
		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, AASRegistryScopes.READ_SCOPE, new TagTargetInformation(tag));

		return taggedAASDescriptorsSupplier.get();
	}

	@Override
	public Set<TaggedAASDescriptor> authorizeLookupTags(final SubjectInformationType subjectInformation, final Set<String> tags, final Supplier<Set<TaggedAASDescriptor>> taggedAASDescriptorsSupplier) throws InhibitException {
		return taggedAASDescriptorsSupplier.get();
	}

	@Override
	public void authorizeRegisterSubmodel(final SubjectInformationType subjectInformation, final IIdentifier aasId, final TaggedSubmodelDescriptor taggedSubmodelDescriptor) throws InhibitException {
		final IIdentifier smId = taggedSubmodelDescriptor.getIdentifier();

		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, AASRegistryScopes.WRITE_SCOPE, new BaSyxObjectTargetInformation(IdHelper.getIdentifierId(aasId), IdHelper.getIdentifierId(smId), null));
	}

	@Override
	public Set<TaggedSubmodelDescriptor> authorizeLookupSubmodelTag(final SubjectInformationType subjectInformation, final String submodelTag, final Supplier<Set<TaggedSubmodelDescriptor>> taggedSubmodelDescriptorsSupplier)
			throws InhibitException {
		SimpleRbacHelper.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, AASRegistryScopes.READ_SCOPE, new TagTargetInformation(submodelTag));

		return taggedSubmodelDescriptorsSupplier.get();
	}

	@Override
	public Set<TaggedSubmodelDescriptor> authorizeLookupSubmodelTags(final SubjectInformationType subjectInformation, final Set<String> submodelTags, final Supplier<Set<TaggedSubmodelDescriptor>> taggedSubmodelDescriptorsSupplier)
			throws InhibitException {
		return taggedSubmodelDescriptorsSupplier.get();
	}

	@Override
	public Set<TaggedSubmodelDescriptor> authorizeLookupBothAasAndSubmodelTags(final SubjectInformationType subjectInformation, final Set<String> aasTags, final Set<String> submodelTags,
			final Supplier<Set<TaggedSubmodelDescriptor>> taggedSubmodelDescriptorsSupplier) throws InhibitException {
		return taggedSubmodelDescriptorsSupplier.get();
	}
}
