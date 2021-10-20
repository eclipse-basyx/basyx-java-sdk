/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.metamodel.api.policypoints;

/**
 * Container for access control policy points.
 * 
 * @author rajashek, schnicke
 *
 */
public interface IAccessControlPolicyPoints {
	/**
	 * Gets the access control administration policy point of the AAS.
	 * 
	 * @return
	 */
	public Object getPolicyAdministrationPoint();

	/**
	 * Gets the access control policy decision point of the AAS.
	 * 
	 * @return
	 */
	public Object getPolicyDecisionPoint();

	/**
	 * Gets the access control policy enforcement point of the AAS.
	 * 
	 * @return
	 */
	public Object getPolicyEnforcementPoint();

	/**
	 * Gets the access control policy information points of the AAS.
	 * 
	 * @return
	 */
	public Object getPolicyInformationPoints();
}
