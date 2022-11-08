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
package org.eclipse.basyx.extensions.aas.registration.mqtt;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.StringJoiner;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * A helper class containing method and string constants of topics used by the
 * AASRegistry.
 * 
 * @author danish
 *
 */
public class MqttV2AASRegistryHelper {
	private static final String AASREGISTRY = "aas-registry";
	private static final String SHELLS = "shells";
	private static final String SUBMODELS = "submodels";
	private static final String CREATED = "created";
	private static final String UPDATED = "updated";
	private static final String DELETED = "deleted";

	/**
	 * This function is for creating a message payload representing a descriptor
	 * change of a submodel of a specific AAS.
	 * 
	 * @param aasId
	 * @param smId
	 */
	public static String createSubmodelDescriptorOfAASChangedPayload(IIdentifier aasId, IIdentifier smId) {
		return "(" + aasId.getId() + "," + smId.getId() + ")";
	}
	
	/**
	 * This method creates a Mqtt topic like /aas-registry/default/shells/created
	 * 
	 */
	public static String createCreateAASTopic(String registryId) {
		return new StringJoiner("/", "/", "")
				.add(AASREGISTRY)
				.add(registryId)
				.add(SHELLS)
				.add(CREATED)
				.toString();		
	}
	
	/**
	 * This method creates a Mqtt topic like /aas-registry/default/submodels/created
	 * 
	 */
	public static String createCreateSubmodelTopic(String registryId) {
		return new StringJoiner("/", "/", "")
				.add(AASREGISTRY)
				.add(registryId)
				.add(SUBMODELS)
				.add(CREATED)
				.toString();
	}

	/**
	 * This method creates a Mqtt topic like /aas-registry/default/shells/[encoded aasId]/submodels/created
	 * 
	 */
	public static String createCreateSubmodelTopicWithAASId(String aasId, String registryId) {
		return new StringJoiner("/", "/", "")
				.add(AASREGISTRY)
				.add(registryId)
				.add(SHELLS)
				.add(encodeAASId(aasId))
				.add(SUBMODELS)
				.add(CREATED)
				.toString();
	}

	/**
	 * This method creates a Mqtt topic like /aas-registry/default/shells/updated
	 * 
	 */
	public static String createUpdateAASTopic(String registryId) {
		return new StringJoiner("/", "/", "")
				.add(AASREGISTRY)
				.add(registryId)
				.add(SHELLS)
				.add(UPDATED)
				.toString();
	}
	
	/**
	 * This method creates a Mqtt topic like /aas-registry/default/submodels/updated
	 * 
	 */
	public static String createUpdateSubmodelTopic(String registryId) {
		return new StringJoiner("/", "/", "")
				.add(AASREGISTRY)
				.add(registryId)
				.add(SUBMODELS)
				.add(UPDATED)
				.toString();
	}
	
	/**
	 * This method creates a Mqtt topic like /aas-registry/default/shells/[encoded aasId]/submodels/updated
	 * 
	 */
	public static String createUpdateSubmodelTopicWithAASId(String aasId, String registryId) {
		return new StringJoiner("/", "/", "")
				.add(AASREGISTRY)
				.add(registryId)
				.add(SHELLS)
				.add(encodeAASId(aasId))
				.add(SUBMODELS)
				.add(UPDATED)
				.toString();
	}
	
	/**
	 * This method creates a Mqtt topic like /aas-registry/default/shells/deleted
	 * 
	 */
	public static String createDeleteAASTopic(String registryId) {
		return new StringJoiner("/", "/", "")
				.add(AASREGISTRY)
				.add(registryId)
				.add(SHELLS)
				.add(DELETED)
				.toString();
	}
	
	/**
	 * This method creates a Mqtt topic like /aas-registry/default/submodels/deleted
	 * 
	 */
	public static String createDeleteSubmodelTopic(String registryId) {
		return new StringJoiner("/", "/", "")
				.add(AASREGISTRY)
				.add(registryId)
				.add(SUBMODELS)
				.add(DELETED)
				.toString();
	}
	
	/**
	 * This method creates a Mqtt topic like /aas-registry/default/shells/[encoded aasId]/submodels/deleted
	 * 
	 */
	public static String createDeleteSubmodelTopicWithAASId(String aasId, String registryId) {
		return new StringJoiner("/", "/", "")
				.add(AASREGISTRY)
				.add(registryId)
				.add(SHELLS)
				.add(encodeAASId(aasId))
				.add(SUBMODELS)
				.add(DELETED)
				.toString();
	}
	
	private static String encodeAASId(String aasId) {
		return Base64.getUrlEncoder().withoutPadding().encodeToString(aasId.getBytes(StandardCharsets.UTF_8));
	}
}
