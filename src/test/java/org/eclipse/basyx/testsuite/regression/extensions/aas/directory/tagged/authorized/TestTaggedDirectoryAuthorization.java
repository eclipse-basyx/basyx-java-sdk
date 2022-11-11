/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.extensions.aas.directory.tagged.authorized;

import java.util.HashSet;
import org.apache.commons.collections4.map.HashedMap;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.IAASTaggedDirectory;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedAASDescriptor;
import org.eclipse.basyx.extensions.aas.directory.tagged.authorized.AuthorizedTaggedDirectory;
import org.eclipse.basyx.extensions.aas.directory.tagged.map.MapTaggedDirectory;
import org.eclipse.basyx.extensions.aas.registration.authorization.AASRegistryScopes;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Tests authorization implementation for the AASTaggedDirectory
 *
 * @author fried
 */
public class TestTaggedDirectoryAuthorization {
	private static IAASTaggedDirectory authorizedTaggedDirectory;

	private SecurityContext _getSecurityContextWithAuthorities(final String... authorities) {
		final SecurityContext context = SecurityContextHolder.createEmptyContext();
		final Authentication authentication = new TestingAuthenticationToken(null, null, authorities);
		context.setAuthentication(authentication);
		return context;
	}

	private SecurityContext getEmptySecurityContext() {
		return SecurityContextHolder.createEmptyContext();
	}

	private SecurityContext getSecurityContextWithoutAuthorities() {
		return _getSecurityContextWithAuthorities();
	}

	private SecurityContext getSecurityContextWithReadAuthority() {
		return _getSecurityContextWithAuthorities("SCOPE_" + AASRegistryScopes.READ_SCOPE);
	}

	private SecurityContext getSecurityContextWithWriteAuthority() {
		return _getSecurityContextWithAuthorities("SCOPE_" + AASRegistryScopes.WRITE_SCOPE);
	}

	@BeforeClass
	public static void setUp() {
		IAASTaggedDirectory taggedDirectory = new MapTaggedDirectory(new HashedMap<>(), new HashedMap<>());
		authorizedTaggedDirectory = new AuthorizedTaggedDirectory(taggedDirectory);
	}

	@Test
	public void writeAction_securityContextWithWriteAuthority() {
		SecurityContextHolder.setContext(getSecurityContextWithWriteAuthority());
		TaggedAASDescriptor descriptor = createTestDescriptor();

		authorizedTaggedDirectory.register(descriptor);
	}

	@Test(expected = ProviderException.class)
	public void writeAction_emptySecurityContextThrowsError() {
		SecurityContextHolder.setContext(getEmptySecurityContext());
		TaggedAASDescriptor descriptor = createTestDescriptor();
		authorizedTaggedDirectory.register(descriptor);
	}

	@Test
	public void readAction_securityContextWithReadAuthority() {
		SecurityContextHolder.setContext(getSecurityContextWithReadAuthority());
		authorizedTaggedDirectory.lookupAll();
	}

	@Test(expected = ProviderException.class)
	public void readAction_emptySecurityContextThrowsError() {
		SecurityContextHolder.setContext(getEmptySecurityContext());
		authorizedTaggedDirectory.lookupAll();
	}

	@Test(expected = ProviderException.class)
	public void readAction_LookupTagEmptySecurityContextThrowsError() {
		SecurityContextHolder.setContext(getEmptySecurityContext());
		authorizedTaggedDirectory.lookupTag("test");
	}

	@Test
	public void readAction_LookupTagWithReadAuthority() {
		SecurityContextHolder.setContext(getSecurityContextWithReadAuthority());
		authorizedTaggedDirectory.lookupTag("test");
	}

	@Test(expected = ProviderException.class)
	public void readAction_LookupTagsEmptySecurityContextThrowsError() {
		SecurityContextHolder.setContext(getEmptySecurityContext());
		authorizedTaggedDirectory.lookupTags(new HashSet<String>());
	}

	@Test
	public void readAction_LookupTagsWithReadAuthority() {
		SecurityContextHolder.setContext(getSecurityContextWithReadAuthority());
		authorizedTaggedDirectory.lookupTags(new HashSet<String>());
	}

	private TaggedAASDescriptor createTestDescriptor() {
		Identifier shellIdentifier = new Identifier(IdentifierType.CUSTOM, "testIdentifier");
		String shellIdShort = "testIDShort";

		Asset shellAsset = new Asset("testAssetIdShort", new Identifier(IdentifierType.CUSTOM, "assetTestIdentifier"), AssetKind.INSTANCE);

		AssetAdministrationShell assetAdministrationShell = new AssetAdministrationShell(shellIdShort, shellIdentifier, shellAsset);

		TaggedAASDescriptor descriptor = new TaggedAASDescriptor(assetAdministrationShell, "http://testEndpoint.test/testEndpoint");
		return descriptor;
	}

}
