/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.directory.tagged.authorized;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.IAASTaggedDirectory;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedAASDescriptor;
import org.eclipse.basyx.extensions.aas.registration.authorization.AuthorizedAASRegistry;
import org.eclipse.basyx.extensions.aas.registration.authorization.GrantedAuthorityAASRegistryAuthorizer;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationGrantedAuthorityAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.CodeAuthentication;
import org.eclipse.basyx.extensions.shared.authorization.ISubjectInformationProvider;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.NotAuthorized;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Implementation of {@link IAASTaggedDirectory} for restricting access to
 * sensitive data
 *
 * @author fried, wege
 *
 */
public class AuthorizedTaggedDirectory<SubjectInformationType> extends AuthorizedAASRegistry<SubjectInformationType> implements IAASTaggedDirectory {
	private static final Logger logger = LoggerFactory.getLogger(AuthorizedAASRegistry.class);

	private IAASTaggedDirectory decoratedTaggedDirectory;

	protected final ITaggedDirectoryAuthorizer<SubjectInformationType> taggedDirectoryAuthorizer;
	protected final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider;

	/**
	 * Provides registry implementation that authorizes invocations before
	 * forwarding them to the provided registry implementation.
	 */
	public AuthorizedTaggedDirectory(
			final IAASTaggedDirectory decoratedTaggedDirectory,
			final ITaggedDirectoryAuthorizer<SubjectInformationType> taggedDirectoryAuthorizer,
			final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider
	) {
		super(decoratedTaggedDirectory, taggedDirectoryAuthorizer, subjectInformationProvider);
		this.decoratedTaggedDirectory = decoratedTaggedDirectory;
		this.taggedDirectoryAuthorizer = taggedDirectoryAuthorizer;
		this.subjectInformationProvider = subjectInformationProvider;
	}

	/**
	 * @deprecated
	 */
	public AuthorizedTaggedDirectory(final IAASTaggedDirectory decoratedTaggedDirectory) {
		this(
				decoratedTaggedDirectory,
				(ITaggedDirectoryAuthorizer<SubjectInformationType>) new GrantedAuthorityAASRegistryAuthorizer<>(new AuthenticationGrantedAuthorityAuthenticator()),
				(ISubjectInformationProvider<SubjectInformationType>) new AuthenticationContextProvider()
		);
	}

	@Override
	public void register(final TaggedAASDescriptor descriptor) {
		if (CodeAuthentication.isCodeAuthentication()) {
			decoratedRegistry.register(descriptor);
			return;
		}

		try {
			enforceRegister(descriptor);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}

		try {
			super.enforceRegister(descriptor);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}

		decoratedTaggedDirectory.register(descriptor);
	}

	protected void enforceRegister(final TaggedAASDescriptor descriptor) throws InhibitException {
		final IIdentifier aasId = descriptor.getIdentifier();

		taggedDirectoryAuthorizer.enforceRegister(
				subjectInformationProvider.get(),
				aasId
		);
	}

	@Override
	public Set<TaggedAASDescriptor> lookupTag(final String tag) {
		if (CodeAuthentication.isCodeAuthentication()) {
			return decoratedTaggedDirectory.lookupTag(tag);
		}

		try {
			final Set<TaggedAASDescriptor> taggedAASDescriptors = enforceLookupTag(tag);

			return taggedAASDescriptors.stream().map(taggedAASDescriptor -> {
				try {
					return super.enforceLookupAAS(taggedAASDescriptor.getIdentifier());
				} catch (final InhibitException e) {
					// remove submodel descriptor if enforcement was unsuccessful
					logger.info(e.getMessage(), e);
				}
				return null;
			}).filter(Objects::nonNull).collect(Collectors.toSet())
					.stream()
					.filter(TaggedAASDescriptor.class::isInstance)
					.map(TaggedAASDescriptor.class::cast)
					.collect(Collectors.toSet());
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected Set<TaggedAASDescriptor> enforceLookupTag(final String tag) throws InhibitException {
		final Set<TaggedAASDescriptor> enforcedAASDescriptorsAfterLookupTag = taggedDirectoryAuthorizer.enforceLookupTag(
				subjectInformationProvider.get(),
				tag,
				() -> decoratedTaggedDirectory.lookupTag(tag)
		);

		final Set<TaggedAASDescriptor> enforcedAASDescriptorsAfterLookupAAS = enforcedAASDescriptorsAfterLookupTag.stream().map(enforcedAASDescriptor -> {
			try {
				return enforceLookupAAS(enforcedAASDescriptor.getIdentifier());
			} catch (final InhibitException e) {
				// remove submodel descriptor if enforcement was unsuccessful
				logger.info(e.getMessage(), e);
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList())
				.stream()
				.filter(TaggedAASDescriptor.class::isInstance)
				.map(TaggedAASDescriptor.class::cast)
				.collect(Collectors.toSet());

		return enforcedAASDescriptorsAfterLookupAAS.stream().map(enforcedAASDescriptor -> {
			final List<SubmodelDescriptor> submodelDescriptorsToRemove = enforcedAASDescriptor.getSubmodelDescriptors().stream().map(submodelDescriptor -> {
				final IIdentifier smId = submodelDescriptor.getIdentifier();
				try {
					return enforceLookupSubmodel(enforcedAASDescriptor.getIdentifier(), smId);
				} catch (final InhibitException e) {
					// remove submodel descriptor if enforcement was unsuccessful
					logger.info(e.getMessage(), e);
				}
				return null;
			}).filter(Objects::nonNull).collect(Collectors.toList());

			submodelDescriptorsToRemove.forEach(submodelDescriptor -> enforcedAASDescriptor.removeSubmodelDescriptor(submodelDescriptor.getIdentifier()));

			return enforcedAASDescriptor;
		}).collect(Collectors.toSet());
	}

	@Override
	public Set<TaggedAASDescriptor> lookupTags(final Set<String> tags) {
		if (CodeAuthentication.isCodeAuthentication()) {
			return decoratedTaggedDirectory.lookupTags(tags);
		}

		try {
			return enforceLookupTags(tags);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected Set<TaggedAASDescriptor> enforceLookupTags(final Set<String> tags) throws InhibitException {
		return tags.stream().map(tag -> {
			try {
				return enforceLookupTag(tag);
			} catch (final InhibitException e) {
				// remove submodel descriptor if enforcement was unsuccessful
				logger.info(e.getMessage(), e);
			}
			return null;
		}).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toSet());
	}
}
