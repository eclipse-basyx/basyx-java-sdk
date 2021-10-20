/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.metamodel.api.security;

import org.eclipse.basyx.aas.metamodel.api.policypoints.IAccessControlPolicyPoints;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;

/**
 * Container for security relevant information of the AAS.
 * 
 * @author rajashek, schnicke
 *
 */
public interface ISecurity {
	/**
	 * Gets the access control policy points of the AAS.
	 * 
	 * @return
	 */
	IAccessControlPolicyPoints getAccessControlPolicyPoints();

	/**
	 * Gets the certificates of the AAS.
	 * 
	 * @return
	 */
	ICertificate getCertificate();

	/**
	 * Gets the certificate extensions as required by the AAS
	 * 
	 * @return
	 */
	Reference getRequiredCertificateExtension();
}
