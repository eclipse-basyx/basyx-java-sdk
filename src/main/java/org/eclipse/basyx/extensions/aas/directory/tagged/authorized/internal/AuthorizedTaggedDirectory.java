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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.IAASTaggedDirectory;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedAASDescriptor;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedSubmodelDescriptor;
import org.eclipse.basyx.extensions.aas.registration.authorization.internal.AuthorizedAASRegistry;
import org.eclipse.basyx.extensions.shared.authorization.internal.AuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.internal.AuthenticationGrantedAuthorityAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.internal.ElevatedCodeAuthentication;
import org.eclipse.basyx.extensions.shared.authorization.internal.ISubjectInformationProvider;
import org.eclipse.basyx.extensions.shared.authorization.internal.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.internal.NotAuthorized;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link IAASTaggedDirectory} for restricting access to sensitive data
 *
 * @author fried, wege
 */
public class AuthorizedTaggedDirectory<SubjectInformationType> extends AuthorizedAASRegistry<SubjectInformationType> implements IAASTaggedDirectory {
	private static final Logger logger = LoggerFactory.getLogger(AuthorizedTaggedDirectory.class);

	private IAASTaggedDirectory decoratedTaggedDirectory;

	protected final ITaggedDirectoryAuthorizer<SubjectInformationType> taggedDirectoryAuthorizer;
	protected final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider;

	/**
	 * Provides registry implementation that authorizes invocations before forwarding them to the provided registry implementation.
	 */
	public AuthorizedTaggedDirectory(final IAASTaggedDirectory decoratedTaggedDirectory, final ITaggedDirectoryAuthorizer<SubjectInformationType> taggedDirectoryAuthorizer,
			final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider) {
		super(decoratedTaggedDirectory, taggedDirectoryAuthorizer, subjectInformationProvider);
		this.decoratedTaggedDirectory = decoratedTaggedDirectory;
		this.taggedDirectoryAuthorizer = taggedDirectoryAuthorizer;
		this.subjectInformationProvider = subjectInformationProvider;
	}

	/**
	 * @deprecated Please use {@link AuthorizedTaggedDirectory#AuthorizedTaggedDirectory(IAASTaggedDirectory, ITaggedDirectoryAuthorizer, ISubjectInformationProvider)} instead for more explicit parametrization.
	 */
	@Deprecated @SuppressWarnings("unchecked") public AuthorizedTaggedDirectory(final IAASTaggedDirectory decoratedTaggedDirectory) {
		this(decoratedTaggedDirectory, (ITaggedDirectoryAuthorizer<SubjectInformationType>) new GrantedAuthorityTaggedDirectoryAuthorizer<>(new AuthenticationGrantedAuthorityAuthenticator()),
				(ISubjectInformationProvider<SubjectInformationType>) new AuthenticationContextProvider());
	}

	@Override public void register(final TaggedAASDescriptor descriptor) {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			decoratedRegistry.register(descriptor);
			return;
		}

		try {
			authorizeRegister(descriptor);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}

		try {
			super.authorizeRegister(descriptor);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}

		decoratedTaggedDirectory.register(descriptor);
	}

	protected void authorizeRegister(final TaggedAASDescriptor descriptor) throws InhibitException {
		taggedDirectoryAuthorizer.authorizeRegister(subjectInformationProvider.get(), descriptor);
	}

	@Override public Set<TaggedAASDescriptor> lookupTag(final String tag) {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedTaggedDirectory.lookupTag(tag);
		}

		try {
			final Set<TaggedAASDescriptor> taggedAASDescriptors = authorizeLookupTag(tag);

			return taggedAASDescriptors.stream().map(taggedAASDescriptor -> {
				try {
					return super.authorizeLookupAAS(taggedAASDescriptor.getIdentifier());
				} catch (final InhibitException e) {
					// remove aas descriptor if authorization was unsuccessful
					logger.info(e.getMessage(), e);
				}
				return null;
			}).filter(Objects::nonNull).collect(Collectors.toSet()).stream().filter(TaggedAASDescriptor.class::isInstance).map(TaggedAASDescriptor.class::cast).collect(Collectors.toSet());
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected Set<TaggedAASDescriptor> authorizeLookupTag(final String tag) throws InhibitException {
		final Set<TaggedAASDescriptor> authorizedAASDescriptorsAfterLookupTag = taggedDirectoryAuthorizer.authorizeLookupTag(subjectInformationProvider.get(), tag, () -> decoratedTaggedDirectory.lookupTag(tag));

		final Set<TaggedAASDescriptor> authorizedAASDescriptorsAfterLookupAAS = authorizedAASDescriptorsAfterLookupTag.stream().map(aasDescriptor -> {
			try {
				return authorizeLookupAAS(aasDescriptor.getIdentifier());
			} catch (final InhibitException e) {
				// remove aas descriptor if authorization was unsuccessful
				logger.info(e.getMessage(), e);
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList()).stream().filter(TaggedAASDescriptor.class::isInstance).map(TaggedAASDescriptor.class::cast).collect(Collectors.toSet());

		return authorizedAASDescriptorsAfterLookupAAS.stream().map(aasDescriptor -> {
			final List<SubmodelDescriptor> submodelDescriptorsToRemove = aasDescriptor.getSubmodelDescriptors().stream().map(submodelDescriptor -> {
				final IIdentifier smId = submodelDescriptor.getIdentifier();
				try {
					return authorizeLookupSubmodel(aasDescriptor.getIdentifier(), smId);
				} catch (final InhibitException e) {
					// remove submodel descriptor if authorization was unsuccessful
					logger.info(e.getMessage(), e);
				}
				return null;
			}).filter(Objects::nonNull).collect(Collectors.toList());

			submodelDescriptorsToRemove.forEach(submodelDescriptor -> aasDescriptor.removeSubmodelDescriptor(submodelDescriptor.getIdentifier()));

			return aasDescriptor;
		}).collect(Collectors.toSet());
	}

	@Override public Set<TaggedAASDescriptor> lookupTags(final Set<String> tags) {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedTaggedDirectory.lookupTags(tags);
		}

		try {
			return authorizeLookupTags(tags);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	@Override public void registerSubmodel(final IIdentifier aasId, final TaggedSubmodelDescriptor smDescriptor) {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			decoratedRegistry.register(aasId, smDescriptor);
			return;
		}

		try {
			authorizeRegisterSubmodel(aasId, smDescriptor);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}

		try {
			super.authorizeRegister(aasId, smDescriptor);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}

		decoratedTaggedDirectory.registerSubmodel(aasId, smDescriptor);
	}

	protected void authorizeRegisterSubmodel(final IIdentifier aasId, final TaggedSubmodelDescriptor smDescriptor) throws InhibitException {
		taggedDirectoryAuthorizer.authorizeRegisterSubmodel(subjectInformationProvider.get(), aasId, smDescriptor);
	}

	@Override public Set<TaggedSubmodelDescriptor> lookupSubmodelTag(final String submodelTag) {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedTaggedDirectory.lookupSubmodelTag(submodelTag);
		}

		try {
			return authorizeLookupSubmodelTag(submodelTag);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected Set<TaggedSubmodelDescriptor> authorizeLookupSubmodelTag(final String submodelTag) throws InhibitException {
		final Set<TaggedSubmodelDescriptor> authorizedSubmodelDescriptorsAfterLookupTag = taggedDirectoryAuthorizer
				.authorizeLookupSubmodelTag(subjectInformationProvider.get(), submodelTag, () -> decoratedTaggedDirectory.lookupSubmodelTag(submodelTag));

		return authorizedSubmodelDescriptorsAfterLookupTag.stream().map(smDescriptor -> {
			try {
				// TODO: decide if to do this, what rules must user specify for access?
				return authorizeLookupSubmodel(new ModelUrn("*"), smDescriptor.getIdentifier());
			} catch (final InhibitException e) {
				// remove submodel descriptor if authorization was unsuccessful
				logger.info(e.getMessage(), e);
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList()).stream().filter(TaggedSubmodelDescriptor.class::isInstance).map(TaggedSubmodelDescriptor.class::cast).collect(Collectors.toSet());
	}

	@Override public Set<TaggedSubmodelDescriptor> lookupSubmodelTags(final Set<String> submodelTags) {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedTaggedDirectory.lookupSubmodelTags(submodelTags);
		}

		try {
			return authorizeLookupSubmodelTags(submodelTags);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected Set<TaggedSubmodelDescriptor> authorizeLookupSubmodelTags(final Set<String> submodelTags) throws InhibitException {
		final Set<TaggedSubmodelDescriptor> authorizedTaggedSmDescriptorsByCollection = authorizeLookupSubmodelTagListOnly(submodelTags);
		final Set<TaggedSubmodelDescriptor> authorizedTaggedSmDescriptorsByIndividual = submodelTags.stream().map(tag -> {
			try {
				return authorizeLookupSubmodelTag(tag);
			} catch (final InhibitException e) {
				// remove submodel descriptor if authorization was unsuccessful
				logger.info(e.getMessage(), e);
			}
			return null;
		}).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toSet());

		final Set<TaggedSubmodelDescriptor> authorizedTaggedAASDescriptors = new HashSet<>(authorizedTaggedSmDescriptorsByCollection);
		authorizedTaggedAASDescriptors.retainAll(authorizedTaggedSmDescriptorsByIndividual);

		return authorizedTaggedAASDescriptors;
	}

	private Set<TaggedSubmodelDescriptor> authorizeLookupSubmodelTagListOnly(final Set<String> submodelTags) throws InhibitException {
		return taggedDirectoryAuthorizer.authorizeLookupSubmodelTags(subjectInformationProvider.get(), submodelTags, () -> decoratedTaggedDirectory.lookupSubmodelTags(submodelTags));
	}

	@Override public Set<TaggedSubmodelDescriptor> lookupBothAasAndSubmodelTags(final Set<String> aasTags, final Set<String> submodelTags) {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedTaggedDirectory.lookupSubmodelTags(submodelTags);
		}

		try {
			return authorizeLookupBothAasAndSubmodelTags(aasTags, submodelTags);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected Set<TaggedSubmodelDescriptor> authorizeLookupBothAasAndSubmodelTags(final Set<String> aasTags, final Set<String> submodelTags) throws InhibitException {
		final Set<TaggedSubmodelDescriptor> authorizedTaggedSmDescriptorsByBothAasAndSubmodelTags = authorizeLookupBothAasAndSubmodelTagListOnly(aasTags, submodelTags);
		final Set<TaggedSubmodelDescriptor> authorizedTaggedSmDescriptorsBySubmodelTags = authorizeLookupSubmodelTags(submodelTags);

		final Set<TaggedSubmodelDescriptor> authorizedTaggedDescriptors = new HashSet<>(authorizedTaggedSmDescriptorsByBothAasAndSubmodelTags);
		authorizedTaggedDescriptors.retainAll(authorizedTaggedSmDescriptorsBySubmodelTags);

		return authorizedTaggedDescriptors;
	}

	private Set<TaggedSubmodelDescriptor> authorizeLookupBothAasAndSubmodelTagListOnly(final Set<String> aasTags, final Set<String> submodelTags) throws InhibitException {
		return taggedDirectoryAuthorizer.authorizeLookupBothAasAndSubmodelTags(subjectInformationProvider.get(), aasTags, submodelTags, () -> decoratedTaggedDirectory.lookupBothAasAndSubmodelTags(aasTags, submodelTags));
	}

	protected Set<TaggedAASDescriptor> authorizeLookupTags(final Set<String> tags) throws InhibitException {
		final Set<TaggedAASDescriptor> authorizedTaggedAASDescriptorsByCollection = authorizeLookupTagListOnly(tags);
		final Set<TaggedAASDescriptor> authorizedTaggedAASDescriptorsByIndividual = tags.stream().map(tag -> {
			try {
				return authorizeLookupTag(tag);
			} catch (final InhibitException e) {
				// remove aas descriptor if authorization was unsuccessful
				logger.info(e.getMessage(), e);
			}
			return null;
		}).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toSet());

		final Set<TaggedAASDescriptor> authorizedTaggedAASDescriptors = new HashSet<>(authorizedTaggedAASDescriptorsByCollection);
		authorizedTaggedAASDescriptors.retainAll(authorizedTaggedAASDescriptorsByIndividual);

		return authorizedTaggedAASDescriptors;
	}

	private Set<TaggedAASDescriptor> authorizeLookupTagListOnly(final Set<String> tags) throws InhibitException {
		return taggedDirectoryAuthorizer.authorizeLookupTags(subjectInformationProvider.get(), tags, () -> decoratedTaggedDirectory.lookupTags(tags));
	}
}
