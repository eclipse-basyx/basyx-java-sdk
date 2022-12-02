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
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.IAASTaggedDirectory;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedAASDescriptor;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedSubmodelDescriptor;
import org.eclipse.basyx.extensions.aas.registration.authorization.internal.IAASRegistryAuthorizer;
import org.eclipse.basyx.extensions.shared.authorization.internal.InhibitException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * Interface for the authorization points used in {@link AuthorizedTaggedDirectory}.
 *
 * @author wege
 */
public interface ITaggedDirectoryAuthorizer<SubjectInformationType> extends IAASRegistryAuthorizer<SubjectInformationType> {
	/**
	 * Checks authorization for {@link IAASTaggedDirectory#register(AASDescriptor)}.
	 *
	 * @param subjectInformation  information of the requester.
	 * @param taggedAASDescriptor the AAS descriptor.
	 * @throws InhibitException if authorization failed
	 */
	public void authorizeRegister(final SubjectInformationType subjectInformation, final TaggedAASDescriptor taggedAASDescriptor) throws InhibitException;

	/**
	 * Checks authorization for {@link IAASTaggedDirectory#lookupTag(String)}.
	 *
	 * @param subjectInformation           information of the requester.
	 * @param tag                          the tag of the AASes.
	 * @param taggedAASDescriptorsSupplier supplier for the set of AAS descriptors.
	 * @return the authorized set of AAS descriptors
	 * @throws InhibitException if authorization failed
	 */
	public Set<TaggedAASDescriptor> authorizeLookupTag(final SubjectInformationType subjectInformation, final String tag, final Supplier<Set<TaggedAASDescriptor>> taggedAASDescriptorsSupplier) throws InhibitException;

	/**
	 * Checks authorization for {@link IAASTaggedDirectory#lookupTags(Set)}.
	 *
	 * @param subjectInformation           information of the requester.
	 * @param tags                         the tags of the AASes.
	 * @param taggedAASDescriptorsSupplier supplier for the set of AAS descriptors.
	 * @return the authorized set of AAS descriptors
	 * @throws InhibitException if authorization failed
	 */
	public Set<TaggedAASDescriptor> authorizeLookupTags(final SubjectInformationType subjectInformation, final Set<String> tags, final Supplier<Set<TaggedAASDescriptor>> taggedAASDescriptorsSupplier) throws InhibitException;

	/**
	 * Checks authorization for {@link IAASTaggedDirectory#registerSubmodel(IIdentifier, TaggedSubmodelDescriptor)}.
	 *
	 * @param subjectInformation       information of the requester.
	 * @param aasId                    id of the AAS.
	 * @param taggedSubmodelDescriptor supplier for the submodel descriptor.
	 * @throws InhibitException if authorization failed
	 */
	public void authorizeRegisterSubmodel(final SubjectInformationType subjectInformation, final IIdentifier aasId, final TaggedSubmodelDescriptor taggedSubmodelDescriptor) throws InhibitException;

	/**
	 * Checks authorization for {@link IAASTaggedDirectory#lookupSubmodelTag(String)}}.
	 *
	 * @param subjectInformation                information of the requester.
	 * @param submodelTag                       tag of the submodel.
	 * @param taggedSubmodelDescriptorsSupplier supplier for the set of submodel descriptors.
	 * @return the authorized set of submodel descriptors
	 * @throws InhibitException if authorization failed
	 */
	public Set<TaggedSubmodelDescriptor> authorizeLookupSubmodelTag(final SubjectInformationType subjectInformation, final String submodelTag, final Supplier<Set<TaggedSubmodelDescriptor>> taggedSubmodelDescriptorsSupplier)
			throws InhibitException;

	/**
	 * Checks authorization for {@link IAASTaggedDirectory#lookupSubmodelTags(Set)}.
	 *
	 * @param subjectInformation                information of the requester.
	 * @param submodelTags                      the tags of the submodels.
	 * @param taggedSubmodelDescriptorsSupplier supplier for the set of submodel descriptors.
	 * @return the authorized set of submodel descriptors
	 * @throws InhibitException if authorization failed
	 */
	public Set<TaggedSubmodelDescriptor> authorizeLookupSubmodelTags(final SubjectInformationType subjectInformation, final Set<String> submodelTags, final Supplier<Set<TaggedSubmodelDescriptor>> taggedSubmodelDescriptorsSupplier)
			throws InhibitException;

	/**
	 * Checks authorization for {@link IAASTaggedDirectory#lookupBothAasAndSubmodelTags(Set, Set)}.
	 *
	 * @param subjectInformation                information of the requester.
	 * @param aasTags                           the tags of the AASes.
	 * @param submodelTags                      the tags of the submodels.
	 * @param taggedSubmodelDescriptorsSupplier supplier for the set of submodel descriptors.
	 * @return the authorized set of submodel descriptors
	 * @throws InhibitException if authorization failed
	 */
	public Set<TaggedSubmodelDescriptor> authorizeLookupBothAasAndSubmodelTags(final SubjectInformationType subjectInformation, final Set<String> aasTags, final Set<String> submodelTags,
			final Supplier<Set<TaggedSubmodelDescriptor>> taggedSubmodelDescriptorsSupplier) throws InhibitException;
}
