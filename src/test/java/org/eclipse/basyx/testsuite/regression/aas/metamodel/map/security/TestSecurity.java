/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.aas.metamodel.map.security;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.map.policypoints.AccessControlPolicyPoints;
import org.eclipse.basyx.aas.metamodel.map.security.Security;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.junit.Test;

/**
 * Tests constructor, setter and getter of {@link Security} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestSecurity {
	
	@Test
	public void testSetAccessControlPolicyPoints() {
		AccessControlPolicyPoints points = new AccessControlPolicyPoints();
		Security security = new Security();
		security.setAccessControlPolicyPoints(points);
		assertEquals(points, security.getAccessControlPolicyPoints());
	}
	
	@Test
	public void testGetRequiredCertificateExtension() {
		Reference reference = new Reference(new Key(KeyElements.ASSET, false, "testValue", IdentifierType.IRI));
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Security.REQUIREDCERTIFICATEEXTENSION, reference);
		Security security = Security.createAsFacade(map);
		assertEquals(reference, security.getRequiredCertificateExtension());
	} 
}
