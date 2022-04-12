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
package org.eclipse.basyx.aas.metamodel.map.policypoints;

import java.util.Map;

import org.eclipse.basyx.aas.metamodel.api.policypoints.IAccessControlPolicyPoints;
import org.eclipse.basyx.vab.model.VABModelMap;

/**
 * Security class
 * 
 * @author elsheikh
 *
 */
public class AccessControlPolicyPoints extends VABModelMap<Object> implements IAccessControlPolicyPoints {
	public static final String POLICYADMINISTRATIONPOINT = "policyAdministrationPoint";
	public static final String POLICYDECISIONPOINT = "policyDecisionPoint";
	public static final String POLICYENFORECEMENTPOINT = "policyEnforcementPoint";
	public static final String POLICYINFORMATIONPOINTS = "policyInformationPoints";

	/**
	 * Constructor
	 */
	public AccessControlPolicyPoints() {}

	/**
	 * Creates a DataSpecificationIEC61360 object from a map
	 * 
	 * @param map
	 *            a DataSpecificationIEC61360 object as raw map
	 * @return a DataSpecificationIEC61360 object, that behaves like a facade for
	 *         the given map
	 */
	public static AccessControlPolicyPoints createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}

		AccessControlPolicyPoints ret = new AccessControlPolicyPoints();
		ret.setMap(map);
		return ret;
	}

	public void setPolicyAdministrationPoint(Object obj) {
		put(AccessControlPolicyPoints.POLICYADMINISTRATIONPOINT, obj);
	}

	@Override
	public Object getPolicyAdministrationPoint() {
		return get(AccessControlPolicyPoints.POLICYADMINISTRATIONPOINT);
	}

	public void setPolicyDecisionPoint(Object obj) {
		put(AccessControlPolicyPoints.POLICYDECISIONPOINT, obj);
	}

	@Override
	public Object getPolicyDecisionPoint() {
		return get(AccessControlPolicyPoints.POLICYDECISIONPOINT);
	}

	public void setPolicyEnforcementPoint(Object obj) {
		put(AccessControlPolicyPoints.POLICYENFORECEMENTPOINT, obj);
	}

	@Override
	public Object getPolicyEnforcementPoint() {
		return get(AccessControlPolicyPoints.POLICYENFORECEMENTPOINT);
	}

	public void setPolicyInformationPoints(Object obj) {
		put(AccessControlPolicyPoints.POLICYINFORMATIONPOINTS, obj);
	}

	@Override
	public Object getPolicyInformationPoints() {
		return get(AccessControlPolicyPoints.POLICYINFORMATIONPOINTS);
	}

}
