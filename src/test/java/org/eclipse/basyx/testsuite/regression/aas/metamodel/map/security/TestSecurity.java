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
package org.eclipse.basyx.testsuite.regression.aas.metamodel.map.security;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
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
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put(Security.REQUIREDCERTIFICATEEXTENSION, reference);
		Security security = Security.createAsFacade(map);
		assertEquals(reference, security.getRequiredCertificateExtension());
	} 
}
