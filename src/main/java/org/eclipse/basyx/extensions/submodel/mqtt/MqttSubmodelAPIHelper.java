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

import java.util.List;

import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.restapi.observing.ObservableSubmodelAPI;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

/**
 * A helper class containing string constants of topics used by the SubmodelAPI.
 * 
 * @author danish
 *
 */
public class MqttSubmodelAPIHelper {
	public static final String TOPIC_CREATESUBMODEL = "BaSyxSubmodel_createdSubmodel";
	public static final String TOPIC_ADDELEMENT = "BaSyxSubmodel_addedSubmodelElement";
	public static final String TOPIC_DELETEELEMENT = "BaSyxSubmodel_removedSubmodelElement";
	public static final String TOPIC_UPDATEELEMENT = "BaSyxSubmodel_updatedSubmodelElement";
	
	public static IIdentifier getSubmodelId(ObservableSubmodelAPI observedAPI) {
		ISubmodel submodel = observedAPI.getSubmodel();
		return submodel.getIdentification();
	}
	
	public static IIdentifier getAASId(ObservableSubmodelAPI observedAPI) {
		ISubmodel submodel = observedAPI.getSubmodel();
		IReference parentReference = submodel.getParent();
		if (parentReference != null) {
			List<IKey> keys = parentReference.getKeys();
			if (doesKeysExists(keys)) {
				return createIdentifier(keys);
			}
		}
		return null;
	}

	public static String createChangedSubmodelPayload(String submodelId) {
		return submodelId;
	}

	public static String createChangedSubmodelElementPayload(String aasId, String submodelId, String elementPart) {
		elementPart = VABPathTools.stripSlashes(elementPart);
		return "(" + aasId + "," + submodelId + "," + elementPart + ")";
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
