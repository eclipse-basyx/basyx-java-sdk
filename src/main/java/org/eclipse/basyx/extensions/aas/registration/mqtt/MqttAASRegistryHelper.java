/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.registration.mqtt;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * A helper class containing method and string constants of topics used by the AASRegistry.
 * 
 * @author danish
 *
 */
public class MqttAASRegistryHelper {
	public static final String TOPIC_REGISTERAAS = "BaSyxRegistry_registeredAAS";
	public static final String TOPIC_REGISTERSUBMODEL = "BaSyxRegistry_registeredSubmodel";
	public static final String TOPIC_DELETEAAS = "BaSyxRegistry_deletedAAS";
	public static final String TOPIC_DELETESUBMODEL = "BaSyxRegistry_deletedSubmodel";
	
	/**
	 * This function is for creating a message payload representing a descriptor change of a submodel of a specific AAS.
	 * @param aasId
	 * @param smId
	 */
	public static String createSubmodelDescriptorOfAASChangedPayload(IIdentifier aasId, IIdentifier smId) {
		return "(" + aasId.getId() + "," + smId.getId() + ")";
	}
}
