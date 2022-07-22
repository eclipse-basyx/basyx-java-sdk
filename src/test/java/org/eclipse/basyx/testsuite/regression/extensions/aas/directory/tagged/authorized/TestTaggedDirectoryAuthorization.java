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
import org.eclipse.basyx.extensions.aas.registration.authorization.AuthorizedAASRegistry;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.testsuite.regression.extensions.shared.mqtt.AuthorizationContextProvider;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests authorization implementation for the AASTaggedDirectory
 *
 * @author fried
 */
public class TestTaggedDirectoryAuthorization {

	private static IAASTaggedDirectory authorizedTaggedDirectory;

	private AuthorizationContextProvider securityContextProvider = new AuthorizationContextProvider(AuthorizedAASRegistry.READ_AUTHORITY, AuthorizedAASRegistry.WRITE_AUTHORITY);

	@BeforeClass
	public static void setUp() {
		IAASTaggedDirectory taggedDirectory = new MapTaggedDirectory(new HashedMap<>(), new HashedMap<>());
		authorizedTaggedDirectory = new AuthorizedTaggedDirectory(taggedDirectory);
	}

	@Test
	public void writeAction_securityContextWithWriteAuthority() {
		securityContextProvider.setSecurityContextWithWriteAuthority();
		TaggedAASDescriptor descriptor = createTestDescriptor();

		authorizedTaggedDirectory.register(descriptor);
	}

	@Test(expected = ProviderException.class)
	public void writeAction_emptySecurityContextThrowsError() {
		securityContextProvider.setEmptySecurityContext();
		TaggedAASDescriptor descriptor = createTestDescriptor();
		authorizedTaggedDirectory.register(descriptor);
	}

	@Test
	public void readAction_securityContextWithReadAuthority() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		authorizedTaggedDirectory.lookupAll();
	}

	@Test(expected = ProviderException.class)
	public void readAction_emptySecurityContextThrowsError() {
		securityContextProvider.setEmptySecurityContext();
		authorizedTaggedDirectory.lookupAll();
	}

	@Test(expected = ProviderException.class)
	public void readAction_LookupTagEmptySecurityContextThrowsError() {
		securityContextProvider.setEmptySecurityContext();
		authorizedTaggedDirectory.lookupTag("test");
	}

	@Test
	public void readAction_LookupTagWithReadAuthority() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		authorizedTaggedDirectory.lookupTag("test");
	}

	@Test(expected = ProviderException.class)
	public void readAction_LookupTagsEmptySecurityContextThrowsError() {
		securityContextProvider.setEmptySecurityContext();
		authorizedTaggedDirectory.lookupTags(new HashSet<String>());
	}

	@Test
	public void readAction_LookupTagsWithReadAuthority() {
		securityContextProvider.setSecurityContextWithReadAuthority();
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
