/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
 * Tests constructor, setter and getter of {@link AccessControlPolicyPoints} for their
 * correctness
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
