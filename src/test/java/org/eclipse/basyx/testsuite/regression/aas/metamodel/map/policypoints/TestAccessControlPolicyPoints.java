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
package org.eclipse.basyx.testsuite.regression.aas.metamodel.map.policypoints;

import static org.junit.Assert.assertEquals;

import org.eclipse.basyx.aas.metamodel.map.policypoints.AccessControlPolicyPoints;
import org.eclipse.basyx.aas.metamodel.map.policypoints.PolicyAdministrationPoint;
import org.eclipse.basyx.aas.metamodel.map.policypoints.PolicyDecisionPoint;
import org.eclipse.basyx.aas.metamodel.map.policypoints.PolicyEnforcementPoint;
import org.eclipse.basyx.aas.metamodel.map.policypoints.PolicyInformationPoints;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests constructor, setter and getter of {@link AccessControlPolicyPoints} for
 * their correctness
 * 
 * @author haque
 *
 */
public class TestAccessControlPolicyPoints {
	private AccessControlPolicyPoints points;

	@Before
	public void buildObject() {
		points = new AccessControlPolicyPoints();
	}

	@Test
	public void testSetPolicyAdministrationPoint() {
		PolicyAdministrationPoint policyAdministrationPoint = new PolicyAdministrationPoint();
		points.setPolicyAdministrationPoint(policyAdministrationPoint);
		assertEquals(policyAdministrationPoint, points.getPolicyAdministrationPoint());
	}

	@Test
	public void testPolicyDecisionPoint() {
		PolicyDecisionPoint policyDecisionPoint = new PolicyDecisionPoint();
		points.setPolicyDecisionPoint(policyDecisionPoint);
		assertEquals(policyDecisionPoint, points.getPolicyDecisionPoint());
	}

	@Test
	public void testPolicyEnforcementPoint() {
		PolicyEnforcementPoint policyEnforcementPoint = new PolicyEnforcementPoint();
		points.setPolicyEnforcementPoint(policyEnforcementPoint);
		assertEquals(policyEnforcementPoint, points.getPolicyEnforcementPoint());
	}

	@Test
	public void testPolicyInformationPoints() {
		PolicyInformationPoints policyInformationPoints = new PolicyInformationPoints();
		points.setPolicyInformationPoints(policyInformationPoints);
		assertEquals(policyInformationPoints, points.getPolicyInformationPoints());
	}
}
