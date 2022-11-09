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
package org.eclipse.basyx.extensions.submodel.mqtt;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.StringJoiner;

import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.restapi.observing.ObservableSubmodelAPIV2;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

/**
 * A helper class containing string constants of topics used by the SubmodelAPI.
 * 
 * @author danish, siebert
 *
 */
public class MqttV2SubmodelAPIHelper {	
	private static final String AASREPOSITORY = "aas-repository";
	private static final String SHELLS = "shells";
	private static final String SUBMODELS = "submodels";
	private static final String SUBMODELELEMENTS = "submodelElements";
	private static final String CREATED = "created";
	private static final String UPDATED = "updated";
	private static final String DELETED = "deleted";
	private static final String VALUE = "value";
	
	public static String createCreateSubmodelElementTopic(String aasId, String submodelId, String idShortPath, String repoId) {
		idShortPath = VABPathTools.stripSlashes(idShortPath);
		
		return new StringJoiner("/", "/", "")
				.add(AASREPOSITORY)
				.add(repoId)
				.add(SHELLS)
				.add(encodeId(aasId))
				.add(SUBMODELS)
				.add(encodeId(submodelId))
				.add(SUBMODELELEMENTS)
				.add(idShortPath)
				.add(CREATED)
				.toString();
	}
	
	public static String createUpdateSubmodelElementTopic(String aasId, String submodelId, String idShortPath, String repoId) {
		idShortPath = VABPathTools.stripSlashes(idShortPath);
		
		return new StringJoiner("/", "/", "")
				.add(AASREPOSITORY)
				.add(repoId)
				.add(SHELLS)
				.add(encodeId(aasId))
				.add(SUBMODELS)
				.add(encodeId(submodelId))
				.add(SUBMODELELEMENTS)
				.add(idShortPath)
				.add(UPDATED)
				.toString();
	}
	
	public static String createDeleteSubmodelElementTopic(String aasId, String submodelId, String idShortPath, String repoId) {
		idShortPath = VABPathTools.stripSlashes(idShortPath);
		
		return new StringJoiner("/", "/", "")
				.add(AASREPOSITORY)
				.add(repoId)
				.add(SHELLS)
				.add(encodeId(aasId))
				.add(SUBMODELS)
				.add(encodeId(submodelId))
				.add(SUBMODELELEMENTS)
				.add(idShortPath)
				.add(DELETED)
				.toString();
	}
	
	public static String createSubmodelElementValueTopic(String aasId, String submodelId, String idShortPath, String repoId) {
		idShortPath = VABPathTools.stripSlashes(idShortPath);
		
		return new StringJoiner("/", "/", "")
				.add(AASREPOSITORY)
				.add(repoId)
				.add(SHELLS)
				.add(encodeId(aasId))
				.add(SUBMODELS)
				.add(encodeId(submodelId))
				.add(SUBMODELELEMENTS)
				.add(idShortPath)
				.add(VALUE)
				.toString();	
	}
	
	private static String encodeId(String id) {
		if (id == null) {
			return "<empty>";
		}
		
		return Base64.getUrlEncoder().withoutPadding().encodeToString(id.getBytes(StandardCharsets.UTF_8));
	}
	
	public static IIdentifier getSubmodelId(ObservableSubmodelAPIV2 observedAPI) {
		ISubmodel submodel = observedAPI.getSubmodel();
		return submodel.getIdentification();
	}
	
	public static IIdentifier getAASId(ObservableSubmodelAPIV2 observedAPI) {
		ISubmodel submodel = observedAPI.getSubmodel();
		IReference parentReference = submodel.getParent();
		if (parentReference != null) {
			List<IKey> keys = parentReference.getKeys();
			if (doesKeysExists(keys)) {
				return createIdentifier(keys);
			}
		}
		throw new RuntimeException("Could not find parent reference for the submodel '" + submodel.getIdShort() + "'");
	}

	private static boolean doesKeysExists(List<IKey> keys) {
		return keys != null && !keys.isEmpty();
	}
	
	private static IIdentifier createIdentifier(List<IKey> keys) {
		return new Identifier(IdentifierType.fromString(keys.get(0).getIdType().toString()), keys.get(0).getValue());
	}
	
	public static MqttConnectOptions getMqttConnectOptions(String username, char[] password) {
		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(username);
		options.setPassword(password);
		
		return options;
	}
}
