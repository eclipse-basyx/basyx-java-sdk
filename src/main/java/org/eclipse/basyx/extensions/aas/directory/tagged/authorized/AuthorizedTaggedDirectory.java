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

import java.util.Set;

import org.eclipse.basyx.extensions.aas.directory.tagged.api.IAASTaggedDirectory;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedAASDescriptor;
import org.eclipse.basyx.extensions.aas.registration.authorization.AuthorizedAASRegistry;
import org.eclipse.basyx.extensions.shared.authorization.SecurityContextAuthorizer;

/**
 *
 * Implementation of {@link IAASTaggedDirectory} for restricting access to
 * sensitive data
 *
 * @author fried
 *
 */
public class AuthorizedTaggedDirectory extends AuthorizedAASRegistry implements IAASTaggedDirectory {

	private final SecurityContextAuthorizer authorizer = new SecurityContextAuthorizer();
	private IAASTaggedDirectory taggedDirectory;

	public AuthorizedTaggedDirectory(IAASTaggedDirectory taggedDirectory) {
		super(taggedDirectory);
		this.taggedDirectory = taggedDirectory;
	}

	@Override
	public void register(TaggedAASDescriptor descriptor) {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(AuthorizedAASRegistry.WRITE_AUTHORITY);
		taggedDirectory.register(descriptor);
	}

	@Override
	public Set<TaggedAASDescriptor> lookupTag(String tag) {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(AuthorizedAASRegistry.READ_AUTHORITY);
		return taggedDirectory.lookupTag(tag);
	}

	@Override
	public Set<TaggedAASDescriptor> lookupTags(Set<String> tags) {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(AuthorizedAASRegistry.READ_AUTHORITY);
		return taggedDirectory.lookupTags(tags);
	}

}
