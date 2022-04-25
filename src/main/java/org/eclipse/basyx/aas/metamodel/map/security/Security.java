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
package org.eclipse.basyx.aas.metamodel.map.security;

import java.util.Map;

import org.eclipse.basyx.aas.metamodel.api.policypoints.IAccessControlPolicyPoints;
import org.eclipse.basyx.aas.metamodel.api.security.ICertificate;
import org.eclipse.basyx.aas.metamodel.api.security.ISecurity;
import org.eclipse.basyx.aas.metamodel.map.policypoints.AccessControlPolicyPoints;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.vab.model.VABModelMap;

/**
 * KeyElements as defined in DAAS document
 * 
 * @author schnicke
 *
 */
public class Security extends VABModelMap<Object> implements ISecurity {
	public static final String ACCESSCONTROLPOLICYPOINTS = "accessControlPolicyPoints";
	public static final String CERTIFICATE = "certificate";
	public static final String REQUIREDCERTIFICATEEXTENSION = "requiredCertificateExtension";

	/**
	 * Constructor
	 */
	public Security() {
	}

	/**
	 * Creates a Security object from a map
	 * 
	 * @param map
	 *            a Security object as raw map
	 * @return a Security object, that behaves like a facade for the given map
	 */
	public static Security createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}

		Security ret = new Security();
		ret.setMap(map);
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IAccessControlPolicyPoints getAccessControlPolicyPoints() {
		return AccessControlPolicyPoints.createAsFacade((Map<String, Object>) get(Security.ACCESSCONTROLPOLICYPOINTS));
	}

	public void setAccessControlPolicyPoints(IAccessControlPolicyPoints obj) {
		put(Security.ACCESSCONTROLPOLICYPOINTS, obj);
	}

	@Override
	public ICertificate getCertificate() {
		// TODO: Implement
		throw new RuntimeException("Not implemented");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Reference getRequiredCertificateExtension() {
		return Reference.createAsFacade((Map<String, Object>) get(REQUIREDCERTIFICATEEXTENSION));
	}
}
