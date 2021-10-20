/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
	public Security() {}

	/**
	 * Creates a Security object from a map
	 * 
	 * @param obj
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
