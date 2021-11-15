/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.registry.descriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.basyx.registry.descriptor.AASDescriptor;
import org.eclipse.basyx.registry.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.registry.descriptor.parts.Endpoint;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link AASDescriptor}
 *
 * @author fischer, jung
 *
 */
public class AASDescriptorSubmodels {

	private AASDescriptor shellDescriptor;
	private SubmodelDescriptor submodelDescriptor;

	@Before
	public void setUp() {
		shellDescriptor = new AASDescriptor(new Identifier(IdentifierType.CUSTOM, "Test"), new Endpoint("http://a.b/c/aas"));
		submodelDescriptor = new SubmodelDescriptor("SM1", new Identifier(IdentifierType.CUSTOM, "SM1"), new Endpoint("http://a.b/c/aas/submodels/SM1"));
		shellDescriptor.addSubmodelDescriptor(submodelDescriptor);
	}

	@Test
	public void addSubmodel() {
		SubmodelDescriptor newSubmodelDescriptor = new SubmodelDescriptor("SM2", new Identifier(IdentifierType.CUSTOM, "SM2"), new Endpoint("http://a.b/c/aas/submodels/SM2"));
		shellDescriptor.addSubmodelDescriptor(newSubmodelDescriptor);

		assertEquals(2, shellDescriptor.getSubmodelDescriptors().size());
		assertTrue(shellDescriptor.getSubmodelDescriptors().contains(newSubmodelDescriptor));
		assertTrue(shellDescriptor.getSubmodelDescriptors().contains(submodelDescriptor));
	}

	@Test
	public void removeSubmodelByIdentifier() {
		shellDescriptor.removeSubmodelDescriptor(submodelDescriptor.getIdentifier());
		assertEquals(0, shellDescriptor.getSubmodelDescriptors().size());
		assertTrue(!shellDescriptor.getSubmodelDescriptors().contains(submodelDescriptor));
	}

	@Test
	public void removeSubmodelByIdShort() {
		shellDescriptor.removeSubmodelDescriptor(submodelDescriptor.getIdShort());
		assertEquals(0, shellDescriptor.getSubmodelDescriptors().size());
		assertTrue(!shellDescriptor.getSubmodelDescriptors().contains(submodelDescriptor));
	}
}
